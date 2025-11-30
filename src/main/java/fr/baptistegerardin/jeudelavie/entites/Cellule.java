package fr.baptistegerardin.jeudelavie.entites;

/**
 * Une cellule est le plus petit élément du jeu de la vie de Conway
 * Elle est constituée de deux attributs :
 * - Une première valeur booléenne concernant son état "en vie" ou "morte".
 * - Une seconde valeur booléenne sur son état à la prochaine génération.
 * @author Velki0
 * @version 1.0
 */
public class Cellule {

    /** État à la génération en cours. */
    private boolean enVie;
    /** État à la prochaine génération. */
    private boolean prochainEtat;

    /**
     * Constructeur permettant de créer une nouvelle cellule avec un état de départ aléatoire si aucun paramètre n'est sélectionné.
     */
    protected Cellule() {

        aleatoireEnVie();

    }

    /**
     * Constructeur permettant de fixer l'état de départ de la nouvelle cellule.
     * @param enVie État de la cellule.
     */
    protected Cellule(boolean enVie) {

        this.enVie = enVie;

    }

    /**
     * Getter permettant de récupérer l'état actuel de la cellule.
     * @return L'état de la cellule pour la génération en cours.
     */
    protected boolean getEnVie() { return enVie; }

    /**
     * Getter permettant de récupérer le prochain état de la cellule.
     * @return L'état de la cellule pour la génération suivante.
     */
    protected boolean getProchainEtat() { return prochainEtat; }

    /**
     * Setter permettant de fixer l'état actuel de la cellule.
     * @param enVie Valeur booléenne redéfinissant l'état actuel de la cellule.
     */
    protected void setEnVie(boolean enVie) { this.enVie = enVie; }

    /**
     * Setter permettant de fixer le prochain état de la cellule.
     * @param prochainEtat Valeur booléenne redéfinissant le prochain état de la cellule.
     */
    protected void setProchainEtat(boolean prochainEtat) { this.prochainEtat = prochainEtat; }

    /**
     * Méthode permettant de fixer une valeur 'true' ou 'false' pour l'attribut enVie de la cellule.
     */
    private void aleatoireEnVie() { enVie = (int) (Math.random() * 2) == 1; }

}
