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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Représentation du Jeu de la Vie de Conway
 * Pour plus d'informations sur les règles du jeu, de son invention et de son histoire, vous pouvez vous référer au lien suivant :
 * <a href="https://fr.wikipedia.org/wiki/Jeu_de_la_vie">Jeu de la Vie - Wikipédia</a>.
 * La classe JeuDeLaVie est une interface graphique proposant l'affichage du jeu et plusieurs options pour l'utilisateur.
 * Parmi ces options, nous pouvons retrouver :
 * - La création d'une table aux dimensions précisées par l'utilisateur (suivant la taille son écran).
 * - Le chargement d'un modèle prédéfini grâce à l'ouverture d'un fichier .rle ou .txt.
 * - Le changement des paramètres de taille des cellules et de la vitesse d'actualisation du jeu.
 * - Rendre le tableau de jeu actuel aléatoire.
 * - Mettre en marche et mettre pause au défilement des générations.
 * @author Velki0
 * @version 1.0
 */
public class JeuDeLaVie extends JFrame implements ActionListener {

    /** Barre de menu du jeu. */
    private final JMenuBar barreDeMenu;
    /** Onglet du menu du jeu. */
    private final JMenu menuFichier, menuJeu, menuAide;
    /** Option contenue dans l'onglet 'Fichier'. */
    private final JMenuItem menuFichierNouvelleGrille, menuFichierOuvrir, menuFichierOptions, menuFichierQuitter;
    /** Option contenue dans l'onglet 'Jeu'. */
    private final JMenuItem menuJeuAutoRemplissage, menuJeuStart, menuJeuStop, menuJeuReset;
    /** Option contenue dans l'onglet 'Aide'. */
    private final JMenuItem menuAideSource, menuAideAPropos;
    /** Affichage graphique permettant d'informer l'utilisateur sur le numéro de la génération en cours. */
    private static final JLabel generationLabel = new JLabel("Génération : ");
    /** Le plateau de jeu. */
    private final PlateauDeJeu plateauDeJeu;
    /** Paramètre de mise en marche. */
    private Thread jeu;

    /**
     * Constructeur de l'affichage graphique de l'application.
     * Cette fenêtre est composée d'un menu comportant plusieurs options ayant des effets sur la mise en place de la grille de jeu.
     * On retrouve dans son corps une indication sur la génération en cours et un plateau affichant chaque cellule du Jeu de la Vie.
     */
    public JeuDeLaVie() {

        // Initialisation du menu.
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

        // Initialisation du plateau de jeu.
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel panneauCentral = new JPanel(new GridBagLayout());
        GridBagConstraints contraintes = new GridBagConstraints();
        contraintes.fill = GridBagConstraints.VERTICAL;

        // Ajout du panneau pour afficher le numéro de génération.
        contraintes.gridx = 0;
        contraintes.gridy = 0;
        panneauCentral.add(generationLabel, contraintes);

        // Ajout du plateau de cellules.
        plateauDeJeu = new PlateauDeJeu();
        contraintes.gridy = 1;
        panneauCentral.add(plateauDeJeu, contraintes);
        panneauCentral.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(panneauCentral);

        // Fin de l'initialisation.
        pack();
        jeu = new Thread(plateauDeJeu);

    }

    /**
     * Méthode permettant de mettre en marche ou en pause le Jeu de la Vie.
     * @param marche Valeur booléenne définissant si le jeu doit se mettre en marche ('true') ou en pause ('false').
     */
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

    /**
     * Setter afin d'afficher le nouveau numéro de génération sur l'interface graphique.
     * @param generation Numéro de la génération.
     */
    protected static void setGenerationLabel(String generation) { generationLabel.setText(generation); }

