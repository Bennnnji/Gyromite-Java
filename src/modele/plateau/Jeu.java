/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import VueControleur.VueControleurGyromite;
import modele.deplacements.*;

import java.io.*;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/** Actuellement, cette classe gère les postions
 * (ajouter conditions de victoire, chargement du plateau, etc.)
 */
public class Jeu {

    public static final int SIZE_X = 28; // taille du plateau
    public static final int SIZE_Y = 18; // taille du plateau

    public int bombe_restante = 0;
    public int nb_vie = 3;
    public int score = 0;

    // compteur de déplacements horizontal et vertical (1 max par défaut, à chaque pas de temps)
    private HashMap<Entite, Integer> cmptDeplH = new HashMap<Entite, Integer>();
    private HashMap<Entite, Integer> cmptDeplV = new HashMap<Entite, Integer>();

    private Heros hector;

    private HashMap<Entite, Point> map = new  HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à partir de sa référence
    private Entite[][] grilleEntites = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    private Ordonnanceur ordonnanceur = new Ordonnanceur(this);

    // Tableau de sting qui contient le plateau de jeu
    private String[] prebuildmap = new String[SIZE_Y * SIZE_X];

    private int nvCourant = 1;


    public Jeu() throws IOException{
        LoadLevel();
    }

    public void resetCmptDepl() {
        cmptDeplH.clear();
        cmptDeplV.clear();
    }

    public void start(long _pause) {
        ordonnanceur.start(_pause);
    }

    public Entite[][] getGrille() {
        return grilleEntites;
    }
    public boolean HeroSurCorde = false;

    public boolean BotSurCorde = false;

    public void LoadLevel()
    {
        // on vide le tableau de string
        prebuildmap = new String[SIZE_Y * SIZE_X];
        // on charge le nouveau niveau
        getStaticMap("mapLVL-" + nvCourant + ".txt");
        initialisationDesEntites();

    }

