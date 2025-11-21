package org.fr.entites;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Modele {

    private final int lignesTotales;
    private final int colonnesTotales;
    private final List<String> modeleEncode;
    private List<List<Boolean>> modeleNormalise;

    public Modele(Path chemin) throws IOException {

        modeleEncode = Files.readAllLines(chemin, StandardCharsets.UTF_8);
        modeleEncode.removeIf(ligne -> ligne.charAt(0) == '#');
        lignesTotales = getLignesTotales(modeleEncode);
        colonnesTotales = getColonnesTotales(modeleEncode);
        normaliserModele();

    }

    private void normaliserModele() {

        modeleNormalise = new ArrayList<>();
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

    }

    private List<String> normaliserLignes() {

        StringBuilder modeleSurUneSeuleLigne = new StringBuilder();
        for (int index = 1; index < modeleEncode.size(); index++) {
            modeleSurUneSeuleLigne.append(modeleEncode.get(index));
        }
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
        for (int index = 0; index < lignesModeleEncode.size(); index++) {
            if (lignesModeleEncode.get(index).charAt(0) == 'b' || lignesModeleEncode.get(index).charAt(0) == 'o') {
                lignesModeleEncode.set(index, '1' + lignesModeleEncode.get(index));
            }
            while (lignesModeleEncode.get(index).matches(".*[a-z][a-z|$].*")) {
                lignesModeleEncode.replaceAll(chaine -> chaine.replaceAll("([a-z])([a-z|$])", "$11$2"));
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
        // Complète par des cases mortes les lignes non complètes
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

    private static int getLignesTotales(List<String> pattern) {

        return Integer.parseInt(pattern.getFirst().substring((pattern.getFirst().indexOf("y = ") + 4),pattern.getFirst().indexOf(",", (pattern.getFirst().indexOf(",") + 1))));

    }

    private static int getColonnesTotales(List<String> modele) {

        return Integer.parseInt(modele.getFirst().substring((modele.getFirst().indexOf("x = ") + 4), modele.getFirst().indexOf(",")));

    }

    protected List<List<Boolean>> getModeleNormaliser() { return modeleNormalise; }

}