    @Override
    public void actionPerformed(ActionEvent evenement) {

        // Mise en place d'une hauteur et une largeur maximales pour l'affichage en fonction de l'écran de l'utilisateur.
        int hauteurMax = (Math.round((float) Toolkit.getDefaultToolkit().getScreenSize().height / 100) * 100) - 200;
        int largeurMax = (Math.round((float) Toolkit.getDefaultToolkit().getScreenSize().width / 100) * 100) - 100;

        if (evenement.getSource() == menuFichierNouvelleGrille) {

            // Création d'une nouvelle grille vierge de taille renseignée par l'utilisateur.
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            final JFrame fenetreNouvelleGrille = new JFrame();
            fenetreNouvelleGrille.setTitle("Nouvelle Grille");
            fenetreNouvelleGrille.setResizable(false);
            JPanel panneauNouvelleGrille = new JPanel();
            panneauNouvelleGrille.setLayout(new BoxLayout(panneauNouvelleGrille, BoxLayout.Y_AXIS));
            fenetreNouvelleGrille.add(panneauNouvelleGrille);
            final JLabel labelOptions = new JLabel("<html><body style='text-align:center;'>Option de la nouvelle Grille<br>La taille maximale dépend de votre taille d'écran et de la taille des cellules.</body></html>");
            labelOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel labelLignes = new JLabel("Nombre le lignes (" + (hauteurMax / plateauDeJeu.getTailleCellules()) + " max) : ");
            labelLignes.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel labelColonnes = new JLabel("Nombre de colonnes (" + (largeurMax / plateauDeJeu.getTailleCellules()) + " max) : ");
            labelColonnes.setAlignmentX(Component.CENTER_ALIGNMENT);
            Dimension dimensionSpinners = new Dimension(50, 20);
            final JSpinner spinnerNouvelleGrilleHauteur = new JSpinner(new SpinnerNumberModel(100, 20, hauteurMax, 1));
            spinnerNouvelleGrilleHauteur.setAlignmentX(Component.CENTER_ALIGNMENT);
            spinnerNouvelleGrilleHauteur.setMaximumSize(dimensionSpinners);
            final JSpinner spinnerNouvelleGrilleLargeur = new JSpinner(new SpinnerNumberModel(100, 20, largeurMax, 1));
            spinnerNouvelleGrilleLargeur.setAlignmentX(Component.CENTER_ALIGNMENT);
            spinnerNouvelleGrilleLargeur.setMaximumSize(dimensionSpinners);
            final JButton creerNouvelleGrille = new JButton("Créer la nouvelle Grille");
            creerNouvelleGrille.setAlignmentX(Component.CENTER_ALIGNMENT);
            panneauNouvelleGrille.add(labelOptions);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauNouvelleGrille.add(labelLignes);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 5)));
            panneauNouvelleGrille.add(spinnerNouvelleGrilleHauteur);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauNouvelleGrille.add(labelColonnes);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 5)));
            panneauNouvelleGrille.add(spinnerNouvelleGrilleLargeur);
            panneauNouvelleGrille.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauNouvelleGrille.add(creerNouvelleGrille);
            panneauNouvelleGrille.setBorder(BorderFactory.createLineBorder(panneauNouvelleGrille.getBackground(), 10));

            spinnerNouvelleGrilleHauteur.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    // Protection pour que l'utilisateur ne renseigne pas une hauteur trop grande qui rendrait l'application plus grande que l'écran.
                    if ((int) spinnerNouvelleGrilleHauteur.getValue() * plateauDeJeu.getTailleCellules() > hauteurMax) {
                        spinnerNouvelleGrilleHauteur.setValue(hauteurMax / plateauDeJeu.getTailleCellules());
                    }

                }

            });

            spinnerNouvelleGrilleLargeur.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    // Protection pour que l'utilisateur ne renseigne pas une largeur trop grande qui rendrait l'application plus grande que l'écran.
                    if ((int) spinnerNouvelleGrilleLargeur.getValue() * plateauDeJeu.getTailleCellules() > largeurMax) {
                        spinnerNouvelleGrilleLargeur.setValue(largeurMax / plateauDeJeu.getTailleCellules());
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

            // Permettre l'ouverture d'un modèle prédéfini sous le format .rle ou .txt.
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

            // Permettre le changement de la taille des pixels et la vitesse d'actualisation.
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            final JFrame fenetreOptions = new JFrame();
            fenetreOptions.setTitle("Options");
            fenetreOptions.setResizable(false);
            JPanel panneauOptions = new JPanel();
            panneauOptions.setLayout(new BoxLayout(panneauOptions, BoxLayout.Y_AXIS));
            fenetreOptions.add(panneauOptions);
            final JSlider sliderTailleCellules = new JSlider(0, 10, plateauDeJeu.getTailleCellules());
            sliderTailleCellules.setPaintLabels(true);
            sliderTailleCellules.setPaintTicks(true);
            sliderTailleCellules.setPaintTrack(true);
            sliderTailleCellules.setMajorTickSpacing(10);
            sliderTailleCellules.setMinorTickSpacing(1);
            sliderTailleCellules.setAlignmentX(Component.CENTER_ALIGNMENT);
            final JLabel sliderTailleCellulesLabel = new JLabel("Taille des cellules : " + sliderTailleCellules.getValue());
            sliderTailleCellulesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
            panneauOptions.add(sliderTailleCellulesLabel);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 3)));
            panneauOptions.add(sliderTailleCellules);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauOptions.add(sliderVitesseLabel);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 3)));
            panneauOptions.add(sliderVitesse);
            panneauOptions.add(Box.createRigidArea(new Dimension(0, 10)));
            panneauOptions.add(changerOptions);
            panneauOptions.setBorder(BorderFactory.createLineBorder(panneauOptions.getBackground(), 10));

            sliderTailleCellules.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    // Quelques protections pour que l'utilisateur ne renseigne pas une taille de cellule trop grande qui rendrait l'application plus grande que l'écran.
                    if (sliderTailleCellules.getValue() < 1) {
                        sliderTailleCellules.setValue(1);
                    }
                    if (sliderTailleCellules.getValue() > hauteurMax / plateauDeJeu.getHauteurPlateau()) {
                        sliderTailleCellules.setValue(Math.round((float) hauteurMax / plateauDeJeu.getHauteurPlateau()) - 1);
                    }
                    if (sliderTailleCellules.getValue() > largeurMax / plateauDeJeu.getLargeurPlateau()) {
                        sliderTailleCellules.setValue(Math.round((float) largeurMax / plateauDeJeu.getLargeurPlateau()) - 1);
                    }
                    sliderTailleCellulesLabel.setText("Taille des cellules : " + sliderTailleCellules.getValue());

                }

            });

            sliderVitesse.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    // Protection pour que l'utilisateur ne renseigne pas une vitesse d'actualisation trop petite.
                    if (sliderVitesse.getValue() < 5) {
                        sliderVitesse.setValue(5);
                    }
                    sliderVitesseLabel.setText("Vitesse : " + sliderVitesse.getValue() + " ms");

                }

            });

            changerOptions.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evenement) {

                    plateauDeJeu.setTailleCellules(sliderTailleCellules.getValue());
                    plateauDeJeu.setVitesseActualisation(sliderVitesse.getValue());
                    pack();
                    fenetreOptions.dispose();

                }

            });

            fenetreOptions.pack();
            fenetreOptions.setLocationRelativeTo(null);
            fenetreOptions.setVisible(true);

        } else if (evenement.getSource().equals(menuFichierQuitter)) {

            // Bouton Quitter.
            System.exit(0);

        } else if (evenement.getSource().equals(menuJeuAutoRemplissage)) {

            // Remplissage Auto.
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            plateauDeJeu.autoRemplissage();

        } else if (evenement.getSource().equals(menuJeuStart)) {

            // Mettre le jeu en route.
            mettreLeJeuEnMarche(true);

        } else if (evenement.getSource().equals(menuJeuStop)) {

            // Mettre le jeu en pause.
            mettreLeJeuEnMarche(false);

        } else if (evenement.getSource().equals(menuJeuReset)) {

            // Réinitialiser la grille au format actuel avec uniquement des cases vides.
            if (jeu.isAlive()) {
                mettreLeJeuEnMarche(false);
            }
            plateauDeJeu.reinitialiserGrille();
            plateauDeJeu.repaint();

        } else if (evenement.getSource().equals(menuAideSource)) {

            // Source du projet.
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

            // À propos du projet.
            JOptionPane.showMessageDialog(null,
                    """
                            Ce jeu est inspiré du célèbre jeu "Game of Life" de John Conway.
                            Ce projet est une création personnelle dans le but de découvrir l'environnement Java.
                            N'hésitez pas à me retrouver sur GitHub à l'adresse suivante : https://github.com/Velki0"""
            );

        }

    }

}
