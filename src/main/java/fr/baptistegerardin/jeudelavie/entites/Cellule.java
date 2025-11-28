package fr.baptistegerardin.jeudelavie.entites;

public class Cellule {

    private boolean enVie;
    private boolean prochainEtat;

    protected Cellule() {

        aleatoireEnVie();

    }

    protected Cellule(boolean enVie) {

        this.enVie = enVie;

    }

    protected boolean getEnVie() { return enVie; }
    protected boolean getProchainEtat() { return prochainEtat; }

    protected void setEnVie(boolean enVie) { this.enVie = enVie; }
    protected void setProchainEtat(boolean prochainEtat) { this.prochainEtat = prochainEtat; }

    private void aleatoireEnVie() { enVie = (int) (Math.random() * 2) == 1; }

}