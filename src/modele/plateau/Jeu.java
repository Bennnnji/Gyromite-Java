/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import VueControleur.VueControleurGyromite;
import modele.deplacements.*;

import javax.lang.model.UnknownEntityException;
import java.awt.*;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.HashMap;
import java.util.Scanner;

/** Actuellement, cette classe gère les postions
 * (ajouter conditions de victoire, chargement du plateau, etc.)
 */
public class Jeu {

    public static final int SIZE_X = 28; // taille du plateau
    public static final int SIZE_Y = 18; // taille du plateau

    public int bombe_restante = 0;
    public int nb_vie = 4;
    public int score = 0;

    private Gravite g;

    // compteur de déplacements horizontal et vertical (1 max par défaut, à chaque pas de temps)
    private HashMap<Entite, Integer> cmptDeplH = new HashMap<Entite, Integer>();
    private HashMap<Entite, Integer> cmptDeplV = new HashMap<Entite, Integer>();

    private Heros hector;

    private HashMap<Entite, Point> map = new  HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à partir de sa référence
    private Entite[][][] grilleEntites = new Entite[SIZE_X][SIZE_Y][2]; // permet de récupérer une entité à partir de ses coordonnées

    private Ordonnanceur ordonnanceur = new Ordonnanceur(this);

    // Tableau de sting qui contient le plateau de jeu
    private String[] prebuildmap = new String[SIZE_Y * SIZE_X];

    private int nvCourant = 1;


    public Jeu(){
        LoadLevel(0);
    }

    public void resetCmptDepl() {
        cmptDeplH.clear();
        cmptDeplV.clear();
    }

    public void start(long _pause) {
        ordonnanceur.start(_pause);
    }

    public Entite[][][] getGrille() {
        return grilleEntites;
    }

    public void LoadLevel(int ChoixMap)
    {
        // on vide le tableau de string
        prebuildmap = new String[SIZE_Y * SIZE_X];
        // on charge le nouveau niveau
        if(ChoixMap == 1)
        {
            getStaticMap("CreatedLevel.txt");
            nb_vie = 10;
        }
        else
        {
            getStaticMap("MapLVL/mapLVL-" + nvCourant + ".txt");
        }

        // on initialise les entites sur le plateau
        initialisationDesEntites();

    }

    public int getBombe_restante() {
        return bombe_restante;
    }

    public int getScore() {
        return score;
    }

    public int getNbVie() {
        return nb_vie;
    }
    public void setNvCourant(int nvCourant) {
        this.nvCourant = nvCourant;
    }

