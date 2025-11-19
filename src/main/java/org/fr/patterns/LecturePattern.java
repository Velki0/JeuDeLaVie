package org.fr.patterns;

import org.fr.entites.TableauJeu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class LecturePattern {

    public static void LancerPattern() throws IOException, InterruptedException {

        Path chemin = Paths.get("src/main/resources/patterns/pattern.rle");
        List<String> pattern = Files.readAllLines(chemin, StandardCharsets.UTF_8);
        pattern.removeIf(ligne -> ligne.charAt(0) == '#');
        List<List<Boolean>> patternNormalise = normaliserPattern(pattern);

        TableauJeu tableauJeu =  new TableauJeu(patternNormalise);
        while(true){
            tableauJeu.afficherTableauJeu();
            tableauJeu.setProchainTableau();
            tableauJeu.setTableau();
            TimeUnit.MILLISECONDS.sleep(100);
        }

    }

    private static List<List<Boolean>> normaliserPattern(List<String> pattern) {

        List<List<Boolean>> patternNormalise = new ArrayList<>();
        int colonnesTotales = getColonnesTotales(pattern);
        List<String> lignesNormalise = normaliserLignes(pattern, colonnesTotales);
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
            patternNormalise.add(ligneCaractere);
        }

        return patternNormalise;

    }

    private static List<String> normaliserLignes(List<String> pattern, int colonnesTotales) {

        StringBuilder patternSurUneSeuleLigne = new StringBuilder();
        for (int index = 1; index < pattern.size(); index++) {
            patternSurUneSeuleLigne.append(pattern.get(index));
        }
        List<String> lignesPatternEncode = new ArrayList<>();
        int compteurligne = 0;
        lignesPatternEncode.add("");
        for (char caractere : patternSurUneSeuleLigne.toString().toCharArray()) {
            if (caractere == '$') {
                lignesPatternEncode.set(compteurligne, lignesPatternEncode.get(compteurligne) + caractere);
                compteurligne++;
                lignesPatternEncode.add("");
            } else {
                lignesPatternEncode.set(compteurligne, lignesPatternEncode.get(compteurligne) + caractere);
            }
        }
        // Ajout d'un '1' au début des lignes qui commence par une lettre et entre deux caractères non digitaux
        for (int index = 0; index < lignesPatternEncode.size(); index++) {
            if (lignesPatternEncode.get(index).charAt(0) == 'b' || lignesPatternEncode.get(index).charAt(0) == 'o') {
                lignesPatternEncode.set(index, '1' + lignesPatternEncode.get(index));
            }
            while (lignesPatternEncode.get(index).matches(".*[a-z][a-z|$].*")) {
                lignesPatternEncode.replaceAll(chaine -> chaine.replaceAll("([a-z])([a-z|$])", "$11$2"));
            }
        }
        // Décoder le RLE puis completer les lignes incomplètes
        List<String> lignesPatternDecode = new ArrayList<>();
        StringBuilder chaineDecode;
        for (String ligne : lignesPatternEncode) {
            int compteur = 0;
            chaineDecode = new StringBuilder();
            for (char caractere : ligne.toCharArray()) {
                if (Character.isDigit(caractere)) {
                    compteur = 10 * compteur + Character.getNumericValue(caractere);
                } else if (caractere == '$' | caractere == '!') {
                    lignesPatternDecode.add(chaineDecode.toString());
                    while (compteur > 1) {
                        lignesPatternDecode.add(String.join("", Collections.nCopies(colonnesTotales, "b")));
                        compteur--;
                    }
                } else {
                    chaineDecode.append(String.join("", Collections.nCopies(compteur, String.valueOf(caractere))));
                    compteur = 0;
                }
            }
        }
        // Complète par des cases mortes les lignes non complètes
        for (int index = 0; index < lignesPatternDecode.size(); index++) {
            if (lignesPatternDecode.get(index).length() < colonnesTotales) {
                lignesPatternDecode.set(index, (lignesPatternDecode.get(index) + String.join("", Collections.nCopies((colonnesTotales - lignesPatternDecode.get(index).length()), "b"))));
            }
        }
        return lignesPatternDecode;

    }


    private static int getLignesTotales(List<String> pattern) {

        return Integer.parseInt(pattern.getFirst().substring((pattern.getFirst().indexOf("y = ") + 4),pattern.getFirst().indexOf(",", (pattern.getFirst().indexOf(",") + 1))));

    }

    private static int getColonnesTotales(List<String> pattern) {

        return Integer.parseInt(pattern.getFirst().substring((pattern.getFirst().indexOf("x = ") + 4),pattern.getFirst().indexOf(",")));

    }

}
