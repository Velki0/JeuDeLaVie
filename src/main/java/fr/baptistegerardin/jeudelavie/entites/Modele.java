package fr.baptistegerardin.jeudelavie.entites;

import fr.baptistegerardin.jeudelavie.assertion.ModeleRenseigneNonConforme;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La classe Modele est un modèle d'automate cellulaire qui peut être chargé grâce à une ressource extérieure.
 * Les sites communautaires comme https://conwaylife.com/wiki/ ou https://conwaylife.appspot.com/library/ proposent un certain nombre de modèles disponibles généralement au format .rle.
 * Ces modèles peuvent être téléchargés et ouverts grâce à cette application.
 * Ils doivent cependant respecter un certain formatage comme ci-dessous :
 * Plusieurs lignes de commentaires optionnels.
 * Puis une ligne qui stipule le nombre de colonnes et de lignes, exemple : "x = 45, y = 30, rule = B3/S23".
 * Et enfin une suite de nombres, de 'b', de 'o', de '$' et enfin d'un '!'.
 * Chaque instance de nombre fait référence à l'occurrence du prochain caractère.
 * Un 'b' est une cellule morte, un 'o' est une cellule en vie, le '$' signifie un changement de ligne et le '!' signifie la fin du modèle.
 * @author Velki0
 * @version 1.0
 */
public class Modele {

    /** Le nombre total de lignes du modèle */
    private final int lignesTotales;
    /** Le nombre total de colonnes du modèle */
    private final int colonnesTotales;
    /** Une liste de String contenant toutes les données brutes fournies par le fichier .rle ou .txt */
    private final List<String> modeleEncode;
    /** Un tableau de booléens contenant toutes les informations du modèle normalisé et lisible par le programme */
    private final List<List<Boolean>> modeleNormalise;

    /**
     * Constructeur de la méthode Modele pour l'instanciation d'un nouveau modèle d'automate.
     * Cette méthode nécessite d'un chemin d'accès pour le fichier .rle ou .txt à lire.
     * @param chemin Le chemin d'accès du fichier au format .rle ou .txt.
     * @throws IOException Exception jetée si le chemin renvoie sur un fichier introuvable ou non-existant.
     * @throws ModeleRenseigneNonConforme Exception jetée si le fichier renseigné ne possède pas des attributs conformes de lignes et colonnes 'y = ' et 'x = '.
     */
    protected Modele(Path chemin) throws IOException, ModeleRenseigneNonConforme {

        modeleEncode = Files.readAllLines(chemin, StandardCharsets.UTF_8);
        modeleEncode.removeIf(ligne -> ligne.charAt(0) == '#');
        if (!(modeleEncode.getFirst().matches(".*(x = ).*") && modeleEncode.getFirst().matches(".*(, y = ).*"))) {
            throw new ModeleRenseigneNonConforme("Le fichier ne renseigne pas ou renseigne mal son nombre de lignes et de colonnes");
        }
        lignesTotales = getLignesTotales();
        colonnesTotales = getColonnesTotales();
        if (lignesTotales < 1 || colonnesTotales < 1) {
            throw new ModeleRenseigneNonConforme("Le fichier ne possède pas un nombre de lignes et de colonnes correct : 'x = " + colonnesTotales + "' et 'y = '" + lignesTotales + "'");
        }
        modeleNormalise = normaliserModele();

    }

    /**
     * La méthode getLignesTotales permet de lire la valeur du nombre de lignes totales 'y = ' contenu dans le fichier fourni par l'utilisateur.
     * @return La valeur du nombre de lignes totales du modèle chargé.
     */
    private int getLignesTotales() {

        return Integer.parseInt(modeleEncode.getFirst().substring((modeleEncode.getFirst().indexOf("y = ") + 4),modeleEncode.getFirst().indexOf(",", (modeleEncode.getFirst().indexOf(",") + 1))));

    }

    /**
     * La méthode getColonnesTotales permet de lire la valeur du nombre de lignes totales 'x = ' contenu dans le fichier fourni par l'utilisateur.
     * @return La valeur du nombre de colonnes totales du modèle chargé.
     */
    private int getColonnesTotales() {

        return Integer.parseInt(modeleEncode.getFirst().substring((modeleEncode.getFirst().indexOf("x = ") + 4), modeleEncode.getFirst().indexOf(",")));

    }

