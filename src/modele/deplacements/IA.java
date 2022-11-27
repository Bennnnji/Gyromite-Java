package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

public class IA extends RealisateurDeDeplacement {

    private Direction directionCourante;
    // Design pattern singleton
    private static IA ia;

    private boolean alreadyChoose = false;
    public boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            Entite eBas = e.regarderDansLaDirection(Direction.bas);
            Entite eHaut = e.regarderDansLaDirection(Direction.haut);
            Entite eGauche = e.regarderDansLaDirection(Direction.gauche);
            Entite eDroite = e.regarderDansLaDirection(Direction.droite);
            if (eGauche == null && eBas != null && eBas.peutServirDeSupport() && !alreadyChoose) {
                if (e.avancerDirectionChoisie(Direction.gauche))
                    ret = true;
                alreadyChoose = true;
            }
            else alreadyChoose = false;
            if (eDroite == null && eBas != null && eBas.peutServirDeSupport() && !alreadyChoose) {
                if (e.avancerDirectionChoisie(Direction.droite))
                    ret = true;
                alreadyChoose = true;
            }
            else alreadyChoose = false;
            if (eBas != null && eBas.peutPermettreDeMonterDescendre() && !alreadyChoose) {
                if (e.avancerDirectionChoisie(Direction.haut))
                    ret = true;
                alreadyChoose = true;
            }
            if(eHaut != null && eHaut.peutPermettreDeMonterDescendre() && !alreadyChoose) {
                if(e.avancerDirectionChoisie(Direction.haut))
                    ret = true;
                alreadyChoose = true;
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

    public static void  reset()
    {
        ia = new IA();
    }
    public void resetDirection() {
        directionCourante = null;
    }
}
