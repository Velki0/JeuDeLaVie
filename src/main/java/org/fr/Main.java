package org.fr;

import org.fr.entites.JeuDeLaVie;

import javax.swing.*;
import java.awt.*;

public class Main {

    static void main(String[] args) {

        JFrame jeuDeLaVie = new JeuDeLaVie();
        jeuDeLaVie.setTitle("Jeu de la Vie de Conway");
        jeuDeLaVie.setLocationByPlatform(true);
        jeuDeLaVie.setResizable(false);
        jeuDeLaVie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jeuDeLaVie.setVisible(true);

    }

}
