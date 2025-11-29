package fr.baptistegerardin.jeudelavie.entites;

import fr.baptistegerardin.jeudelavie.exceptions.ModeleRenseigneNonConforme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class JeuDeLaVie extends JFrame implements ActionListener {

    // Menu du jeu
    private final JMenuBar barreDeMenu;
    private final JMenu menuFichier, menuJeu, menuAide;
    private final JMenuItem menuFichierNouvelleGrille, menuFichierOuvrir, menuFichierOptions, menuFichierQuitter;
    private final JMenuItem menuJeuAutoRemplissage, menuJeuStart, menuJeuStop, menuJeuReset;
    private final JMenuItem menuAideSource, menuAideAPropos;
    private static final JLabel generationLabel = new JLabel("Génération : ");
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
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel panneauCentral = new JPanel(new GridBagLayout());
        GridBagConstraints contraintes = new GridBagConstraints();
        contraintes.fill = GridBagConstraints.VERTICAL;

        // Ajout du panneau pour afficher le numéro de génération
        contraintes.gridx = 0;
        contraintes.gridy = 0;
        panneauCentral.add(generationLabel, contraintes);

        // Ajout du plateau de cellules
        plateauDeJeu = new PlateauDeJeu();
        contraintes.gridy = 1;
        panneauCentral.add(plateauDeJeu, contraintes);
        panneauCentral.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(panneauCentral);

        // Fin de l'initialisation
        pack();
        jeu = new Thread(plateauDeJeu);

    }

    private void mettreLeJeuEnMarche(boolean marche) {

        if (marche) {
            menuJeuStart.setEnabled(false);
            menuJeuStop.setEnabled(true);
            jeu = new Thread(plateauDeJeu);
            jeu.start();
        } else {
            menuJeuStart.setEnabled(true);
            menuJeuStop.setEnabled(false);
            jeu.interrupt();
        }

    }

    protected static void setGenerationLabel(String generation) {
        generationLabel.setText(generation);
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
            panneauNouvelleGrille.setLayout(new BoxLayout(panneauNouvelleGrille, BoxLayout.Y_AXIS));
            fenetreNouvelleGrille.add(panneauNouvelleGrille);
            final JLabel labelOptions = new JLabel("Option de la nouvelle Grille");
            labelOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel labelLignes = new JLabel("Nombre le lignes (" + (800 / plateauDeJeu.getTaillePixels()) + " max) : ");
            labelLignes.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel labelColonnes = new JLabel("Nombre de colonnes (" + (800 / plateauDeJeu.getTaillePixels()) + " max) : ");
            labelColonnes.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JSpinner spinnerNouvelleGrilleHauteur = new JSpinner(new SpinnerNumberModel(100, 20, 800, 1));
            spinnerNouvelleGrilleHauteur.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JSpinner spinnerNouvelleGrilleLargeur = new JSpinner(new SpinnerNumberModel(100, 20, 800, 1));
            spinnerNouvelleGrilleLargeur.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JButton creerNouvelleGrille = new JButton("Créer la nouvelle Grille");
            creerNouvelleGrille.setAlignmentX(Component.CENTER_ALIGNMENT);
            panneauNouvelleGrille.add(labelOptions);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauNouvelleGrille.add(labelLignes);
            panneauNouvelleGrille.add(spinnerNouvelleGrilleHauteur);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauNouvelleGrille.add(labelColonnes);
            panneauNouvelleGrille.add(spinnerNouvelleGrilleLargeur);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauNouvelleGrille.add(creerNouvelleGrille);
            panneauNouvelleGrille.setBorder(BorderFactory.createLineBorder(panneauNouvelleGrille.getBackground(), 10));

            spinnerNouvelleGrilleHauteur.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    if ((int) spinnerNouvelleGrilleHauteur.getValue() * plateauDeJeu.getTaillePixels() > 800) {
                        spinnerNouvelleGrilleHauteur.setValue(800 / plateauDeJeu.getTaillePixels());
                    }

                }

            });

            spinnerNouvelleGrilleLargeur.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    if ((int) spinnerNouvelleGrilleLargeur.getValue() * plateauDeJeu.getTaillePixels() > 800) {
                        spinnerNouvelleGrilleLargeur.setValue(800 / plateauDeJeu.getTaillePixels());
                    }

                }

            });

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
            if (valeurRetourne == JFileChooser.APPROVE_OPTION) {
                try {
                    plateauDeJeu.chargerModele(Paths.get(chooser.getSelectedFile().getPath()));
                    pack();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Le fichier renseigné est introuvable");
                } catch (ModeleRenseigneNonConforme e) {
                    System.err.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "Le modèle renseigné est non-conforme, veuillez choisir un fichier .rle ou .txt avec le bon formatage de données\n" + e.getMessage());
                }
            }

        } else if (evenement.getSource().equals(menuFichierOptions)) {

            // Permettre le changement de la taille des pixels et la vitesse d'actualisation
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            final JFrame fenetreOptions = new JFrame();
            fenetreOptions.setTitle("Options");
            fenetreOptions.setResizable(false);
            JPanel panneauOptions = new JPanel();
            panneauOptions.setLayout(new BoxLayout(panneauOptions, BoxLayout.Y_AXIS));
            fenetreOptions.add(panneauOptions);
            final JSlider sliderTaillePixels = new JSlider(0, 10, plateauDeJeu.getTaillePixels());
            sliderTaillePixels.setPaintLabels(true);
            sliderTaillePixels.setPaintTicks(true);
            sliderTaillePixels.setPaintTrack(true);
            sliderTaillePixels.setMajorTickSpacing(10);
            sliderTaillePixels.setMinorTickSpacing(1);
            sliderTaillePixels.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel sliderTaillePixelsLabel = new JLabel("Taille des cellules : " + sliderTaillePixels.getValue());
            sliderTaillePixelsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JSlider sliderVitesse = new JSlider(0, 500, plateauDeJeu.getVitesseActualisation());
            sliderVitesse.setPaintLabels(true);
            sliderVitesse.setPaintTicks(true);
            sliderVitesse.setPaintTrack(true);
            sliderVitesse.setMajorTickSpacing(100);
            sliderVitesse.setMinorTickSpacing(50);
            sliderVitesse.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel sliderVitesseLabel = new JLabel("Vitesse : " + sliderVitesse.getValue() + " ms");
            sliderVitesseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JButton changerOptions = new JButton("Sauvegarder");
            changerOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
            panneauOptions.add(sliderTaillePixelsLabel);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 3)));
            panneauOptions.add(sliderTaillePixels);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauOptions.add(sliderVitesseLabel);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 3)));
            panneauOptions.add(sliderVitesse);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauOptions.add(changerOptions);
            panneauOptions.setBorder(BorderFactory.createLineBorder(panneauOptions.getBackground(), 10));

            sliderTaillePixels.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    if (sliderTaillePixels.getValue() < 1) {
                        sliderTaillePixels.setValue(1);
                    }
                    sliderTaillePixelsLabel.setText("Taille des cellules : " + sliderTaillePixels.getValue());

                }

            });

            sliderVitesse.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    if (sliderVitesse.getValue() < 5) {
                        sliderVitesse.setValue(5);
                    }
                    sliderVitesseLabel.setText("Vitesse : " + sliderVitesse.getValue() + " ms");

                }

            });

            changerOptions.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evenement) {

                    plateauDeJeu.setTaillePixels(sliderTaillePixels.getValue());
                    plateauDeJeu.setVitesseActualisation(sliderVitesse.getValue());
                    pack();
                    mettreLeJeuEnMarche(true);
                    fenetreOptions.dispose();

                }

            });

            fenetreOptions.pack();
            fenetreOptions.setLocationRelativeTo(null);
            fenetreOptions.setVisible(true);

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
                            Ce jeu est inspiré du célèbre jeu "Game of Life" de John Conway.
                            Ce projet est une création personnelle dans le but de découvrir l'environnement Java.
                            N'hésitez pas à me retrouver sur GitHub à l'adresse suivante : https://github.com/Velki0"""
            );

        }

    }

}
