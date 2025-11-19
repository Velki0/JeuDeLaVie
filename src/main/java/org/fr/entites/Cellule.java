package org.fr.entites;

public class Cellule {

    private boolean enVie;
    private boolean prochainEtat;

    public Cellule() {

        aleatoireEnVie();

    }

    public Cellule(boolean enVie) {

        this.enVie = enVie;

    }

    public boolean getEnVie() { return enVie; }
    public boolean getProchainEtat() { return prochainEtat; }

    public void setEnVie(boolean enVie) { this.enVie = enVie; }
    public void setProchainEtat(boolean prochainEtat) { this.prochainEtat = prochainEtat; }

    private void aleatoireEnVie() { enVie = (int) (Math.random() * 2) == 1; }

}