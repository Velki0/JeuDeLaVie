package fr.baptistegerardin.jeudelavie.entites;

import fr.baptistegerardin.jeudelavie.exceptions.ModeleRenseigneNonConforme;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La classe Modele est un modèle d'automate cellulaire qui peut être chargé grâce à une ressource extérieure.
 * Il est constitué de trois paramètres, son nombre de lignes, son nombre de colonnes et un tableau de booléens stipulant l'état zéro de l'automate.
 * Les sites communautaires comme : <a href="https://conwaylife.com/wiki/">Le wiki de conwaylife.com</a>
 * ou <a href="https://conwaylife.appspot.com/library/">conwaylife.appspot</a> proposent un grand nombre de modèles disponibles généralement au format .rle.
 * Ces modèles téléchargés peuvent être ouverts grâce à cette application.
 * Ils doivent cependant respecter un certain formatage comme ci-dessous :
 * Plusieurs commentaires optionnels repérés par un '#' en début de ligne.
 * Puis une ligne qui stipule le nombre de colonnes et de lignes, exemple : "x = 45, y = 30, rule = B3/S23".
 * Et enfin une suite de nombres, de 'b', de 'o', de '$' et enfin d'un '!'.
 * Chaque instance de nombre fait référence à l'occurrence du prochain caractère.
 * Un 'b' est une cellule morte, un 'o' est une cellule en vie, le '$' signifie un changement de ligne et le '!' signifie la fin du modèle.
 * Pour exemples, des modèles sont présents dans ce projet à cet emplacement : /src/main/resources/modeles/.
 * @author Velki0
 * @version 1.0
 */
public class Modele {

    /** Le nombre total de lignes du modèle. */
    private final int lignesTotales;
    /** Le nombre total de colonnes du modèle. */
    private final int colonnesTotales;
    /** Un tableau de booléens contenant l'information du point de départ du modèle normalisé et lisible par le programme. */
    private final List<List<Boolean>> modeleNormalise;

    /**
     * Constructeur de la méthode Modele pour l'instanciation d'un nouveau modèle d'automate.
     * Il nécessite d'un chemin d'accès pour le fichier .rle ou .txt à lire.
     * @param chemin Le chemin d'accès du fichier au format .rle ou .txt.
     * @throws IOException Exception jetée si le chemin renvoie sur un fichier introuvable ou non-existant.
     * @throws ModeleRenseigneNonConforme Exception jetée si le fichier renseigné ne possède pas des attributs conformes de lignes et colonnes 'y = ' et 'x = '.
     */
    protected Modele(Path chemin) throws IOException, ModeleRenseigneNonConforme {

        // Le modèle brut est lu puis les éventuels commentaires sont supprimés.
        List<String> modeleEncode = Files.readAllLines(chemin, StandardCharsets.UTF_8);
        modeleEncode.removeIf(ligne -> ligne.charAt(0) == '#');

        // Quelques tests sont réalisés pour s'assurer que le fichier renseigné contient bien des informations sur la taille du modèle.
        // Les attributs lignesTotales et colonnesTotales sont initialisés.
        if (!(modeleEncode.getFirst().matches(".*(x = ).*") && modeleEncode.getFirst().matches(".*(, y = ).*"))) {
            throw new ModeleRenseigneNonConforme("Le fichier ne renseigne pas ou renseigne mal son nombre de lignes et de colonnes");
        }
        lignesTotales = Integer.parseInt(modeleEncode.getFirst().substring(
                modeleEncode.getFirst().indexOf("y = ") + 4,
                modeleEncode.getFirst().indexOf(",", (modeleEncode.getFirst().indexOf(",") + 1)))
        );
        colonnesTotales = Integer.parseInt(modeleEncode.getFirst().substring(
                modeleEncode.getFirst().indexOf("x = ") + 4,
                modeleEncode.getFirst().indexOf(","))
        );
        if (lignesTotales < 1 || colonnesTotales < 1) {
            throw new ModeleRenseigneNonConforme("Le fichier ne possède pas un nombre de lignes et de colonnes correct : 'x = " + colonnesTotales + "' et 'y = '" + lignesTotales + "'");
        }

        // Enfin le modèle est normalisé via l'utilisation de la méthode 'normaliserModele'.
        modeleNormalise = normaliserModele(modeleEncode);

    }

    /**
     * La méthode 'normaliserModele' permet à partir d'un modèle encodé brut de générer un tableau de booléens où chaque ligne et colonne contiennent uniquement des 'b' et des 'o'.
     * Le nombre de lignes contiendra exactement le nombre de caracteres prévu par le modèle (lignesTotales).
     * Et chaque ligne contiendra précisément le nombre de caractères (colonnesTotales).
     * @param modeleEncode La liste de donnée brute du modèle.
     * @return Un tableau de booléens constituant le point de départ du modèle.
     */
    private List<List<Boolean>> normaliserModele(List<String> modeleEncode) {

        // Le modèle est tout d'abord mis sur une seule et même ligne, la première ligne du modèle brut contenant les données 'x = ' et 'y = ' est omise.
        StringBuilder modeleSurUneSeuleLigne = new StringBuilder();
        for (int index = 1; index < modeleEncode.size(); index++) {
            modeleSurUneSeuleLigne.append(modeleEncode.get(index));
        }

        // Le modèle est traité pour en faire une liste de String avec laquelle chaque '$' défini une nouvelle ligne.
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

        // Ajout d'un '1' au début des lignes qui commence par une lettre et entre deux caractères non digitaux.
        // Une vérification est nécessaire si le modèle fourni ne possède aucune itération de 'b' ou de 'o'.
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

        // Le RLE est décodé à cette étape.
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

        // Des cases mortes sont ajoutées pour chaque colonne et ligne non complètes ou précisées dans le fichier source.
        for (int index = 0; index < lignesModeleDecode.size(); index++) {
            if (lignesModeleDecode.get(index).length() < colonnesTotales) {
                lignesModeleDecode.set(index, (lignesModeleDecode.get(index) + String.join("", Collections.nCopies((colonnesTotales - lignesModeleDecode.get(index).length()), "b"))));
            }
        }
        for (int index = lignesModeleDecode.toArray().length; index < lignesTotales; index++) {
            lignesModeleDecode.add(String.join("", Collections.nCopies(colonnesTotales, "b")));
        }

        // La liste de lignes décodées est enfin placé dans un tableau de booléens contenant exactement le bon nombre de lignes et de colonnes.
        List<List<Boolean>> modeleNormalise = new ArrayList<>();
        List<Boolean> ligneCaractere;
        for (String ligne : lignesModeleDecode) {
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
     * Getter permettant de récupérer le nombre de lignes du modèle renseigné par le fichier source.
     * @return Le nombre total de lignes du modèle.
     */
    protected int getLignesTotales() { return lignesTotales; }

    /**
     * Getter permettant de récupérer le nombre de colonnes du modèle renseigné par le fichier source.
     * @return Le nombre total de colonnes du modèle.
     */
    protected int getColonnesTotales() { return colonnesTotales; }

    /**
     * Getter permettant de récupérer le modèle lisible par l'application.
     * @return Le modèle de l'automate cellulaire sous la forme d'un tableau de booléens.
     */
    protected List<List<Boolean>> getModeleNormaliser() { return modeleNormalise; }

}