    private Point StartPoint;
    public void setStartPoint(int x, int y)
    {
        StartPoint = new Point(x, y);
    }
    public void RestartHeroPos(Point pCourant, Entite e ){
        deplacerEntite(pCourant, StartPoint, e);
    }
    private void initialisationDesEntites() {
        g = new Gravite();
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (prebuildmap[i].charAt(j) == '1') {
                    addEntite(new MurVertical(this), j, i, 0);
                } else if (prebuildmap[i].charAt(j) == '2') {
                    addEntite(new Mur(this), j, i, 0);
                } else if (prebuildmap[i].charAt(j) == '3') {
                    addEntite(new MurBrique(this), j, i, 0);
                } else if (prebuildmap[i].charAt(j) == 'G') {
                    addEntite(new SupportColonne(this), j, i, 0);
                } else if (prebuildmap[i].charAt(j) == 'B') {
                    addEntite(new Bombe(this), j, i, 0);
                    bombe_restante++;
                }
                else if (prebuildmap[i].charAt(j) == 'M') {
                    Bot b = new Bot(this);
                    IA newIa = new IA();
                    addEntite(b, j, i, 1);
                    g.addEntiteDynamique(b);
                    newIa.getInstance().addEntiteDynamique(b);
                    ordonnanceur.add(newIa.getInstance());

                }
                else if (prebuildmap[i].charAt(j) == 'L') {
                    addEntite(new Liane(this), j, i, 0);
                }
                else if (prebuildmap[i].charAt(j) == 'X') {
                    Pilier pil = new Pilier(this);
                    for (int k = 0; k < 3; k++) {
                        Colonne elemCol = new Colonne(this);
                        addEntite(elemCol, j, i - k, 1);
                        pil.addColonne(elemCol);

                    }
                    ColonneDepl.getInstance().addEntiteDynamique(pil);
                    ordonnanceur.add(ColonneDepl.getInstance());

                }
                else if(prebuildmap[i].charAt(j) == 'Y')
                {
                    Pilier pil = new Pilier(this);
                    for(int k = 0; k < 3 ; k++)
                    {
                        ColonneR elemCol = new ColonneR(this);
                        addEntite(elemCol,j,i-k, 1);
                        pil.addColonne(elemCol);
                    }
                    ColonneDeplB.getInstance().addEntiteDynamique(pil);
                    ordonnanceur.add(ColonneDeplB.getInstance());
                }
                else if(prebuildmap[i].charAt(j) == 'H')
                {
                    hector = new Heros(this);
                    addEntite(hector, j, i, 1);
                    setStartPoint(j, i);
                    g.addEntiteDynamique(hector);
                    Controle4Directions.getInstance().addEntiteDynamique(hector);
                    ordonnanceur.add(Controle4Directions.getInstance());
                }


            }

        }
        ordonnanceur.add(g);
        System.out.println("il y'a " + bombe_restante + " bombe(s) sur le plateau");
    }

    private void addEntite(Entite e, int x, int y, int z) {
        grilleEntites[x][y][z] = e;
        map.put(e, new Point(x, y));
    }

    public void supprimerEntite(Entite e, int x, int y, int z) {
        grilleEntites[x][y][z] = null;
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
        boolean eEstHero = e.peutEtreEcrase() && e.peutServirDeSupport() && !e.estEnnemi(); // permet de savoir si l'entité est un héros
        boolean pCibleEstHero = false; // permet de savoir si la position cible est un héros
        boolean deplacementAutorise = false;

        Point pCourant = map.get(e); // position courante de l'entité
        Point pCible = calculerPointCible(pCourant, d); // calcul de la position cible
        if(objetALaPosition(pCible) != null)
        {
            pCibleEstHero = objetALaPosition(pCible).peutEtreEcrase() && !objetALaPosition(pCible).estEnnemi(); // permet de savoir si la position cible est un héros
        }

        if(contenuDansGrille(pCible)) {

            if(objetALaPosition(pCible) != null) {
                // si la case cible contient une bombe
                if (objetALaPosition(pCible).peutEtreRamasse() && e.peutEtreEcrase()) {
                    if (eEstHero) {
                        System.out.println("Bombe ramassée");
                        bombe_restante--;
                        deplacementAutorise = true;
                        supprimerEntite(objetALaPosition(pCible), pCible.x, pCible.y, 0);
                        System.out.println("Il reste " + bombe_restante + " bombes");
                        score += 100;
                        System.out.println("Score : " + score);
                    }
                    else if(e.estEnnemi())
                    {
                        deplacementAutorise = true;
                    }
                }
                // si la case cible contient un smick
                else if (objetALaPosition(pCible).estEnnemi() && eEstHero) {
                    // si le prof arrive au dessus du smick
                    if (d == Direction.bas) {
                        System.out.println("Smicks Ecrasé");
                        score += 50;
                        deplacementAutorise = true;
                        System.out.println("Score : " + score);
                        g.supprimerEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                        IA.getInstance().removeEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                        supprimerEntite(objetALaPosition(pCible), pCible.x, pCible.y, 1);
                    } else if(d == Direction.droite || d == Direction.gauche) {
                        System.out.println("Collision avec un Smicks");
                        nb_vie--;
                        g.supprimerEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                        IA.getInstance().removeEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                        supprimerEntite(objetALaPosition(pCible), pCible.x, pCible.y, 1);
                        System.out.println("Il vous reste " + nb_vie + " vies");
                        RestartHeroPos(pCourant, e);
                    }
                }
                else if(objetALaPosition(pCible).peutPermettreDeMonterDescendre() && eEstHero) {
                    deplacementAutorise = true;
                }
                else if(objetALaPosition(pCible).peutPermettreDeMonterDescendre() && e.estEnnemi())
                {
                    deplacementAutorise = true;
                }
                else if(objetALaPosition(pCible).peutEtreEcrase() && e.estPilier())
                {
                    Entite eBas = (Entite) objetALaPosition(pCible).regarderDansLaDirection(Direction.bas); // entité sous la cible
                    Entite eHaut = (Entite) objetALaPosition(pCible).regarderDansLaDirection(Direction.haut); // entité au dessus de la cible
                    Entite pilBas = (Entite) e.regarderDansLaDirection(Direction.bas); // entité sous le pilier (car ici e est un pilier).
                    if(pCibleEstHero && (!pilBas.estEnnemi() && !pilBas.peutEtreEcrase()))
                    {
                        hector.avancerDirectionChoisie(Direction.haut); // on fait bouger le personnage vers le haut.
                    }

                    if(eBas != null && eBas.peutServirDeSupport() && !eBas.estPilier() && !eBas.peutPermettreDeMonterDescendre()
                            || eHaut != null && eHaut.peutServirDeSupport() && !eHaut.estPilier()  && !eHaut.peutPermettreDeMonterDescendre())
                    {
                        if(pCibleEstHero)
                        {
                            System.out.println("Vous avez été ecrasé");
                            RestartHeroPos(pCible, objetALaPosition(pCible));
                            System.out.println("Il vous reste " + nb_vie + " vies");
                            deplacementAutorise = true;
                            nb_vie--;

                        }
                        else if(objetALaPosition(pCible).estEnnemi())
                        {
                            System.out.println("Smicks Ecrasé");
                            Point pEnnemi = map.get(objetALaPosition(pCible));
                            IA.getInstance().removeEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                            g.supprimerEntiteDynamique((EntiteDynamique) objetALaPosition(pCible));
                            supprimerEntite(objetALaPosition(pCible), pEnnemi.x, pEnnemi.y, 1);
                            deplacementAutorise = true;
                        }
                    }
                }
            }
            else {
                deplacementAutorise = true;
            }
            if (deplacementAutorise) { // a adapter (collisions murs, etc.)
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
        grilleEntites[pCourant.x][pCourant.y][1] = null;
        grilleEntites[pCible.x][pCible.y][1] = e;
        map.put(e, pCible);
    }

    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    private Entite objetALaPosition(Point p) {
        Entite retour = null;

        // si la position est dans la grille
        if (contenuDansGrille(p)) {
            // si en z=1 il y a une entité
            if (grilleEntites[p.x][p.y][1] != null) {
                retour = grilleEntites[p.x][p.y][1];
            }
            // si en z=0 il y a une entité
            else if (grilleEntites[p.x][p.y][0] != null) {
                retour = grilleEntites[p.x][p.y][0];
            }

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
            BufferedWriter writer = new BufferedWriter(new FileWriter("HighScore.txt", false));
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
            BufferedReader reader = new BufferedReader(new FileReader("HighScore.txt"));
            String line = reader.readLine();
            //si il n'ya rien dans le fichier
            if(line == null){
                retour = true;
            }
            else
            {
                int highscore = Integer.parseInt(line);
                if(score > highscore){
                    return NewHighScore();
                }}
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
        // on reset tout pour pas qu'il y ai de bug -> doublons d'entité etc.
        nvCourant++;
        ordonnanceur.clear();
        resetCmptDepl();
        Controle4Directions.reset();
        g.clearEntiteDynamique();
        IA.reset();
        ColonneDepl.reset();
        ColonneDeplB.reset();
        // vide la grille d'entité
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                grilleEntites[i][j][0] = null;
                grilleEntites[i][j][1] = null;
            }
        }
        map.clear();
        LoadLevel(0);

    }

    public void ResetAll()
    {
        bombe_restante = 0;
        score = 0;
        nb_vie = 4;
        ordonnanceur.clear();
        resetCmptDepl();
        Controle4Directions.reset();
        g.clearEntiteDynamique();
        IA.reset();
        ColonneDepl.reset();
        ColonneDeplB.reset();
        // vide la grille d'entité
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                grilleEntites[i][j][0] = null;
                grilleEntites[i][j][1] = null;
            }
        }
        map.clear();
    }
    public int GameIsFinished(){

        if(bombe_restante <= 0 && nvCourant < 3){
            LevelFinished();
            return 0;
        }
        else if(bombe_restante <= 0 && nvCourant == 3){
            System.out.println("Game finished");
            if(HighScore()){
                System.out.println("New HighScore");
            }
            return 2;
        }
        else if(nb_vie <= 0) {
            System.out.println("Vous avez perdu !");
            System.out.println("Votre score est de : " + score);
            if(HighScore()){
                System.out.println("Nouveau HighScore ! : " + score);
            }

            return 1;
        }


        return 0;
    }
}