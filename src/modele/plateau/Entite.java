/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;

public abstract class Entite {
    protected Jeu jeu;
    
    public Entite(Jeu _jeu) {
        jeu = _jeu;
    }
    
    public abstract boolean peutEtreEcrase(); // l'entité peut être écrasée (par exemple par une colonne ...)
    public abstract boolean peutServirDeSupport(); // permet de stopper la gravité, prendre appui pour sauter
    public abstract boolean peutPermettreDeMonterDescendre(); // si utilisation de corde (attention, l'environnement ne peut pour l'instant sotker qu'une entité par case (si corde : 2 nécessaires), améliorations à prévoir)

    public abstract boolean peutEtreRamasse(); // l'entité peut être ramassée (par exemple par un personnage)

    public abstract boolean estEnnemi(); // l'entité est un ennemi (par exemple un fantôme)
    public abstract boolean peutEtreTraversee();

    public abstract boolean estPilier();

    public Object regarderDansLaDirection(Direction d) {
        return jeu.regarderDansLaDirection(this, d);
    }
}
