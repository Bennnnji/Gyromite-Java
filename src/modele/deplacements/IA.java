package modele.deplacements;

import modele.plateau.Bot;
import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

public class IA extends RealisateurDeDeplacement {
    private static IA ia;

    public static IA getInstance() {
        if (ia == null) {
            ia = new IA();
        }
        return ia;
    }

    @Override
    protected boolean realiserDeplacement() {
        boolean ret = false;
        Direction directionCourante , directionPrecedente;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            Bot smick = (Bot) e;
            directionCourante = smick.getDirectionCourante();
            Entite eDirection = e.regarderDansLaDirection(directionCourante);
            if(directionCourante != null)
            {
                switch(directionCourante)
                {
                    case droite, gauche:
                        directionPrecedente = directionCourante;
                        smick.setDirectionCourante(directionCourante);
                        smick.setAncienneDirection(directionPrecedente);
                        if(e.avancerDirectionChoisie(directionCourante))
                            ret = true;
                        if (directionCourante == Direction.droite && eDirection != null && eDirection.peutServirDeSupport() && !eDirection.peutPermettreDeMonterDescendre()) {
                            smick.setDirectionCourante(Direction.gauche);
                            ret = false;
                        } else if (directionCourante == Direction.gauche && eDirection != null && eDirection.peutServirDeSupport() && !eDirection.peutPermettreDeMonterDescendre()) {
                            smick.setDirectionCourante(Direction.droite);
                            ret = false;
                        }
                        break;
                    case haut:
                        Entite eBas = e.regarderDansLaDirection(Direction.bas);
                        if(eBas != null && eBas.peutServirDeSupport())
                        {
                            if(e.avancerDirectionChoisie(Direction.haut))
                                ret = true;
                        }
                        break;
                    case bas:
                        if(e.avancerDirectionChoisie(Direction.bas))
                            ret = true;
                        break;

                }
            }

        }

        return ret;
    }

    public static IA reset() {
        ia = new IA();
        return ia;
    }
}