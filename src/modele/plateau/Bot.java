/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;

import java.util.Random;

/**
 * Ennemis (Smicks)
 */
public class Bot extends EntiteDynamique {
    private Direction directionCourante = Direction.gauche;
    private Direction ancienneDirection = Direction.gauche;
    public Bot(Jeu _jeu) {
        super(_jeu);
    }

    public boolean peutEtreEcrase() { return true; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false; };

    public boolean peutEtreRamasse() { return false; }

    public boolean estEnnemi() { return true; }

    @Override
    public boolean peutEtreTraversee() {
        return false;
    }

    public Direction getDirectionCourante() {
        return directionCourante;
    }

    public Direction getAncienneDirection() {
        return ancienneDirection;
    }

    public void setDirectionCourante(Direction directionCourante) {
        this.directionCourante = directionCourante;
    }

    public void setAncienneDirection(Direction directionCourante) {
        this.ancienneDirection = directionCourante;
    }
}
