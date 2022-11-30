/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import VueControleur.VueControleurGyromite;
import modele.plateau.Jeu;

import java.io.*;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TileEditor {
    public static final int SIZE_X = 28; // taille du plateau
    public static final int SIZE_Y = 18; // taille du plateau

    private Jeu jeu;

    private final Entite[][] grilleETile = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    private HashMap<Entite, Point> map = new  HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à partir de sa référence

    private Entite entiteCourante = null;

    // l'entité courante devient un MurVertical
    public void setMurVertical() {
        entiteCourante = new MurVertical(jeu);
    }

    public Entite getEntiteCourante() {
        return entiteCourante;
    }

    public TileEditor(Jeu _jeu) {
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                grilleETile[i][j] = null;
            }
        }
        jeu = _jeu;

    }

}