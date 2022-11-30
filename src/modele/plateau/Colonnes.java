package modele.plateau;

public abstract class Colonnes extends EntiteDynamique {
    public Colonnes(Jeu _jeu) { super(_jeu); }

    public boolean peutEtreEcrase() { return false; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false; };

    public boolean peutEtreRamasse() { return false; }

    public boolean estEnnemi() { return false; }

    public boolean estPilier() { return true; }
    @Override
    public boolean peutEtreTraversee() {
        return false;
    }
}
