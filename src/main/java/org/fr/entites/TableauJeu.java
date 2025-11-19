package org.fr.entites;

import java.util.ArrayList;
import java.util.List;

public class TableauJeu {

    private final int lignesTotales;
    private final int colonnesTotales;
    private final List<List<Cellule>> cellules;

    public TableauJeu(int lignesTotales, int colonnesTotales) {

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

    public TableauJeu(List<List<Boolean>> pattern) {

        this.lignesTotales = pattern.size();
        this.colonnesTotales = pattern.getFirst().size();
        cellules = new ArrayList<>();
        List<Cellule> ligneCellules;
        for (int x = 0; x < lignesTotales; x++) {
            ligneCellules = new ArrayList<>();
            for (int y = 0; y < colonnesTotales; y++) {
                ligneCellules.add(new Cellule(pattern.get(x).get(y)));
            }
            cellules.add(ligneCellules);
        }

    }

    public void afficherTableauJeu() {

        char etat;
        System.out.println("-------------------------------------------------Tableau Jeu-------------------------------------------------");
        for (List<Cellule> ligne : cellules) {
            System.out.print("|");
            for (Cellule cellule : ligne) {
                if (cellule.getEnVie()) { etat = 'X'; } else { etat = ' '; }
                System.out.print(etat + "|");
            }
            System.out.println();
        }

    }

    private boolean getValeurCellule(int ligne, int colonne) {

        if (ligne >= 0 && colonne >= 0 && ligne < lignesTotales && colonne < colonnesTotales) {
            return cellules.get(ligne).get(colonne).getEnVie();
        } else {
            return false;
        }

    }

    private int compterVoisins(int ligne, int colonne) {

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

    public void setProchainTableau() {

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

    }

    public void setTableau() {

        for (int x = 0; x < lignesTotales; x++) {
            for (int y = 0; y < colonnesTotales; y++) {
                cellules.get(x).get(y).setEnVie(cellules.get(x).get(y).getProchainEtat());
            }
        }

    }

}