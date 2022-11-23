package VueControleur;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import modele.deplacements.Controle4Directions;
import modele.deplacements.Direction;
import modele.deplacements.ColonneDepl;
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

    private ImageIcon icoBot;
    private ImageIcon icoVide;
    private ImageIcon icoMurHorizontal;

    private ImageIcon icoMurVertical;

    private ImageIcon icoMurBrique;

    private ImageIcon icoBombe;

    private ImageIcon icoLiane;

    private ImageIcon icoSupportColonne;
    private ImageIcon icoColonne;

    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)


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
                    case KeyEvent.VK_Z : ColonneDepl.getInstance().setDirectionCourante(Direction.haut); break;
                    case KeyEvent.VK_S : ColonneDepl.getInstance().setDirectionCourante(Direction.bas); break;
                }
            }
        });
    }


    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/player.png", 160, 0, 32, 44);
        icoHeroGauche = chargerIcone("Images/player.png", 0, 0, 32, 44);
        icoHeroDroite = chargerIcone("Images/player-2.png", 160, 0, 32, 44);
        icoHeroGrimpe = chargerIcone("Images/player.png", 0, 88, 32, 44);



        icoBot = chargerIcone("Images/smick.png", 0, 0, 20, 20);//chargerIcone("Images/Pacman.png");

        icoVide = chargerIcone("Images/tileset.png", 64, 0, 16, 14);

        icoColonne = chargerIcone("Images/Colonne.png", 0, 0, 16, 16);

        icoMurHorizontal = chargerIcone("Images/tileset.png", 0, 1, 16 ,14);

        icoMurVertical = chargerIcone("Images/tileset.png", 2, 16, 12, 16);

        icoMurBrique = chargerIcone("Images/tileset.png", 33, 1, 15, 15);

        icoBombe = chargerIcone("Images/bomb.png", 20, 16, 26, 32);

        icoLiane = chargerIcone("Images/tileset.png", 17, 0, 14, 16);

        icoSupportColonne = chargerIcone("Images/tileset.png", 17, 18, 16, 14);

    }

    private void placerLesComposantsGraphiques() {
        setTitle("Gyromite");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }


    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (jeu.getGrille()[x][y] instanceof Heros) { // si la grille du modèle contient un Pacman, on associe l'icône Pacman du côté de la vue

                    Direction dir = Controle4Directions.getDirectionPrecedente(); // on récupère la direction précédente du heros

                    if(jeu.HeroSurCorde)
                    {
                        tabJLabel[x][y].setIcon(icoHeroGrimpe);
                    }
                    else
                    {
                        // on associe l'icône correspondant à la direction précédente
                        if (dir == Direction.gauche) {
                            tabJLabel[x][y].setIcon(icoHeroGauche);
                        }
                        else if (dir == Direction.droite) {
                            tabJLabel[x][y].setIcon(icoHeroDroite);
                        }
                        else {
                            tabJLabel[x][y].setIcon(icoHero);
                        }
                    }


                    // si transparence : images avec canal alpha + dessins manuels (voir ci-dessous + créer composant qui redéfinie paint(Graphics g)), se documenter
                    //BufferedImage bi = getImage("Images/smick.png", 0, 0, 20, 20);
                    //tabJLabel[x][y].getGraphics().drawImage(bi, 0, 0, null);

                } else if (jeu.getGrille()[x][y] instanceof Bot) {
                    tabJLabel[x][y].setIcon(icoBot);

                } else if (jeu.getGrille()[x][y] instanceof Mur) {
                    tabJLabel[x][y].setIcon(icoMurHorizontal);
                } else if (jeu.getGrille()[x][y] instanceof MurVertical) {
                    tabJLabel[x][y].setIcon(icoMurVertical);
                } else if (jeu.getGrille()[x][y] instanceof MurBrique) {
                    tabJLabel[x][y].setIcon(icoMurBrique);
                } else if (jeu.getGrille()[x][y] instanceof Colonne) {
                    tabJLabel[x][y].setIcon(icoColonne);
                } else if (jeu.getGrille()[x][y] instanceof SupportColonne) {
                    tabJLabel[x][y].setIcon(icoSupportColonne);
                } else if (jeu.getGrille()[x][y] instanceof Liane) {
                    tabJLabel[x][y].setIcon(icoLiane);
                } else if (jeu.getGrille()[x][y] instanceof Bombe) {
                    tabJLabel[x][y].setIcon(icoBombe);
                } else if (jeu.getGrille()[x][y] instanceof Colonne) {
                    tabJLabel[x][y].setIcon(icoColonne);
                } else {
                    tabJLabel[x][y].setIcon(icoVide);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(jeu.GameIsFinished())
        {
            JOptionPane.showMessageDialog(this, "Game Over");
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
