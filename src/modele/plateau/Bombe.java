package modele.plateau;

public class Bombe extends EntiteStatique {
    public Bombe(Jeu _jeu) { super(_jeu); }
    public boolean peutServirDeSupport() { return false; }
    public boolean peutEtreRamasse() { return true; }

    public boolean estEnnemi() { return false; }
    @Override
    public boolean peutEtreTraversee() {
        return true;
    }
}

