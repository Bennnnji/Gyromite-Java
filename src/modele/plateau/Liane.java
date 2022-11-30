package modele.plateau;

public class Liane extends EntiteStatique {
    public Liane(Jeu _jeu) { super(_jeu); }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return true; };



}

