package VueControleur;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.naming.ldap.Control;
import javax.swing.*;

import modele.deplacements.*;
import modele.plateau.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleurGyromite extends JFrame implements Observer {
    private Jeu jeu; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private static TileEditor tileEditor;
    private int sizeX; // taille de la grille affichée
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoHeroDroite;
    private ImageIcon icoHeroGauche;
    private ImageIcon icoHeroGrimpe;

    private ImageIcon icoBotDroite;
    private ImageIcon icoBotGauche;
    private ImageIcon icoBotGrimpe;

    private ImageIcon icoVide;
    private ImageIcon icoMurHorizontal;
    private ImageIcon icoMurVertical;
    private ImageIcon icoMurBrique;
    private ImageIcon icoBombe;
    private ImageIcon icoLiane;
    private ImageIcon icoSupportColonne;
    private ImageIcon icoColonne;
    private ImageIcon icoColonneR;

    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    private JLabel[][] tabJLabelTilesEditor; // cases graphique pour tileseditor (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    private JLabel[][] tabChoiceTilesEditor; // cases graphique pour tileseditor (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    private JFrame menuPrincipal, TilesEditor, Regles;
    private JPanel menu;

    public VueControleurGyromite(Jeu _jeu, TileEditor _tileEditor) {
        sizeX = jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;
        tileEditor = _tileEditor;


        chargerLesIcones();
        placerLesComposantsGraphiques();
        ajouterEcouteurClavier();
    }

    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT -> Controle4Directions.getInstance().setDirectionCourante(Direction.gauche);
                    case KeyEvent.VK_RIGHT -> Controle4Directions.getInstance().setDirectionCourante(Direction.droite);
                    case KeyEvent.VK_DOWN -> Controle4Directions.getInstance().setDirectionCourante(Direction.bas);
                    case KeyEvent.VK_UP -> Controle4Directions.getInstance().setDirectionCourante(Direction.haut);
                    case KeyEvent.VK_Z -> ColonneDepl.getInstance().setDirectionCourante();
                    case KeyEvent.VK_A -> ColonneDeplB.getInstance().setDirectionCourante();
                    case KeyEvent.VK_ESCAPE -> {
                        setVisible(false);
                        dispose();
                        menuPrincipal.setVisible(true);

                    }
                }
            }
        });
    }


    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/player.png", 160, 0, 32, 38);
        icoHeroGauche = chargerIcone("Images/player.png", 0, 0, 32, 38);
        icoHeroDroite = chargerIcone("Images/player-2.png", 160, 0, 32, 38);
        icoHeroGrimpe = chargerIcone("Images/player.png", 0, 88, 32, 38);

        icoBotGauche = chargerIcone("Images/smick.png", 0, 0, 32, 32);
        icoBotDroite = chargerIcone("Images/smick-2.png", 96, 0, 32, 32);
        icoBotGrimpe = chargerIcone("Images/smick.png", 0, 96, 32, 32);


        icoVide = chargerIcone("Images/tileset.png", 64, 0, 16, 14);

        icoColonne = chargerIcone("Images/tileset.png", 16, 48, 16, 16);

        icoColonneR = chargerIcone("Images/tileset.png", 16, 80, 16, 16);

        icoMurHorizontal = chargerIcone("Images/tileset.png", 0, 1, 16 ,14);

        icoMurVertical = chargerIcone("Images/tileset.png", 2, 16, 12, 16);

        icoMurBrique = chargerIcone("Images/tileset.png", 33, 1, 15, 15);

        icoBombe = chargerIcone("Images/bomb.png", 20, 16, 26, 32);

        icoLiane = chargerIcone("Images/tileset.png", 17, 0, 14, 16);

        icoSupportColonne = chargerIcone("Images/tileset.png", 17, 18, 16, 14);

    }

    private void placerLesComposantsGraphiques() {

        // ----------------------------------Menu Principal-------------------------------------------------------------

        PlacerComposantGraphiqueMenuPrincipale();

        // ----------------------------------Fenêtre de Jeu-------------------------------------------------------------
        setTitle("Gyromite");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre
        setResizable(false); //Rend inajustable la taille de la fenêtre

        // On place les composants graphiques
        JPanel panneauPrincipal = new JPanel();
        panneauPrincipal.setLayout(new BorderLayout());
        setContentPane(panneauPrincipal);

        // On place le menu d'infos en haut
        menu = new JPanel();
        menu.setLayout(new GridLayout(1, 3));
        panneauPrincipal.add(menu, BorderLayout.NORTH);

        // on place le le score, le nombre de vie et nombre de bombes dans le menu
        JLabel score = new JLabel("", SwingConstants.CENTER);
        JLabel vie = new JLabel("", SwingConstants.CENTER);
        JLabel bombes = new JLabel("", SwingConstants.CENTER);
        menu.add(score, BorderLayout.WEST);
        menu.add(vie, BorderLayout.CENTER);
        menu.add(bombes, BorderLayout.EAST);

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                grilleJLabels.add(jlab);
            }
        }
        panneauPrincipal.add(grilleJLabels, BorderLayout.CENTER);


        // ----------------------------------Fenêtre TilesEditor--------------------------------------------------------

        PlacerComposantGraphiqueTileEditor();

        // ----------------------------------Fenêtre Règles-------------------------------------------------------------

        PlacerComposantsGraphiqueRegles();

    }

    public void PlacerComposantGraphiqueMenuPrincipale()
    {
        menuPrincipal = new JFrame();
        menuPrincipal.setTitle("Gyromite-Menu");
        menuPrincipal.setSize(800, 600);
        menuPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panelMenuPrincipal = new JPanel();
        panelMenuPrincipal.setLayout(new GridLayout(5, 1));
        menuPrincipal.setResizable(false); //Rend inajustable la taille de la fenêtre
        menuPrincipal.setLocationRelativeTo(null);// Centre le menu principal

        // Ajout du titre
        JLabel titre = new JLabel("Gyromite", SwingConstants.CENTER);
        // titre plus jolie
        titre.setFont(new Font("Arial", Font.BOLD, 50));
        titre.setForeground(Color.BLACK);
        titre.setOpaque(true);

        panelMenuPrincipal.add(titre);


        //----------------------------------------Bouton Jouer----------------------------------------------------------

        JButton boutonJouer = new JButton("Jouer");
        boutonJouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuPrincipal.dispose(); // on ferme la fenêtre
                jeu.ResetAll();
                jeu.LoadLevel(0);
                setVisible(true); // on affiche la fenêtre de jeu
            }
        });
        panelMenuPrincipal.add(boutonJouer);

        // Bouton plus joli
        boutonJouer.setBackground(Color.WHITE);
        boutonJouer.setForeground(Color.BLACK);
        boutonJouer.setFont(new Font("Arial", Font.BOLD, 20));
        boutonJouer.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonJouer.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonJouer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonJouer.setBackground(Color.LIGHT_GRAY);
                boutonJouer.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonJouer.setBackground(Color.WHITE);
                boutonJouer.setForeground(Color.BLACK);
            }
        });
        // -----------------------------------Bouton TilesEditor -------------------------------------------------------

        JButton boutonTilesEditor = new JButton("TilesEditor");
        boutonTilesEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuPrincipal.setVisible(false); // sinon bug avec le hover
                TilesEditor.setVisible(true);
            }
        });
        panelMenuPrincipal.add(boutonTilesEditor);
        // Bouton plus joli
        boutonTilesEditor.setBackground(Color.WHITE);
        boutonTilesEditor.setForeground(Color.BLACK);
        boutonTilesEditor.setFont(new Font("Arial", Font.BOLD, 20));
        boutonTilesEditor.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonTilesEditor.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonTilesEditor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonTilesEditor.setBackground(Color.LIGHT_GRAY);
                boutonTilesEditor.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonTilesEditor.setBackground(Color.WHITE);
                boutonTilesEditor.setForeground(Color.BLACK);
            }
        });

        // -----------------------------------Bouton Regles ------------------------------------------------------------

        JButton boutonRegles = new JButton("Règles");

        boutonRegles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuPrincipal.setVisible(false); // sinon bug avec le hover
                Regles.setVisible(true);
            }
        });

        panelMenuPrincipal.add(boutonRegles);
        // Bouton plus joli
        boutonRegles.setBackground(Color.WHITE);
        boutonRegles.setForeground(Color.BLACK);
        boutonRegles.setFont(new Font("Arial", Font.BOLD, 20));
        boutonRegles.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonRegles.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonRegles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonRegles.setBackground(Color.LIGHT_GRAY);
                boutonRegles.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonRegles.setBackground(Color.WHITE);
                boutonRegles.setForeground(Color.BLACK);
            }
        });

        // ------------------------------------- Bouton Quitter --------------------------------------------------------

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panelMenuPrincipal.add(boutonQuitter);

        // Bouton plus joli
        boutonQuitter.setBackground(Color.WHITE);
        boutonQuitter.setForeground(Color.BLACK);
        boutonQuitter.setFont(new Font("Arial", Font.BOLD, 20));
        boutonQuitter.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonQuitter.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton

        //ajout d'un hover sur le bouton
        boutonQuitter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonQuitter.setBackground(Color.LIGHT_GRAY);
                boutonQuitter.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonQuitter.setBackground(Color.WHITE);
                boutonQuitter.setForeground(Color.BLACK);
            }
        });

        menuPrincipal.add(panelMenuPrincipal); // on ajoute le panel au menu principal

    }

    public void PlacerComposantGraphiqueTileEditor()
    {
        TilesEditor = new JFrame("TilesEditor");
        TilesEditor.setSize(1280, 720);
        TilesEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TilesEditor.setLocationRelativeTo(null); // Centre la fenêtre
        TilesEditor.setResizable(false); //Rend inajustable la taille de la fenêtre

        // On place la grille de Tiles
        JComponent grilleJLabelsTilesEditor = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabelTilesEditor va contenir les cases graphiques et les positionner sous la forme d'une grille
        tabJLabelTilesEditor = new CaseTileEditor[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                tabJLabelTilesEditor[x][y] = new CaseTileEditor();; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                tabJLabelTilesEditor[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                int finalX = x; // On doit créer des variables final pour pouvoir les utiliser dans les listeners
                int finalY = y;
                // On ajoute un listener sur chaque case pour pouvoir cliquer dessus
                tabJLabelTilesEditor[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Si on clic avec le bouton gauche
                        if(e.getButton() == MouseEvent.BUTTON1){
                            tileEditor.setEntiteGrilleETile(finalX, finalY); // On set l'entite sur la case
                        }
                        // Si on clic avec le bouton droit
                        else if(e.getButton() == MouseEvent.BUTTON3){
                            tileEditor.EraseEntiteGrilleETile(finalX, finalY); // On efface l'entité sur la case
                        }
                    }
                });

                grilleJLabelsTilesEditor.add(tabJLabelTilesEditor[x][y]); // On ajoute la case à la grilleJLabelTilesEditor
            }
        }

        TilesEditor.add(grilleJLabelsTilesEditor, BorderLayout.CENTER); // On ajoute la grilleJLabelTilesEditor à la fenêtre TilesEditor

        // Ajout d'un menu a droite de la fenetre
        JPanel panelTilesEditor = new JPanel();
        panelTilesEditor.setLayout(new GridLayout(15, 1)); // On creer un grille pour placer les boutons et les cases de selection

        // ----------------------------------------------- Bouton Quitter ----------------------------------------------

        JButton boutonQuitterTilesEditor = new JButton("Quitter");
        boutonQuitterTilesEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TilesEditor.dispose();
                menuPrincipal.setVisible(true);
            }
        });
        // Bouton plus joli
        boutonQuitterTilesEditor.setBackground(Color.WHITE);
        boutonQuitterTilesEditor.setForeground(Color.BLACK);
        boutonQuitterTilesEditor.setFont(new Font("Arial", Font.BOLD, 20));
        boutonQuitterTilesEditor.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonQuitterTilesEditor.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonQuitterTilesEditor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonQuitterTilesEditor.setBackground(Color.LIGHT_GRAY);
                boutonQuitterTilesEditor.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonQuitterTilesEditor.setBackground(Color.WHITE);
                boutonQuitterTilesEditor.setForeground(Color.BLACK);
            }
        });
        panelTilesEditor.add(boutonQuitterTilesEditor); // On ajoute le bouton quitter au panelTilesEditor

        //---------------------------------------------- Bouton Sauver -------------------------------------------------

        JButton boutonSauver = new JButton("Sauver");
        boutonSauver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tileEditor.SaveInFile(); // On sauvegarde la grille dans un fichier
            }
        });
        // Bouton plus joli
        boutonSauver.setBackground(Color.WHITE);
        boutonSauver.setForeground(Color.BLACK);
        boutonSauver.setFont(new Font("Arial", Font.BOLD, 20));
        boutonSauver.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonSauver.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonSauver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonSauver.setBackground(Color.LIGHT_GRAY);
                boutonSauver.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonSauver.setBackground(Color.WHITE);
                boutonSauver.setForeground(Color.BLACK);
            }
        });
        panelTilesEditor.add(boutonSauver); // On ajoute le bouton Sauver au panelTilesEditor

        // ------------------------------------------------ Bouton New -------------------------------------------------

        JButton boutonNew = new JButton("New");
        boutonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tileEditor.Reset(); // On reset la grille
            }
        });
        // Bouton plus joli
        boutonNew.setBackground(Color.WHITE);
        boutonNew.setForeground(Color.BLACK);
        boutonNew.setFont(new Font("Arial", Font.BOLD, 20));
        boutonNew.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonNew.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonNew.setBackground(Color.LIGHT_GRAY);
                boutonNew.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonNew.setBackground(Color.WHITE);
                boutonNew.setForeground(Color.BLACK);
            }
        });
        panelTilesEditor.add(boutonNew); // On ajoute le bouton New au panelTilesEditor

        // ------------------------------------------------- Bouton Charger --------------------------------------------

        JButton boutonCharger = new JButton("Charger");
        boutonCharger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tileEditor.LoadFromFile(); // On charge la grille depuis un fichier
            }
        });
        // Bouton plus joli
        boutonCharger.setBackground(Color.WHITE);
        boutonCharger.setForeground(Color.BLACK);
        boutonCharger.setFont(new Font("Arial", Font.BOLD, 20));
        boutonCharger.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonCharger.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonCharger.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonCharger.setBackground(Color.LIGHT_GRAY);
                boutonCharger.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonCharger.setBackground(Color.WHITE);
                boutonCharger.setForeground(Color.BLACK);
            }
        });

        panelTilesEditor.add(boutonCharger); // On ajoute le bouton Charger au panelTilesEditor


        // ------------------------------------------------ Bouton Jouer -----------------------------------------------

        JButton boutonJouer = new JButton("Jouer");
        boutonJouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // on reset tout
                if (tileEditor.getLancerJeu()){
                    jeu.ResetAll();
                    // On lance le jeu avec la grille sauvegardée
                    jeu.LoadLevel(1);
                    setVisible(true);
                    tileEditor.SaveInFile(); // On sauvegarde la grille dans un fichier
                    TilesEditor.dispose();
                }
                else
                {
                    tileEditor.SaveInFile(); // On sauvegarde la grille dans un fichier
                    System.out.println("Le jeu n'a pas été lancé");
                }


            }
        });
        // Bouton plus joli
        boutonJouer.setBackground(Color.WHITE);
        boutonJouer.setForeground(Color.BLACK);
        boutonJouer.setFont(new Font("Arial", Font.BOLD, 20));
        boutonJouer.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonJouer.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonJouer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonJouer.setBackground(Color.LIGHT_GRAY);
                boutonJouer.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonJouer.setBackground(Color.WHITE);
                boutonJouer.setForeground(Color.BLACK);
            }
        });
        panelTilesEditor.add(boutonJouer); // On ajoute le bouton Jouer au panelTilesEditor



        // ---------------------------------------------- Cases Selection ----------------------------------------------

        tabChoiceTilesEditor = new CaseChoixTileEditor[1][10]; // On creer un tableau de 10 cases de selection
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 1; x++) {
                tabChoiceTilesEditor[x][y] = new CaseChoixTileEditor(); // On creer une case de selection
                tabChoiceTilesEditor[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                //fait en sorte que l'image soit centré
                tabChoiceTilesEditor[x][y].setHorizontalAlignment(SwingConstants.CENTER);
                tabChoiceTilesEditor[x][y].setVerticalAlignment(SwingConstants.CENTER);

                int finalX = x; // On doit créer des variables final pour pouvoir les utiliser dans les listeners
                int finalY = y; // On doit créer des variables final pour pouvoir les utiliser dans les listeners
                tabChoiceTilesEditor[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        switch (tabChoiceTilesEditor[finalX][finalY].getName()) {
                            case "MurVertical" -> tileEditor.setEntiteCourante(new MurVertical(jeu));
                            case "MurHorizontal" -> tileEditor.setEntiteCourante(new Mur(jeu));
                            case "MurBrique" -> tileEditor.setEntiteCourante(new MurBrique(jeu));
                            case "SupportPilier" -> tileEditor.setEntiteCourante(new SupportColonne(jeu));
                            case "Bombe" -> tileEditor.setEntiteCourante(new Bombe(jeu));
                            case "Liane" -> tileEditor.setEntiteCourante(new Liane(jeu));
                            case "Monstre" -> tileEditor.setEntiteCourante(new Bot(jeu));
                            case "Colonne" -> tileEditor.setEntiteCourante(new Colonne(jeu));
                            case "ColonneR" -> tileEditor.setEntiteCourante(new ColonneR(jeu));
                            case "Heros" -> tileEditor.setEntiteCourante(new Heros(jeu));
                        }
                    }
                });
                panelTilesEditor.add(tabChoiceTilesEditor[x][y], BorderLayout.CENTER);
            }
        }
        // On donne un nom a chaque case de selection pour pouvoir identifier quelle entite lui est associé
        tabChoiceTilesEditor[0][0].setName("MurVertical");
        tabChoiceTilesEditor[0][1].setName("MurHorizontal");
        tabChoiceTilesEditor[0][2].setName("MurBrique");
        tabChoiceTilesEditor[0][3].setName("SupportPilier");
        tabChoiceTilesEditor[0][4].setName("Bombe");
        tabChoiceTilesEditor[0][5].setName("Monstre");
        tabChoiceTilesEditor[0][6].setName("Liane");
        tabChoiceTilesEditor[0][7].setName("Colonne");
        tabChoiceTilesEditor[0][8].setName("ColonneR");
        tabChoiceTilesEditor[0][9].setName("Heros");

        TilesEditor.add(panelTilesEditor, BorderLayout.EAST);
    }

    public void PlacerComposantsGraphiqueRegles(){

        Regles = new JFrame("Règles");
        Regles.setSize(1000, 800);
        Regles.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Regles.setLocationRelativeTo(null);
        Regles.setResizable(false);
        Regles.setLayout(new BorderLayout());
        Regles.setBackground(Color.lightGray);


        // ---------------------------------------------- Panel Regles ----------------------------------------------

        JPanel panelRegles = new JPanel();
        panelRegles.setLayout(new BorderLayout());
        panelRegles.setBackground(Color.lightGray);

        // ---------------------------------------------- Titre Regles ----------------------------------------------

        JLabel titreRegles = new JLabel("Règles du jeu");
        titreRegles.setFont(new Font("Arial", Font.BOLD, 30));
        titreRegles.setForeground(Color.WHITE);
        titreRegles.setHorizontalAlignment(SwingConstants.CENTER);
        panelRegles.add(titreRegles, BorderLayout.NORTH);

        // ---------------------------------------------- Texte Regles ----------------------------------------------

        JTextArea texteRegles = new JTextArea();
        texteRegles.append("\n Le but du jeu est de ramasser toutes les bombes disponible dans les niveaux\n");
        texteRegles.append(" La partie s'arrête lorsque toutes les bombes sont ramassez ou que vous n'avez plus de vie.\n");

        texteRegles.append("\n Deux types de piliers sont placés dans la map ( Rouge et Bleu ).\n");
        texteRegles.append(" Vous avez la possibilités de monter et descendre grâce au piliers mais aussi de se faire \n écraser.\n");

        texteRegles.append("\n Des monstres sont placés dans les niveaux, vous pouvez les écraser :\n");
        texteRegles.append(" - En tombant dessus tout en appuyant sur la touche 'bas'. \n");
        texteRegles.append(" - En les écrasant grâce au piliers. \n");
        texteRegles.append("\n Si vous avancez sur eux vous perdez une vie. \n");

        texteRegles.append("\n Règles du TileEditor :\n");
        texteRegles.append("\n Vous pouvez créer votre propre niveaux en utilisant le TileEditor ( voir exemple map sur internet ).\n");
        texteRegles.append(" Vous pouvez : \n");
        texteRegles.append(" Sauvegarder votre map -> Sauver \n");
        texteRegles.append(" Effacer la map en cours -> New \n");
        texteRegles.append(" Charger une map ( précédemment sauver )-> Charger \n");
        texteRegles.append(" Jouer à votre map directement -> Jouer \n");
        texteRegles.append("\n Attention : \n");
        texteRegles.append(" Vous ne pouvez pas sauvegarder une map si vous n'avez pas placé de Heros et une bombe.\n");
        texteRegles.append(" Placement des colonnes : \n");
        texteRegles.append(" Il est important de placer une seul colonne poour l'entièreté d'un pilier.\n");
        texteRegles.append(" - Pour les rouges, il faut placer une colonne à coté du support de pilier car elle sont dirigé vers le haut, de base.\n");
        texteRegles.append(" - Pour les bleus, il faut placer une colonne deux cases en dessous du support car elle sont dirigé vers le bas, de base.\n");

        texteRegles.append("\n Contrôles : \n");
        texteRegles.append("\n - Déplacement joueur :  flèches haut, bas, gauche, droite \n");
        texteRegles.append(" - Déplacement piliers : touche 'a' pour les rouges, touche 'z' pour les bleus\n");



        texteRegles.setFont(new Font("Arial", Font.PLAIN, 17));
        texteRegles.setForeground(Color.white);
        texteRegles.setBackground(Color.lightGray);
        texteRegles.setEditable(false);
        texteRegles.setLineWrap(true);
        texteRegles.setWrapStyleWord(true);
        panelRegles.add(texteRegles, BorderLayout.CENTER);


        // ---------------------------------------------- Bouton Quitter ----------------------------------------------

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.setFont(new Font("Arial", Font.BOLD, 20));
        boutonQuitter.setBackground(Color.WHITE);
        boutonQuitter.setForeground(Color.BLACK);
        boutonQuitter.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        boutonQuitter.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Regles.setVisible(false);
                Regles.dispose();
                menuPrincipal.setVisible(true);
            }
        });
        // Bouton plus joli
        boutonQuitter.setBackground(Color.WHITE);
        boutonQuitter.setForeground(Color.BLACK);
        boutonQuitter.setFont(new Font("Arial", Font.BOLD, 20));
        boutonQuitter.setFocusable(false); // Enlève le contour bleu autour du bouton
        boutonQuitter.setBorder(BorderFactory.createEtchedBorder()); // Ajoute un contour au bouton
        //ajout d'un hover sur le bouton
        boutonQuitter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boutonQuitter.setBackground(Color.LIGHT_GRAY);
                boutonQuitter.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boutonQuitter.setBackground(Color.WHITE);
                boutonQuitter.setForeground(Color.BLACK);
            }
        });

        panelRegles.add(boutonQuitter, BorderLayout.SOUTH);

        Regles.add(panelRegles, BorderLayout.CENTER);


    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        ((JLabel)menu.getComponent(0)).setText("Score : " + jeu.getScore()); // on met à jour la label score
        ((JLabel)menu.getComponent(1)).setText("Vie : " + jeu.getNbVie()); // on met à jour la label vies
        ((JLabel)menu.getComponent(2)).setText("Bombes : " + jeu.getBombe_restante()); // on met à jour la label bombes
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                // ------------------------------------------- Heros ---------------------------------------------------

                if (jeu.getGrille()[x][y][1] instanceof Heros) { // si la grille du modèle contient un Pacman, on associe l'icône Pacman du côté de la vue

                    Direction dir = Controle4Directions.getDirectionPrecedente(); // on récupère la direction précédente du heros

                    if(jeu.getGrille()[x][y][0] instanceof Liane)
                    {
                        tabJLabel[x][y].setIcon(icoHeroGrimpe);
                    }
                    else if (dir == Direction.gauche) {
                        tabJLabel[x][y].setIcon(icoHeroGauche);
                    }
                    else if (dir == Direction.droite) {
                        tabJLabel[x][y].setIcon(icoHeroDroite);
                    }
                    else {
                        tabJLabel[x][y].setIcon(icoHero);
                    }
                    // -----------------------------------------------------------------------------------------------------

                    // ------------------------------------------- Monstre -------------------------------------------------

                } else if (jeu.getGrille()[x][y][1] instanceof Bot) {
                    Direction dir2 = ((Bot) jeu.getGrille()[x][y][1]).getDirectionCourante();
                    if (jeu.getGrille()[x][y][0] instanceof Liane) {
                        tabJLabel[x][y].setIcon(icoBotGrimpe);
                    }
                    else if (dir2 == Direction.gauche) {
                        tabJLabel[x][y].setIcon(icoBotGauche);
                    } else if (dir2 == Direction.droite) {
                        tabJLabel[x][y].setIcon(icoBotDroite);
                    }
                    else {
                        tabJLabel[x][y].setIcon(icoBotGauche);
                    }
                    // -----------------------------------------------------------------------------------------------------

                } else if (jeu.getGrille()[x][y][0] instanceof Mur) {
                    tabJLabel[x][y].setIcon(icoMurHorizontal);
                } else if (jeu.getGrille()[x][y][0] instanceof MurVertical) {
                    tabJLabel[x][y].setIcon(icoMurVertical);
                } else if (jeu.getGrille()[x][y][0] instanceof MurBrique) {
                    tabJLabel[x][y].setIcon(icoMurBrique);
                } else if (jeu.getGrille()[x][y][0] instanceof SupportColonne) {
                    tabJLabel[x][y].setIcon(icoSupportColonne);
                } else if (jeu.getGrille()[x][y][0] instanceof Liane) {
                    tabJLabel[x][y].setIcon(icoLiane);
                } else if (jeu.getGrille()[x][y][0] instanceof Bombe) {
                    tabJLabel[x][y].setIcon(icoBombe);
                } else if (jeu.getGrille()[x][y][1] instanceof Colonne) {
                    tabJLabel[x][y].setIcon(icoColonne);
                }
                else if (jeu.getGrille()[x][y][1] instanceof ColonneR) {
                    tabJLabel[x][y].setIcon(icoColonneR);
                }
                else {
                    tabJLabel[x][y].setIcon(icoVide);
                }

                // ------------------------------------------- TileEditor ----------------------------------------------

                if(tileEditor.getGrilleETile()[x][y] instanceof Mur)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoMurHorizontal);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof MurVertical)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoMurVertical);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof MurBrique)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoMurBrique);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof SupportColonne)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoSupportColonne);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof Liane)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoLiane);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof Bombe)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoBombe);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof Colonne)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoColonne);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof ColonneR)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoColonneR);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof Bot)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoBotGauche);
                }
                else if(tileEditor.getGrilleETile()[x][y] instanceof Heros)
                {
                    tabJLabelTilesEditor[x][y].setIcon(icoHero);
                }
                else {
                    tabJLabelTilesEditor[x][y].setIcon(null);
                }
                // -----------------------------------------------------------------------------------------------------
            }
            // ------------------------------------ TileEditor - ChoiceTiles -------------------------------------------

            for(int y = 0; y < 10; y++)
            {
                if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "MurVertical"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoMurVertical);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "MurHorizontal"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoMurHorizontal);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "MurBrique"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoMurBrique);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "SupportPilier"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoSupportColonne);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "Liane"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoLiane);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "Bombe"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoBombe);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "Monstre"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoBotGauche);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "Colonne"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoColonne);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "ColonneR"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoColonneR);
                }
                else if(Objects.equals(tabChoiceTilesEditor[0][y].getName(), "Heros"))
                {
                    tabChoiceTilesEditor[0][y].setIcon(icoHero);
                }
            }
            // ---------------------------------------------------------------------------------------------------------
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(jeu.GameIsFinished() == 1)
        {
            JOptionPane.showMessageDialog(this, "Game Over");
            System.exit(0);
        }
        else if (jeu.GameIsFinished() == 2)
        {
            JOptionPane.showMessageDialog(this, "You Win");
            System.exit(0);
        }
        else
            mettreAJourAffichage();

        /*
        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                });
        */


    }

    public void AfficherMenu(){
        setVisible(false);
        menuPrincipal.setVisible(true);

    }

    // chargement de l'image entière comme icone
    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurGyromite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }


        return new ImageIcon(image);
    }

    // chargement d'une sous partie de l'image
    private ImageIcon chargerIcone(String urlIcone, int x, int y, int w, int h) {
        // charger une sous partie de l'image à partir de ses coordonnées dans urlIcone
        BufferedImage bi = getSubImage(urlIcone, x, y, w, h);
        // adapter la taille de l'image a la taille du composant (ici : 20x20)
        assert bi != null;
        return new ImageIcon(bi.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
    }

    private BufferedImage getSubImage(String urlIcone, int x, int y, int w, int h) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurGyromite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        BufferedImage bi = image.getSubimage(x, y, w, h);
        return bi;
    }

}