    private void initialisationDesEntites() {
        hector = new Heros(this);
        switch (nvCourant) {
            case 1 -> addEntite(hector, 1, 1);
            case 2 -> addEntite(hector, 7, 3);
            case 3 -> addEntite(hector, 2, 2);
        }

        Gravite g = new Gravite();
        g.addEntiteDynamique(hector);
        ordonnanceur.add(g);

        Controle4Directions.getInstance().addEntiteDynamique(hector);
        ordonnanceur.add(Controle4Directions.getInstance());


        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (prebuildmap[i].charAt(j) == '1') {
                    addEntite(new MurVertical(this), j, i);
                }
                else if (prebuildmap[i].charAt(j) == '2') {
                    addEntite(new Mur(this), j, i);
                }
                else if (prebuildmap[i].charAt(j) == '3') {
                    addEntite(new MurBrique(this), j, i);
                }
                else if (prebuildmap[i].charAt(j) == 'G') {
                    addEntite(new SupportColonne(this), j, i);
                }
                else if (prebuildmap[i].charAt(j) == 'B') {
                    addEntite(new Bombe(this), j, i);
                    bombe_restante++;
                }
                else if (prebuildmap[i].charAt(j) == 'M') {
                    Bot b = new Bot(this);
                    addEntite(b, j, i);
                    IA.getInstance().addEntiteDynamique(b);
                    ordonnanceur.add(IA.getInstance());

                }
                else if (prebuildmap[i].charAt(j) == 'L'){
                    addEntite(new Liane(this), j, i);
                }
                else if (prebuildmap[i].charAt(j) == 'X') {
                    Pilier pil = new Pilier(this);
                    for(int k = 0; k < 3; k++)
                    {
                        Colonne elemCol = new Colonne(this);
                        addEntite(elemCol, j,i-k);
                        pil.addColonne(elemCol);

                    }
                    ColonneDepl.getInstance().addEntiteDynamique(pil);
                    ordonnanceur.add(ColonneDepl.getInstance());

                }

            }

        }
        System.out.println("il y'a " + bombe_restante + " bombe(s) sur le plateau");
    }

    private void addEntite(Entite e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }

    public void supprimerEntite(Entite e) {
        Point p = map.get(e);
        grilleEntites[p.x][p.y] = null;
        map.remove(e);
    }

    /** Permet par exemple a une entité  de percevoir sont environnement proche et de définir sa stratégie de déplacement
     * <p>
     *
     */
    public Entite regarderDansLaDirection(Entite e, Direction d) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCible(positionEntite, d));
    }

    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    public boolean deplacerEntite(Entite e, Direction d) {
        boolean retour = false;

        Point pCourant = map.get(e); // position courante de l'entité

        Point pCible = calculerPointCible(pCourant, d); // calcul de la position cible

        // si la case cible contient une bombe
        if (objetALaPosition(pCible) instanceof Bombe && e instanceof Heros) {
            System.out.println("Bombe ramassée");
            bombe_restante--;
            supprimerEntite(objetALaPosition(pCible));
            System.out.println("Il reste " + bombe_restante + " bombes");
            score += 100;
            System.out.println("Score : " + score);
        }
        // si la case cible contient un smick
        if(objetALaPosition(pCible) instanceof Bot && e instanceof Heros) {
            // si le prof arrive au dessus du smick
            if (d == Direction.bas) {
                System.out.println("Smicks Ecrasé");
                score += 50;
                System.out.println("Score : " + score);
                IA.getInstance().removeEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                supprimerEntite(objetALaPosition(pCible));
            } else {
                System.out.println("Collision avec un Smicks");
                nb_vie--;
                System.out.println("Il vous reste " + nb_vie + " vies");
            }
        }
        if(objetALaPosition(pCible) instanceof Heros && e instanceof Bot)
        {
            System.out.println("Collision avec un Heros");
            nb_vie--;
            System.out.println("Il vous reste " + nb_vie + " vies");
        }
        if (contenuDansGrille(pCible) && objetALaPosition(pCible) == null
                || objetALaPosition(pCible).peutEtreRamasse() || objetALaPosition(pCible).peutPermettreDeMonterDescendre()) { // a adapter (collisions murs, etc.)
            // compter le déplacement : 1 deplacement horizontal et vertical max par pas de temps par entité
            switch (d) {
                case bas, haut -> {
                    if (cmptDeplV.get(e) == null) {
                        cmptDeplV.put(e, 1);

                        retour = true;
                    }
                }
                case gauche, droite -> {

                    if (cmptDeplH.get(e) == null) {
                        cmptDeplH.put(e, 1);
                        retour = true;

                    }
                }
            }
        }

        if (retour) {
            deplacerEntite(pCourant, pCible, e);
        }

        return retour;
    }


    private Point calculerPointCible(Point pCourant, Direction d) {
        return switch (d) {
            case haut -> new Point(pCourant.x, pCourant.y - 1);
            case bas -> new Point(pCourant.x, pCourant.y + 1);
            case gauche -> new Point(pCourant.x - 1, pCourant.y);
            case droite -> new Point(pCourant.x + 1, pCourant.y);
        };
    }

    // Permet de déplacer une entité d'une case à une autre
    private void deplacerEntite(Point pCourant, Point pCible, Entite e) {
        grilleEntites[pCourant.x][pCourant.y] = null;

        // si la case cible contient bien une entité
        if(contenuDansGrille(pCible))
        {
            // si l'entité est un héros, on vérifie si il y a une Liane à la position cible
            if (objetALaPosition(pCible) instanceof Liane && e instanceof Heros) {
                grilleEntites[pCible.x][pCible.y] = e; // on déplace l'entité sur la Liane
                map.put(e, pCible); // on met à jour la position de l'entité dans la map

                // on vérifie si l'entité est sur une Liane
                if(HeroSurCorde){
                    grilleEntites[pCourant.x][pCourant.y] = new Liane(this); // on remet une Liane à la position précédente
                }
                HeroSurCorde = true;
            }
            // si l'entité est un Bot, on vérifie si il y a une Liane à la position cible
            else if ((objetALaPosition(pCible) instanceof Liane && e instanceof Bot)){
                grilleEntites[pCible.x][pCible.y] = e; // on déplace l'entité sur la Liane
                map.put(e, pCible); // on met à jour la position de l'entité dans la map
                if(BotSurCorde){
                    grilleEntites[pCourant.x][pCourant.y] = new Liane(this); // on remet une Liane à la position précédente
                }
                BotSurCorde = true;
            }
            // si l'entite est le bot, si il y a le hero sur la position cible
            else if (objetALaPosition(pCible) instanceof Heros && e instanceof Bot) {
                grilleEntites[pCible.x][pCible.y] = e; // on déplace l'entité sur la Liane
                map.put(e, pCible); // on met à jour la position de l'entité dans la map
                map.put(objetALaPosition(pCible), pCourant); // on met à jour la position de l'entité dans la map
            }
            else {
                grilleEntites[pCourant.x][pCourant.y] = null;
                grilleEntites[pCible.x][pCible.y] = e;
                map.put(e, pCible);

                // on vérifie si l'entité est sur une Liane
                if(HeroSurCorde && e instanceof Heros){
                    grilleEntites[pCourant.x][pCourant.y] = new Liane(this); // on remet une Liane à la position précédente
                    HeroSurCorde = false;
                }
                if(BotSurCorde && e instanceof Bot){
                    grilleEntites[pCourant.x][pCourant.y] = new Liane(this); // on remet une Liane à la position précédente
                    BotSurCorde = false;
                }
            }

        }


    }

    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    private Entite objetALaPosition(Point p) {
        Entite retour = null;

        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }

        return retour;
    }

    public Ordonnanceur getOrdonnanceur() {
        return ordonnanceur;
    }

    private void getStaticMap(String FileName) {
        {
            File myObj = new File(FileName);

            try {
                Scanner myReader = new Scanner(myObj);
                int i = 0;
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    prebuildmap[i] = data;
                    i++;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean NewHighScore(){
        boolean retour = false;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("HighScore.txt", true));
            writer.write(score + "\n");
            writer.close();

            retour = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return retour;
    }

    public boolean HighScore(){
        boolean retour = false;
        try{
            BufferedReader reader = new BufferedReader(new FileReader("score.txt"));
            String line = reader.readLine();
            int highscore = Integer.parseInt(line);
            if(score > highscore){
                return NewHighScore();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return retour;
    }

    public void LevelFinished(){
        if(nvCourant == 1)
        {
            System.out.println("Level " + nvCourant + "finished");
        }
        nvCourant++;
        ordonnanceur.clear();
        resetCmptDepl();
        Controle4Directions.reset();
        IA.reset();
        ColonneDepl.reset();
        // vide la grille d'entité
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                grilleEntites[i][j] = null;
            }
        }
        map.clear();

        if(nvCourant > 3)
        {
            GameIsFinished();
        }
        LoadLevel();
    }

    public void ResetGame(){
        nvCourant = 1;
        score = 0;
    }

    public boolean GameIsFinished(){

        if(bombe_restante <= 0 && nvCourant <= 3){
            LevelFinished();
            return false;
        }
        else if(bombe_restante <= 0 && nvCourant > 3){
            System.out.println("Game finished");
            return true;
        }
        else if(nb_vie <= 0) {
            System.out.println("Vous avez perdu !");
            System.out.println("Votre score est de : " + score);
            if(HighScore()){
                System.out.println("Nouveau HighScore ! : " + score);
            }

            return true;
        }


        return false;
    }
}
