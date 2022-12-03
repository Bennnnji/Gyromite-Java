package VueControleur;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    private int sizeX; // taille de la grille affichée
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoHeroDroite;
    private ImageIcon icoHeroGauche;
    private ImageIcon icoHeroGrimpe;

    private ImageIcon icoHeroHaut;

    private ImageIcon icoBot;
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

    // Game state
    private int gameState;
    private int titleState = 0;
    private int playState = 1;
    private int scoreState = 2;

    private JFrame menuPrincipal, TilesEditor;
    private JPanel menu;

    public VueControleurGyromite(Jeu _jeu) {
        sizeX = jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;

        chargerLesIcones();
        placerLesComposantsGraphiques();
        ajouterEcouteurClavier();
    }

    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT : Controle4Directions.getInstance().setDirectionCourante(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : Controle4Directions.getInstance().setDirectionCourante(Direction.droite); break;
                    case KeyEvent.VK_DOWN : Controle4Directions.getInstance().setDirectionCourante(Direction.bas); break;
                    case KeyEvent.VK_UP : Controle4Directions.getInstance().setDirectionCourante(Direction.haut); break;
                    case KeyEvent.VK_Z : ColonneDepl.getInstance().setDirectionCourante(); break;
                    case KeyEvent.VK_A : ColonneDeplB.getInstance().setDirectionCourante(); break;

                }
            }
        });
    }


    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/player.png", 160, 0, 32, 38);
        icoHeroGauche = chargerIcone("Images/player.png", 0, 0, 32, 38);
        icoHeroDroite = chargerIcone("Images/player-2.png", 160, 0, 32, 38);
        icoHeroGrimpe = chargerIcone("Images/player.png", 0, 88, 32, 38);
        icoHeroHaut = chargerIcone("Images/player.png", 160, 45, 32, 38);



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
        menuPrincipal = new JFrame();
        menuPrincipal.setTitle("Gyromite-Menu");
        menuPrincipal.setSize(800, 600);
        menuPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panelMenuPrincipal = new JPanel();
        panelMenuPrincipal.setLayout(new GridLayout(4, 1));
        menuPrincipal.setResizable(false); //Rend inajustable la taille de la fenêtre
        menuPrincipal.setLocationRelativeTo(null);// Centre le menu principal

        // Ajout du titre
        JLabel titre = new JLabel("Gyromite", SwingConstants.CENTER);
        // tire plus jolie
        titre.setFont(new Font("Arial", Font.BOLD, 50));
        titre.setForeground(Color.BLACK);
        titre.setOpaque(true);

        panelMenuPrincipal.add(titre);


        // Ajout du bouton Jouer
        JButton boutonJouer = new JButton("Jouer");
        boutonJouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuPrincipal.setVisible(false);
                // detruction du menu principal
                menuPrincipal.dispose();
                setVisible(true);
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
        // Ajout du bouton TilesEditor
        JButton boutonTilesEditor = new JButton("TilesEditor");
        boutonTilesEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuPrincipal.setVisible(false);
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

        // Ajout du bouton quitter
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
        TilesEditor = new JFrame("TilesEditor");
        TilesEditor.setSize(1280, 720);
        TilesEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TilesEditor.setLocationRelativeTo(null); // Centre la fenêtre
        TilesEditor.setResizable(false); //Rend inajustable la taille de la fenêtre

        // On place les composants graphiques
        JComponent grilleJLabelsTilesEditor = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille
        tabJLabelTilesEditor = new CaseTileEditor[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                CaseTileEditor jlab = new CaseTileEditor(x,y);
                tabJLabelTilesEditor[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                jlab.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                grilleJLabelsTilesEditor.add(jlab);

            }
        }

        TilesEditor.add(grilleJLabelsTilesEditor, BorderLayout.CENTER);

        // Ajout d'un menu a droite de la fenetre pour choisir les tiles
        JPanel panelTilesEditor = new JPanel();
        panelTilesEditor.setLayout(new GridLayout(10, 1));
        // Ajout du bouton quitter
        JButton boutonQuitterTilesEditor = new JButton("Quitter");
        boutonQuitterTilesEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TilesEditor.setVisible(false);

                menuPrincipal.setVisible(true);
            }
        });
        panelTilesEditor.add(boutonQuitterTilesEditor);

        TilesEditor.add(panelTilesEditor, BorderLayout.EAST);

    }


    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        ((JLabel)menu.getComponent(0)).setText("Score : " + jeu.score); // on met à jour la label score
        ((JLabel)menu.getComponent(1)).setText("Vie : " + jeu.nb_vie); // on met à jour la label vies
        ((JLabel)menu.getComponent(2)).setText("Bombes : " + jeu.bombe_restante); // on met à jour la label bombes
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (jeu.getGrille()[x][y][1] instanceof Heros) { // si la grille du modèle contient un Pacman, on associe l'icône Pacman du côté de la vue

                    Direction dir = Controle4Directions.getDirectionPrecedente(); // on récupère la direction précédente du heros

                    if(jeu.getGrille()[x][y][0] instanceof Liane)
                    {
                        tabJLabel[x][y].setIcon(icoHeroGrimpe);
                    }
                    else if (dir == Direction.gauche) {
                        tabJLabel[x][y].setIcon(icoHeroGauche);
                    }
                    else if (dir == Direction.haut)
                    {
                        tabJLabel[x][y].setIcon(icoHeroHaut);
                    }
                    else if (dir == Direction.droite) {
                        tabJLabel[x][y].setIcon(icoHeroDroite);
                    }
                    else {
                        tabJLabel[x][y].setIcon(icoHero);
                    }

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
            }
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
