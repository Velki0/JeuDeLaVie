package org.fr.entites;

import java.util.ArrayList;
import java.util.List;

public class Automate {

    private final int lignesTotales;
    private final int colonnesTotales;
    private final List<List<Cellule>> cellules;

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

    protected Automate(List<List<Boolean>> modele) {

        this.lignesTotales = modele.size();
        this.colonnesTotales = modele.getFirst().size();
        cellules = new ArrayList<>();
        List<Cellule> ligneCellules;
        for (int x = 0; x < lignesTotales; x++) {
            ligneCellules = new ArrayList<>();
            for (int y = 0; y < colonnesTotales; y++) {
                ligneCellules.add(new Cellule(modele.get(x).get(y)));
            }
            cellules.add(ligneCellules);
        }

    }

    private boolean getValeurCellule(int ligne, int colonne) {

        // Obtention de l'état d'une cellule, si elle est hors champ, la méthode renvoie "false"
        if (ligne >= 0 && colonne >= 0 && ligne < lignesTotales && colonne < colonnesTotales) {
            return cellules.get(ligne).get(colonne).getEnVie();
        } else {
            return false;
        }

    }

    private int compterVoisins(int ligne, int colonne) {

        // Renvoie le nombre de cellules vivantes dans un carré de 9 x 9 centré sur la cellule renseignée
        int compteurVoisins = 0;
        for (int x = ligne - 1; x <= ligne + 1; x++) {
            for (int y = colonne - 1; y <= colonne + 1; y++) {
                if (getValeurCellule(x, y)) {
                    compteurVoisins++;
                }
            }
        }
        // On élimine la valeur de la case ciblée si elle était vivante
        if (getValeurCellule(ligne, colonne)) {
            compteurVoisins--;
        }
        return compteurVoisins;

    }

    protected void mettreAJourEtatAutomate() {

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
        for (int x = 0; x < lignesTotales; x++) {
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).get(y).setEnVie(cellules.get(x).get(y).getProchainEtat());
            }
        }

    }

    protected void reinitialiserAutomate() {

        for (int x = 0; x < lignesTotales; x++) {
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).get(y).setEnVie(false);
            }
        }

    }

    protected int getLignesTotales() { return lignesTotales; }
    protected int getColonnesTotales() { return colonnesTotales; }
    protected List<List<Cellule>> getCellules() { return cellules; }

}