    /**
     * La méthode normaliserModele permet de renvoyer un modèle entièrement décodé.
     * @return Un tableau de booléens faisant référence au modèle normalisé et lisible par l'application.
     */
    private List<List<Boolean>> normaliserModele() {

        List<List<Boolean>> modeleNormalise = new ArrayList<>();
        List<String> lignesNormalise = normaliserLignes();
        List<Boolean> ligneCaractere;
        for (String ligne : lignesNormalise) {
            ligneCaractere = new ArrayList<>();
            for (int y = 0; y < ligne.length(); y++) {
                if (ligne.charAt(y) == 'o') {
                    ligneCaractere.add(true);
                } else {
                    ligneCaractere.add(false);
                }
            }
            modeleNormalise.add(ligneCaractere);
        }
        return modeleNormalise;

    }

    /**
     * La méthode normaliserLignes permet à partir du modèle encodé de générer une liste de String où chaque ligne contient uniquement des 'b' et des 'o'.
     * Le nombre de lignes de la liste contiendra exactement le nombre de caracteres prévu par le modèle (lignesTotales).
     * Et chaque ligne contiendra précisément le nombre de caractères prévu par le modèle référencé (colonnesTotales).
     * @return Une liste de String normalisée du modèle.
     */
    private List<String> normaliserLignes() {

        // Le modèle est tout d'abord mis sur une seule et même ligne, la première ligne du modèle brut contenant les données 'x = ' et 'y = ' est omise.
        StringBuilder modeleSurUneSeuleLigne = new StringBuilder();
        for (int index = 1; index < modeleEncode.size(); index++) {
            modeleSurUneSeuleLigne.append(modeleEncode.get(index));
        }

        // Le modèle est traité pour en faire une liste de String avec laquelle chaque '$' défini une nouvelle ligne
        List<String> lignesModeleEncode = new ArrayList<>();
        int compteurLigne = 0;
        lignesModeleEncode.add("");
        for (char caractere : modeleSurUneSeuleLigne.toString().toCharArray()) {
            if (caractere == '$') {
                lignesModeleEncode.set(compteurLigne, lignesModeleEncode.get(compteurLigne) + caractere);
                compteurLigne++;
                lignesModeleEncode.add("");
            } else {
                lignesModeleEncode.set(compteurLigne, lignesModeleEncode.get(compteurLigne) + caractere);
            }
        }

        // Ajout d'un '1' au début des lignes qui commence par une lettre et entre deux caractères non digitaux
        // Une vérification est nécessaire si le modèle fourni ne possède aucune itération de 'b' ou de 'o'
        if (modeleSurUneSeuleLigne.toString().matches(".*[b|o].*")) {
            for (int index = 0; index < lignesModeleEncode.size(); index++) {
                if (lignesModeleEncode.get(index).charAt(0) == 'b' || lignesModeleEncode.get(index).charAt(0) == 'o') {
                    lignesModeleEncode.set(index, '1' + lignesModeleEncode.get(index));
                }
                while (lignesModeleEncode.get(index).matches(".*[a-z][a-z|$].*")) {
                    lignesModeleEncode.replaceAll(chaine -> chaine.replaceAll("([a-z])([a-z|$])", "$11$2"));
                }
            }
        }

        // Décoder le RLE puis completer les lignes incomplètes
        List<String> lignesModeleDecode = new ArrayList<>();
        StringBuilder chaineDecode;
        for (String ligne : lignesModeleEncode) {
            int compteur = 0;
            chaineDecode = new StringBuilder();
            for (char caractere : ligne.toCharArray()) {
                if (Character.isDigit(caractere)) {
                    compteur = 10 * compteur + Character.getNumericValue(caractere);
                } else if (caractere == '$' | caractere == '!') {
                    lignesModeleDecode.add(chaineDecode.toString());
                    while (compteur > 1) {
                        lignesModeleDecode.add(String.join("", Collections.nCopies(colonnesTotales, "b")));
                        compteur--;
                    }
                } else {
                    chaineDecode.append(String.join("", Collections.nCopies(compteur, String.valueOf(caractere))));
                    compteur = 0;
                }
            }
        }

        // Complète par des cases mortes les colonnes et lignes non complètes ou précisées dans le fichier source
        for (int index = 0; index < lignesModeleDecode.size(); index++) {
            if (lignesModeleDecode.get(index).length() < colonnesTotales) {
                lignesModeleDecode.set(index, (lignesModeleDecode.get(index) + String.join("", Collections.nCopies((colonnesTotales - lignesModeleDecode.get(index).length()), "b"))));
            }
        }
        for (int index = lignesModeleDecode.toArray().length; index < lignesTotales; index++) {
            lignesModeleDecode.add(String.join("", Collections.nCopies(colonnesTotales, "b")));
        }
        return lignesModeleDecode;

    }

    /**
     * Getter de la classe Modele permettant de récupérer le modèle lisible par l'application.
     * @return Le modèle de l'automate cellulaire sous la forme d'un tableau de booléens.
     */
    protected List<List<Boolean>> getModeleNormaliser() { return modeleNormalise; }

}
