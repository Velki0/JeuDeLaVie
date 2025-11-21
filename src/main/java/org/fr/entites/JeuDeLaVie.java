package org.fr.entites;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JeuDeLaVie extends JFrame implements ActionListener {

    // Menu du jeu
    private final JMenuBar barreDeMenu;
    private final JMenu menuFichier, menuJeu, menuAide;
    private final JMenuItem menuFichierNouvelleGrille, menuFichierOuvrir, menuFichierOptions, menuFichierQuitter;
    private final JMenuItem menuJeuAutoRemplissage, menuJeuStart, menuJeuStop, menuJeuReset;
    private final JMenuItem  menuAideSource, menuAideAPropos;
    private final PlateauDeJeu plateauDeJeu;
    private Thread jeu;

    public JeuDeLaVie() {

        // Initialisation du menu
        barreDeMenu = new JMenuBar();
        menuFichier = new JMenu("Fichier");
        menuJeu = new JMenu("Jeu");
        menuAide = new JMenu("Aide");
        menuFichierNouvelleGrille = new JMenuItem("Nouvelle Grille");
        menuFichierOuvrir = new JMenuItem("Ouvrir ...");
        menuFichierOptions = new JMenuItem("Options");
        menuFichierQuitter = new JMenuItem("Quitter");
        menuJeuAutoRemplissage = new JMenuItem("Auto remplissage");
        menuJeuStart = new JMenuItem("Start");
        menuJeuStop = new JMenuItem("Stop");
        menuJeuReset = new JMenuItem("Reset");
        menuAideSource = new JMenuItem("Source");
        menuAideAPropos = new JMenuItem("A Propos");
        setJMenuBar(barreDeMenu);
        barreDeMenu.add(menuFichier);
        barreDeMenu.add(menuJeu);
        barreDeMenu.add(menuAide);
        menuFichier.add(menuFichierNouvelleGrille);
        menuFichier.add(menuFichierOuvrir);
        menuFichier.add(menuFichierOptions);
        menuFichier.add(menuFichierQuitter);
        menuJeu.add(menuJeuAutoRemplissage);
        menuJeu.add(menuJeuStart);
        menuJeu.add(menuJeuStop);
        menuJeu.add(menuJeuReset);
        menuAide.add(menuAideSource);
        menuAide.add(menuAideAPropos);
        menuFichierNouvelleGrille.addActionListener(this);
        menuFichierOuvrir.addActionListener(this);
        menuFichierOptions.addActionListener(this);
        menuFichierQuitter.addActionListener(this);
        menuJeuAutoRemplissage.addActionListener(this);
        menuJeuStart.addActionListener(this);
        menuJeuStop.addActionListener(this);
        menuJeuReset.addActionListener(this);
        menuAideSource.addActionListener(this);
        menuAideAPropos.addActionListener(this);
        menuJeuStop.setEnabled(false);

        // Initialisation du plateau de jeu
        plateauDeJeu = new PlateauDeJeu();
        add(plateauDeJeu);
        pack();
        jeu = new Thread(plateauDeJeu);

    }

    private void mettreLeJeuEnMarche(boolean marche) {

        if (marche) {
            menuJeuStart.setEnabled(false);
            menuJeuStop.setEnabled(true);
            jeu = new Thread(plateauDeJeu);
            jeu.start();
        } else  {
            menuJeuStart.setEnabled(true);
            menuJeuStop.setEnabled(false);
            jeu.interrupt();
        }

    }

    @Override
    public void actionPerformed(ActionEvent evenement) {

        if (evenement.getSource() == menuFichierNouvelleGrille) {

            // Création d'une nouvelle grille vierge de taille renseignée par l'utilisateur
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            final JFrame fenetreNouvelleGrille = new JFrame();
            fenetreNouvelleGrille.setTitle("Nouvelle Grille");
            fenetreNouvelleGrille.setResizable(false);
            JPanel panneauNouvelleGrille = new JPanel();
            fenetreNouvelleGrille.add(panneauNouvelleGrille);
            panneauNouvelleGrille.setLayout(new BoxLayout(panneauNouvelleGrille, BoxLayout.Y_AXIS));
            final JSpinner spinnerNouvelleGrilleHauteur = new JSpinner(new SpinnerNumberModel(20, 20, 120, 1));
            final JSpinner spinnerNouvelleGrilleLargeur = new JSpinner(new SpinnerNumberModel(20, 20, 120, 1));
            final JButton creerNouvelleGrille = new JButton("Créer la nouvelle Grille");
            panneauNouvelleGrille.add(new JLabel("Option de la nouvelle Grille", SwingConstants.CENTER));
            panneauNouvelleGrille.add(new JLabel("Nombre le lignes (120max) : ", SwingConstants.CENTER));
            panneauNouvelleGrille.add(spinnerNouvelleGrilleHauteur);
            panneauNouvelleGrille.add(new JLabel("Nombre le colonnes (120max) : ", SwingConstants.CENTER));
            panneauNouvelleGrille.add(spinnerNouvelleGrilleLargeur);
            panneauNouvelleGrille.add(creerNouvelleGrille);
            panneauNouvelleGrille.setBorder(BorderFactory.createLineBorder(panneauNouvelleGrille.getBackground(), 10));

            creerNouvelleGrille.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evenement) {

                    plateauDeJeu.rearrangerGrille((int) spinnerNouvelleGrilleHauteur.getValue(), (int) spinnerNouvelleGrilleLargeur.getValue());
                    pack();
                    fenetreNouvelleGrille.dispose();

                }

            });

            fenetreNouvelleGrille.pack();
            fenetreNouvelleGrille.setLocationRelativeTo(null);
            fenetreNouvelleGrille.setVisible(true);

        } else if (evenement.getSource().equals(menuFichierOuvrir)) {

            // Permettre l'ouverture d'un modèle prédéfini sous le format .rle ou .txt
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
            FileNameExtensionFilter filtre = new FileNameExtensionFilter("Fichiers .RLE et .TXT", "rle", "txt");
            chooser.setDialogTitle("Choisissez un modèle : ");
            chooser.setFileFilter(filtre);
            int valeurRetourne = chooser.showOpenDialog(null);
            if(valeurRetourne == JFileChooser.APPROVE_OPTION) {
                Path chemin = Paths.get(chooser.getSelectedFile().getPath());
                try {
                    plateauDeJeu.chargerModele(chemin);
                    pack();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (evenement.getSource().equals(menuFichierOptions)){

            // Permettre le changement de la vitesse d'actualisation
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            final JFrame fenetreVitesse = new JFrame();
            fenetreVitesse.setTitle("Changement de vitesse d'actualisation");
            fenetreVitesse.setResizable(false);
            JPanel panneauVitesse = new JPanel();
            fenetreVitesse.add(panneauVitesse);
            panneauVitesse.setLayout(new BoxLayout(panneauVitesse, BoxLayout.Y_AXIS));
            final JSlider sliderVitesse = new JSlider(0, 500, plateauDeJeu.getVitesseActualisation());
            sliderVitesse.setPaintLabels(true);
            sliderVitesse.setPaintTicks(true);
            sliderVitesse.setPaintTrack(true);
            sliderVitesse.setMajorTickSpacing(100);
            sliderVitesse.setMinorTickSpacing(50);
            final JLabel sliderLabel = new JLabel("Vitesse : " + sliderVitesse.getValue() + " ms");
            final JButton changerVitesse = new JButton("Changer la vitesse");
            panneauVitesse.add(sliderVitesse);
            panneauVitesse.add(sliderLabel);
            panneauVitesse.add(changerVitesse);
            panneauVitesse.setBorder(BorderFactory.createLineBorder(panneauVitesse.getBackground(), 10));

            sliderVitesse.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    if(sliderVitesse.getValue() < 10){
                        sliderVitesse.setValue(10);
                    }
                    sliderLabel.setText("Vitesse : " + sliderVitesse.getValue() + " ms");

                }

            });

            changerVitesse.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evenement) {

                    plateauDeJeu.setVitesseActualisation(sliderVitesse.getValue());
                    mettreLeJeuEnMarche(true);
                    fenetreVitesse.dispose();

                }

            });

            fenetreVitesse.pack();
            fenetreVitesse.setLocationRelativeTo(null);
            fenetreVitesse.setVisible(true);

        } else if (evenement.getSource().equals(menuFichierQuitter)) {

            // Bouton Quitter
            System.exit(0);

        } else if (evenement.getSource().equals(menuJeuAutoRemplissage)) {

            // Remplissage Auto
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            plateauDeJeu.autoRemplissage();

        } else if (evenement.getSource().equals(menuJeuStart)) {

            // Mettre le jeu en route
            mettreLeJeuEnMarche(true);

        } else if (evenement.getSource().equals(menuJeuStop)) {

            // Mettre le jeu en pause
            mettreLeJeuEnMarche(false);

        } else if (evenement.getSource().equals(menuJeuReset)) {

            // Réinitialiser la grille au format actuel avec uniquement des cases vides
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            plateauDeJeu.reinitialiserGrille();
            plateauDeJeu.repaint();

        } else if (evenement.getSource().equals(menuAideSource)) {

            // Source du projet
            Desktop bureau = Desktop.getDesktop();
            try {
                bureau.browse(new URI("https://github.com/Velki0/JeuDeLaVie"));
            } catch (URISyntaxException | IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Vous pouvez retrouver le projet sur GitHub à l'adresse suivante : \nhttps://github.com/Velki0/JeuDeLaVie",
                        "Source",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (evenement.getSource().equals(menuAideAPropos)) {

            // À propos du projet
            JOptionPane.showMessageDialog(null,
                    """
                            Ce jeu est inspiré du célèbre jeu "Game of Life" de John Conway.                        \s
                            Ce projet est une création personnelle dans le but de découvrir l'environnement Java.   \s
                            N'hésitez pas à me retrouver sur GitHub à l'adresse suivante : https://github.com/Velki0"""
            );

        }

    }

}
