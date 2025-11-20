package org.fr.entites;

import javax.swing.*;
import java.awt.*;
import java.beans.Transient;
import java.util.Arrays;

public class PlateauDeJeu extends JPanel implements Runnable {

    private boolean[][] grille;
    private int generation;
    private Automate automate;
    private final JLabel generationLabel;
    private int vitesseActualisation;

    public PlateauDeJeu(int lignesTotales, int colonnesTotales) {

        this.grille = new boolean[lignesTotales][colonnesTotales];
        this.automate = new Automate(lignesTotales, colonnesTotales);
        initialiserGrille();
        generation = 0;
        generationLabel = new JLabel("Génération : " + generation);
        add(generationLabel);
        vitesseActualisation = 100;

    }

    public PlateauDeJeu(Automate automate) {

        this.grille = new boolean[automate.getLignesTotales()][automate.getColonnesTotales()];
        this.automate = automate;
        initialiserGrille();
        generation = 0;
        generationLabel = new JLabel("Génération : " + generation);
        add(generationLabel);
        vitesseActualisation = 100;

    }

    private void initialiserGrille() {

        for (int x = 0; x < grille.length; x++) {
            for (int y = 0; y < grille[0].length; y++) {
                grille[x][y] = automate.getCellules().get(x).get(y).getEnVie() ;
            }
        }

    }

    private void mettreAJourGrille() {

        automate.mettreAJourEtatAutomate();
        initialiserGrille();
        generation++;
        generationLabel.setText("Génération : " + generation);

    }

    public void reinitialiserGrille() {

        automate.reinitialiserAutomate();
        for (boolean[] ligne : grille) {
            Arrays.fill(ligne, false);
        }
        generation = 0;
        generationLabel.setText("Génération : " + generation);

    }

    public void autoRemplissageGrille() {

        automate = new Automate(grille.length, grille[0].length);
        initialiserGrille();
        generation = 0;
        repaint();
        generationLabel.setText("Génération : " + generation);

    }

    public void rearrangerGrille(int lignesTotales, int colonnesTotales) {

        this.grille = new boolean[lignesTotales][colonnesTotales];
        this.automate = new Automate(lignesTotales, colonnesTotales);
        initialiserGrille();
        generation = 0;
        repaint();
        generationLabel.setText("Génération : " + generation);

    }

    public int getVitesseActualisation() { return vitesseActualisation; }
    public void setVitesseActualisation(int vitesseActualisation) { this.vitesseActualisation = vitesseActualisation; }

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
