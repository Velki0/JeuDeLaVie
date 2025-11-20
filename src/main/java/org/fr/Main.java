package org.fr;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    static void main(String[] args) throws IOException {

        JFrame jeuDeLaVie = new JeuDeLaVie();
        jeuDeLaVie.setTitle("Jeu de la Vie de Conway");
        jeuDeLaVie.setLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2
        );
        jeuDeLaVie.setLocationByPlatform(true);
        jeuDeLaVie.setResizable(false);
        jeuDeLaVie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jeuDeLaVie.setVisible(true);

    }

}
