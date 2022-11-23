package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

public class IA extends RealisateurDeDeplacement {

    private Direction directionCourante;
    // Design pattern singleton
    private static IA ia;
    public boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            Entite eBas = e.regarderDansLaDirection(Direction.bas);
            Entite eGauche = e.regarderDansLaDirection(Direction.gauche);
            Entite eDroite = e.regarderDansLaDirection(Direction.droite);
            if (eGauche == null && eBas != null && eBas.peutServirDeSupport()) {
                if (e.avancerDirectionChoisie(Direction.gauche))
                    ret = true;
            }
            if (eDroite == null && eBas != null && eBas.peutServirDeSupport())
                if (e.avancerDirectionChoisie(Direction.droite))
                    ret = true;
            if (eBas != null && eBas.peutPermettreDeMonterDescendre()) {
                if (e.avancerDirectionChoisie(Direction.haut))
                    ret = true;
            }
        }

        return ret;

    }

    public static IA getInstance() {
        if (ia == null) {
            ia = new IA();
        }
        return ia;
    }
    public void resetDirection() {
        directionCourante = null;
    }
}
