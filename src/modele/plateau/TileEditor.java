/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import java.io.*;
import java.io.File;

public class TileEditor {
    public static final int SIZE_X = 28; // taille du plateau
    public static final int SIZE_Y = 18; // taille du plateau

    private Jeu jeu;

    private Entite[][] grilleETile = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    private Entite entiteCourante = null; // entité courante ( sélectionnée dans le menu de gauche )

    private boolean CompteurHero = false; // permet de savoir si un héros a déjà été placé

    public void setEntiteCourante(Entite entite) {
        entiteCourante = entite;
    }

    public TileEditor() {
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                grilleETile[i][j] = null;
                FirstPattern();
            }
        }
    }

    public Entite[][] getGrilleETile() {
        return grilleETile;
    }

    // Permet d'allouer une entité à une case de la grille
    public void setEntiteGrilleETile(int x, int y) {
        // Si on a au moins 1 Heros sur la grille, on ne peut pas en ajouter un autre, sinon on peut en ajouter un
        if (entiteCourante instanceof Heros) {
                if(!CompteurHero) {
                    grilleETile[x][y] = entiteCourante;
                    CompteurHero = true;
                } else
                {
                    System.out.println("Vous avez déjà un Heros sur la grille");
                }
        } else {
            grilleETile[x][y] = entiteCourante;
        }
    }

    // Efface l'entité qui est sur la case
    public void EraseEntiteGrilleETile(int x, int y) {
        // Si l'entité est un Heros, on supprime le compteur
        if(grilleETile[x][y] instanceof Heros)
        {
            CompteurHero = false;
        }
        this.grilleETile[x][y] = null;
    }

    // Dessine le partern de base, soit les mur sur les cotés, le plafond et le sol
    public void FirstPattern()
    {
        for(int i = 0; i < SIZE_X; i++)
        {
            for(int j = 0; j < SIZE_Y-2; j++)
            {
                grilleETile[i][0] = new Mur(jeu);
                grilleETile[i][SIZE_Y-1] = new MurBrique(jeu);

                grilleETile[0][j+1] = new MurVertical(jeu);
                grilleETile[SIZE_X-1][j+1] = new MurVertical(jeu);
            }

        }
    }

    public void Reset() {
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                grilleETile[i][j] = null; // on met toutes les cases à null
                CompteurHero = false; // on remet le compteur de Heros à  0
                FirstPattern(); // on remet le pattern de base
            }
        }
    }

    // On utilise des instanceof pour savoir quel est le type d'entité sur la grille
    // On n'a pas trouver d'autres solutions pour faire ca
    public void SaveInFile() {
        try {
            //Creer un fichier
            File file = new File("CreatedLevel.txt");

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int j = 0; j < SIZE_Y; j++) {
                for (int i = 0; i < SIZE_X; i++) {
                    if (grilleETile[i][j] == null) {
                        bw.write("0");
                    } else if (grilleETile[i][j] instanceof Mur) {
                        bw.write("2");
                    } else if (grilleETile[i][j] instanceof MurVertical) {
                        bw.write("1");
                    } else if (grilleETile[i][j] instanceof MurBrique) {
                        bw.write("3");
                    } else if (grilleETile[i][j] instanceof SupportColonne) {
                        bw.write("G");
                    } else if (grilleETile[i][j] instanceof Bombe) {
                        bw.write("B");
                    } else if (grilleETile[i][j] instanceof Bot) {
                        bw.write("M");
                    } else if (grilleETile[i][j] instanceof Liane) {
                        bw.write("L");
                    } else if (grilleETile[i][j] instanceof Colonne) {
                        bw.write("X");
                    } else if (grilleETile[i][j] instanceof ColonneR) {
                        bw.write("Y");
                    } else if (grilleETile[i][j] instanceof Heros) {
                        bw.write("H");
                    }
                }
                bw.write("\n");
            }
            bw.close();
            System.out.println("Fichier créé avec succès");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}