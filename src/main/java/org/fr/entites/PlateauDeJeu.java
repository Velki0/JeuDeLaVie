package org.fr.entites;

import javax.swing.*;
import java.awt.*;
import java.beans.Transient;
import java.io.IOException;
import java.nio.file.Path;

public class PlateauDeJeu extends JPanel implements Runnable {

    private Automate automate;
    private boolean[][] grille;
    private int generation;
    private final JLabel generationLabel;
    private int vitesseActualisation;

    protected PlateauDeJeu() {

        this.automate = new Automate(50, 50, false);
        this.grille = new boolean[50][50];
        generation = 0;
        generationLabel = new JLabel();
        initialiserGrille();
        add(generationLabel);
        vitesseActualisation = 50;

    }

    private void initialiserGrille() {

        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                grille[x][y] = automate.getCellules().get(x).get(y).getEnVie() ;
            }
        }
        generationLabel.setText("Génération : " + generation);
        repaint();

    }

    protected void reinitialiserGrille() {

        automate.reinitialiserAutomate();
        generation = 0;
        initialiserGrille();

    }

    private void mettreAJourGrille() {

        automate.mettreAJourEtatAutomate();
        initialiserGrille();
        generation++;
        generationLabel.setText("Génération : " + generation);

    }

    protected void rearrangerGrille(int lignesTotales, int colonnesTotales) {

        grille = new boolean[lignesTotales][colonnesTotales];
        automate = new Automate(lignesTotales, colonnesTotales, false);
        generation = 0;
        initialiserGrille();

    }

    protected void chargerModele(Path chemin) throws IOException {

        reinitialiserGrille();
        this.automate = new Automate(new Modele(chemin).getModeleNormaliser());
        this.grille = new boolean[automate.getLignesTotales()][automate.getColonnesTotales()];
        generation = 0;
        initialiserGrille();

    }

    protected void autoRemplissage() {

        automate = new Automate(grille.length, grille[0].length);
        generation = 0;
        initialiserGrille();

    }

    protected int getVitesseActualisation() { return vitesseActualisation; }
    protected void setVitesseActualisation(int vitesseActualisation) { this.vitesseActualisation = vitesseActualisation; }

    @Override
    @Transient
    public Dimension getPreferredSize() {

        return new Dimension(grille[0].length * 8, grille.length * 8);

    }

    @Override
    protected void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Color graphColor = graph.getColor();
        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                if (grille[x][y]) {
                    graph.setColor(Color.red);
                    graph.fillRect(y * 8, x * 8, 8, 8);
                }
            }
        }
        graph.setColor(graphColor);

    }

    @Override
    public void run() {

        mettreAJourGrille();
        repaint();
        try {
            Thread.sleep(vitesseActualisation);
            run();
        } catch (InterruptedException _) {}

    }
}
