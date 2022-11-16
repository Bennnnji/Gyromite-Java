package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

/**
 * A la reception d'une commande, toutes les cases (EntitesDynamiques) des
 * colonnes se déplacent dans la direction définie
 * (vérifier "collisions" avec le héros)
 */
public class ColonneDepl extends RealisateurDeDeplacement {

    private Direction directionCourante;
    private static ColonneDepl c3d;

    public static ColonneDepl getInstance() {
        if (c3d == null) {
            c3d = new ColonneDepl();
        }
        return c3d;
    }

    public void setDirectionCourante(Direction _directionCourante) {
        directionCourante = _directionCourante;
    }

    protected boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            if (directionCourante != null)
                switch (directionCourante) {

                    case haut:
                        // on ne peut pas sauter sans prendre appui
                        // (attention, test d'appui réalisé à partir de la position courante, si la
                        // gravité à été appliquée, il ne s'agit pas de la position affichée,
                        // amélioration possible)
                        Entite eHaut = e.regarderDansLaDirection(Direction.haut);
                        if ((eHaut != null && !eHaut.peutServirDeSupport()) || (eHaut == null)) {
                            if (e.avancerDirectionChoisie(Direction.haut))
                                ret = true;

                        }
                        break;
                    case bas:
                        Entite eBas = e.regarderDansLaDirection(Direction.bas);
                        if ((eBas != null && !eBas.peutServirDeSupport()) || (eBas == null))
                            if (e.avancerDirectionChoisie(Direction.bas))
                                ret = true;
                }
        }
        return ret;
    }

    public void resetDirection() {
        directionCourante = null;
    }
}
