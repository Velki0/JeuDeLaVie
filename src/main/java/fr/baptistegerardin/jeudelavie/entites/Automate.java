package fr.baptistegerardin.jeudelavie.entites;

import java.util.ArrayList;
import java.util.List;

/**
 * Un Automate cellulaire est un ensemble de cellules possédant une taille (en lignes et colonnes) maximale fixée.
 * Il est défini suivant la définition du Jeu de la Vie de Conway et suit la règle standard B3/S23.
 * @author Velki0
 * @version 1.0
 */
public class Automate {

    /** Nombre de lignes totales de l'automate. */
    private final int lignesTotales;
    /** Nombre de colonnes totales de l'automate. */
    private final int colonnesTotales;
    /** Tableau à deux dimensions de Cellules définissant l'automate cellulaire prévu par le Jeu de la Vie de Conway.  */
    private final List<List<Cellule>> cellules;

    /**
     * Constructeur d'un automate avec des attributs cellulaires aléatoires.
     * Nécessite deux paramètres définissant la taille de l'objet.
     * @param lignesTotales Nombre de lignes totales de l'automate.
     * @param colonnesTotales Nombre de colonnes totales de l'automate.
     */
    protected Automate(int lignesTotales, int colonnesTotales) {

        this.lignesTotales = lignesTotales;
        this.colonnesTotales = colonnesTotales;
        cellules = new ArrayList<>();
        for (int x = 0; x < lignesTotales; x++) {
            cellules.add(new ArrayList<>());
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).add(new Cellule());
            }
        }

    }

    /**
     * Constructeur d'un automate permettant de fixer l'état initial de chaque cellule de façon uniforme.
     * Nécessite deux paramètres définissant la taille de l'objet et d'une valeur booléenne de départ.
     * @param lignesTotales Nombre de lignes totales de l'automate.
     * @param colonnesTotales Nombre de colonnes totales de l'automate.
     * @param etatInitial État initial de toutes les cellules de l'automate.
     */
    protected Automate(int lignesTotales, int colonnesTotales, boolean etatInitial) {

        this.lignesTotales = lignesTotales;
        this.colonnesTotales = colonnesTotales;
        cellules = new ArrayList<>();
        for (int x = 0; x < lignesTotales; x++) {
            cellules.add(new ArrayList<>());
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).add(new Cellule(etatInitial));
            }
        }

    }

    /**
     * Constructeur d'un automate via le chargement d'un modèle prédéfini.
     * Nécessite toutes les informations de la grille de départ contenu dans le paramètre 'modele'.
     * @param modele Modèle de l'automate cellulaire.
     */
    protected Automate(Modele modele) {

        this.lignesTotales = modele.getLignesTotales();
        this.colonnesTotales = modele.getColonnesTotales();
        cellules = new ArrayList<>();
        List<Cellule> ligneCellules;
        for (int x = 0; x < lignesTotales; x++) {
            ligneCellules = new ArrayList<>();
            for (int y = 0; y < colonnesTotales; y++) {
                ligneCellules.add(new Cellule(modele.getModeleNormaliser().get(x).get(y)));
            }
            cellules.add(ligneCellules);
        }

    }

    /**
     * Méthode pour l'obtention de l'état actuel d'une cellule spécifiée par son identifiant de ligne et de colonne.
     * Il est à noter que si les valeurs de localisation définisse un objet hors grille, la méthode renvoie false.
     * @param ligne Numéro de ligne de la cellule.
     * @param colonne Numéro de colonne de la cellule.
     * @return Une valeur booléenne suivant l'état actuel de la cellule. Revoie 'false' si la cellule est hors-champ.
     */
    private boolean getValeurCellule(int ligne, int colonne) {

        if (ligne >= 0 && colonne >= 0 && ligne < lignesTotales && colonne < colonnesTotales) {
            return cellules.get(ligne).get(colonne).getEnVie();
        } else {
            return false;
        }

    }

    /**
     * Méthode permettant d'obtenir le nombre de cellules voisines en vie pour une cellule spécifiée.
     * @param ligne Numéro de ligne de la cellule.
     * @param colonne Numéro de colonne de la cellule.
     * @return Le nombre de cellules voisines en vie.
     */
    private int compterVoisins(int ligne, int colonne) {

        // Renvoie le nombre de cellules vivantes dans un carré de 9 x 9 centré sur la cellule renseignée.
        int compteurVoisins = 0;
        for (int x = ligne - 1; x <= ligne + 1; x++) {
            for (int y = colonne - 1; y <= colonne + 1; y++) {
                if (getValeurCellule(x, y)) {
                    compteurVoisins++;
                }
            }
        }
        // On élimine la valeur de la case ciblée si elle était vivante.
        if (getValeurCellule(ligne, colonne)) {
            compteurVoisins--;
        }
        return compteurVoisins;

    }

    /**
     * Méthode d'incrémentation de l'automate vers sa prochaine génération.
     */
    protected void mettreAJourAutomate() {

        // Prépare pour chaque cellule son prochain état suivant leur nombre de voisins en vie.
        for (int x = 0; x < lignesTotales; x++) {
            for (int y = 0; y < colonnesTotales; y++) {
                switch (compterVoisins(x, y)) {
                    // Survie à 2 voisins
                    case 2:
                        if (getValeurCellule(x, y)) {
                            cellules.get(x).get(y).setProchainEtat(true);
                        }
                        break;
                    // Survie à 2 voisins ou naissance
                    case 3:
                        cellules.get(x).get(y).setProchainEtat(true);
                        break;
                    // Mort dans tous les autres cas
                    default:
                        cellules.get(x).get(y).setProchainEtat(false);
                        break;
                }
            }
        }
        // Fixe la nouvelle valeur 'en vie' pour chaque cellule.
        for (int x = 0; x < lignesTotales; x++) {
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).get(y).setEnVie(cellules.get(x).get(y).getProchainEtat());
            }
        }

    }

    /**
     * Méthode permettant à l'automate d'effacer entièrement son contenu et de le remplacer par des valeurs 'false' uniquement.
     */
    protected void reinitialiserAutomate() {

        for (int x = 0; x < lignesTotales; x++) {
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).get(y).setEnVie(false);
            }
        }

    }

    /**
     * Getter pour récupérer le nombre de lignes totales de l'automate.
     * @return Le nombre total de lignes de l'automate.
     */
    protected int getLignesTotales() { return lignesTotales; }

    /**
     * Getter pour récupérer le nombre de colonnes totales de l'automate.
     * @return Le nombre total de colonnes de l'automate.
     */
    protected int getColonnesTotales() { return colonnesTotales; }

    /**
     * Getter pour renvoyer le tableau de cellule entier de l'automate.
     * @return Le tableau de cellules de l'automate.
     */
    protected List<List<Cellule>> getCellules() { return cellules; }

}
