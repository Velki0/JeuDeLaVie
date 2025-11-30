package fr.baptistegerardin.jeudelavie.entites;

import fr.baptistegerardin.jeudelavie.exceptions.ModeleRenseigneNonConforme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.Transient;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Le PlateauDeJeu est une entité graphique représentant la vie de l'automate.
 * Il est composé de l'automate, du numéro de la génération en cours et de d'autres informations graphiques comme :
 * - Une grille de pixels.
 * - Une taille de pixels.
 * - Une vitesse d'actualisation de la grille.
 * @author Velki0
 * @version 1.0
 */
public class PlateauDeJeu extends JPanel implements Runnable {

    /** Automate cellulaire du plateau. */
    private Automate automate;
    /** Grille permettant l'affichage graphique. */
    private boolean[][] grille;
    /** Numéro de la génération en cours. */
    private int generation;
    /** Taille des cellules en nombre de pixels pour l'affichage. */
    private int tailleCellules;
    /** Vitesse d'actualisation à laquelle le jeu évolue. */
    private int vitesseActualisation;

    /**
     * Constructeur permettant l'initialisation d'une nouvelle grille graphique du Jeu de la Vie.
     * Cette grille est par défaut fixée avec une taille de 80 par 80, une taille de cellules de cinq pixels et d'une vitesse d'actualisation de 50ms chaque nouvelle génération.
     */
    protected PlateauDeJeu() {

        this.automate = new Automate(80, 80, false);
        this.grille = new boolean[80][80];
        generation = 0;
        tailleCellules = 5;
        initialiserGrille();
        vitesseActualisation = 50;

    }

    /**
     * Méthode d'initialisation de la grille ou chaque élément graphique prend la valeur de la cellule de l'automate qui lui correspond.
     */
    private void initialiserGrille() {

        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                grille[x][y] = automate.getCellules().get(x).get(y).getEnVie() ;
            }
        }
        JeuDeLaVie.setGenerationLabel("Génération : " + generation);
        repaint();

    }

    /**
     * Méthode de réinitialisation du plateau de jeu pour obtenir une grille vierge.
     */
    protected void reinitialiserGrille() {

        automate.reinitialiserAutomate();
        generation = 0;
        initialiserGrille();

    }

    /**
     * Méthode pour l'incrémentation d'une génération et la mise à jour du plateau de jeu.
     */
    private void mettreAJourGrille() {

        automate.mettreAJourAutomate();
        initialiserGrille();
        generation++;
        JeuDeLaVie.setGenerationLabel("Génération : " + generation);

    }

    /**
     * Méthode visant à réarranger le plateau de jeu avec une nouvelle taille défini.
     * Une grille vierge est initialisée.
     * @param lignesTotales Nouveau nombre total de lignes.
     * @param colonnesTotales Nouveau nombre total de colonnes.
     */
    protected void rearrangerGrille(int lignesTotales, int colonnesTotales) {

        grille = new boolean[lignesTotales][colonnesTotales];
        automate = new Automate(lignesTotales, colonnesTotales, false);
        generation = 0;
        revalidate();
        initialiserGrille();

    }

    /**
     * Méthode pour charger un plateau de jeu à partir d'un chemin d'accès vers un fichier .rle ou .txt renseigné par l'utilisateur.
     * @param chemin Chemin d'accès au fichier du modèle.
     * @throws IOException Exception jetée si le chemin renvoie sur un fichier introuvable ou non-existant.
     * @throws ModeleRenseigneNonConforme Exception jetée si le fichier renseigné ne possède pas des attributs conformes de lignes et colonnes.
     */
    protected void chargerModele(Path chemin) throws IOException, ModeleRenseigneNonConforme {

        reinitialiserGrille();
        this.automate = new Automate(new Modele(chemin));
        this.grille = new boolean[automate.getLignesTotales()][automate.getColonnesTotales()];
        generation = 0;
        revalidate();
        initialiserGrille();

    }

    /**
     * Méthode redéfinissant le plateau de jeu avec une grille entièrement aléatoire.
     */
    protected void autoRemplissage() {

        automate = new Automate(grille.length, grille[0].length);
        generation = 0;
        initialiserGrille();

    }

    /**
     * Méthode permettant de récupérer la hauteur du plateau de jeu.
     * @return La hauteur du plateau de jeu en nombre de cellules.
     */
    protected int getHauteurPlateau() { return grille.length; }

    /**
     * Méthode permettant de récupérer la largeur du plateau de jeu.
     * @return La largeur du plateau de jeu en nombre de cellules.
     */
    protected int getLargeurPlateau() { return grille[0].length; }

    /**
     * Getter afin de récupérer la taille prévu pour l'affichage de chaque cellule.
     * @return La taille des cellules en nombres de pixels.
     */
    protected int getTailleCellules() { return tailleCellules; }

    /**
     * Getter afin de récupérer la vitesse actuelle d'actualisation du plateau de jeu.
     * @return La vitesse d'actualisation en millisecondes (ms).
     */
    protected int getVitesseActualisation() { return vitesseActualisation; }

    /**
     * Setter pour fixer la nouvelle hauteur et largeur des cellules renseignées par l'utilisateur.
     * @param tailleEnPixels Nouvelle taille d'une cellule comprise entre 1x1 et 10x10 pixels.
     */
    protected void setTailleCellules(int tailleEnPixels) {

        this.tailleCellules = tailleEnPixels;
        revalidate();
        repaint();

    }

    /**
     * Setter pour fixer la nouvelle vitesse d'actualisation du plateau de jeu.
     * @param vitesseActualisation Nouvelle vitesse d'actualisation en millisecondes (ms).
     */
    protected void setVitesseActualisation(int vitesseActualisation) { this.vitesseActualisation = vitesseActualisation; }

    @Override
    @Transient
    public Dimension getPreferredSize() {

        return new Dimension(grille[0].length * tailleCellules, grille.length * tailleCellules);

    }

    @Override
    protected void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Color graphColor = graph.getColor();
        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                if (grille[x][y]) {
                    graph.setColor(Color.red);
                    graph.fillRect(y * tailleCellules, x * tailleCellules, tailleCellules, tailleCellules);
                }
            }
        }
        graph.setColor(graphColor);

    }

    /**
     * Boucle d'actualisation du plateau de jeu.
     * Chaque nouvelle génération est affichée avec un délai prévu par l'attribut 'vitesseActualisation'.
     */
    @Override
    @SuppressWarnings("BusyWait")
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            mettreAJourGrille();
            try {
                Thread.sleep(vitesseActualisation);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

}
