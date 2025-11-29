package fr.baptistegerardin.jeudelavie;

import fr.baptistegerardin.jeudelavie.entites.JeuDeLaVie;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame jeuDeLaVie = new JeuDeLaVie();
        jeuDeLaVie.setTitle("Jeu de la Vie de Conway");
        jeuDeLaVie.setLocationByPlatform(true);
        jeuDeLaVie.setResizable(false);
        jeuDeLaVie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jeuDeLaVie.setVisible(true);

    }

}
