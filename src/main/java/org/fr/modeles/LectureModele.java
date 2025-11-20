package org.fr.modeles;

import org.fr.entites.Automate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LectureModele {

    /*
         Les patterns doivent être placés dans src/main/java/ressources/pattersJeuDeLaVie/pattern.rle
         Ils doivent respecter un format spécifique tel que :

         #N p43 glider loop
         #O Mike Playle
         #C A period-43 oscillator based on a stable reflector
         #C Discovered on 25 Apr 2013
         #C www.conwaylife.com/wiki/P43_glider_loop
         x = 65, y = 65, rule = B3/S23
         27b2o$27bobo$29bo4b2o$25b4ob2o2bo2bo$25bo2bo3bobob2o$28bobobobo$29b2ob
         obo$33bo2$19b2o$20bo8bo$20bobo5b2o$21b2o$35bo$36bo$34b3o2$25bo$25b2o$
         24bobo4b2o22bo$31bo21b3o$32b3o17bo$34bo17b2o2$45bo$46b2o12b2o$45b2o14b
         o$3b2o56bob2o$4bo9b2o37bo5b3o2bo$2bo10bobo37b2o3bo3b2o$2b5o8bo5b2o35b
         2obo$7bo13bo22b2o15bo$4b3o12bobo21bobo12b3o$3bo15b2o22bo13bo$3bob2o35b
         2o5bo8b5o$b2o3bo3b2o37bobo10bo$o2b3o5bo37b2o9bo$2obo56b2o$3bo14b2o$3b
         2o12b2o$19bo2$11b2o17bo$12bo17b3o$9b3o21bo$9bo22b2o4bobo$38b2o$39bo2$
         28b3o$28bo$29bo$42b2o$35b2o5bobo$35bo8bo$44b2o2$31bo$30bobob2o$30bobob
         obo$27b2obobo3bo2bo$27bo2bo2b2ob4o$29b2o4bo$35bobo$36b2o!

    */

    public static Automate chargerModele() throws IOException {

        Path chemin = Paths.get("src/main/resources/modeles/pattern.rle");
        List<String> modele = Files.readAllLines(chemin, StandardCharsets.UTF_8);
        modele.removeIf(ligne -> ligne.charAt(0) == '#');
        List<List<Boolean>> modeleNormalise = normaliserModele(modele);
        return new Automate(modeleNormalise);

    }

    private static List<List<Boolean>> normaliserModele(List<String> modele) {

        List<List<Boolean>> modeleNormalise = new ArrayList<>();
        int colonnesTotales = getColonnesTotales(modele);
        List<String> lignesNormalise = normaliserLignes(modele, colonnesTotales);
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

    private static List<String> normaliserLignes(List<String> modele, int colonnesTotales) {

        StringBuilder modeleSurUneSeuleLigne = new StringBuilder();
        for (int index = 1; index < modele.size(); index++) {
            modeleSurUneSeuleLigne.append(modele.get(index));
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
        return lignesModeleDecode;

    }

    private static int getColonnesTotales(List<String> modele) {

        return Integer.parseInt(modele.getFirst().substring((modele.getFirst().indexOf("x = ") + 4),modele.getFirst().indexOf(",")));

    }

}
