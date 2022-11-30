package modele.deplacements;
import modele.plateau.EntiteDynamique;

public class IA extends RealisateurDeDeplacement {
    public static Object getListeEntitesDynamiques;
    private Direction directionCourante = Direction.droite;
    private Direction directionPrecedente = this.directionCourante;

    private static IA ia;

    public void setDirectionCourante(Direction _directionCourante) {

        directionCourante = _directionCourante;
    }

    public IA(){

    }

    public boolean peutAvancer(EntiteDynamique e){
        // test si le bot fait face à un objet qui infranchissable
        //      si
        if((e.regarderDansLaDirection(directionCourante) != null && !e.regarderDansLaDirection(directionCourante).peutEtreTraversee()) ||
                (e.regarderDansLaDirection(Direction.bas) != null && e.regarderDansLaDirection(Direction.bas).peutServirDeSupport() && e.regarderDansLaDirection(Direction.bas).regarderDansLaDirection(directionCourante) == null)
            /*|| (directionEchelle != null && e.regarderDansLaDirection(Direction.bas) instanceof Echelle)*/){
            return false;
        }
        return true;
    }

    public static IA getInstance()
    {
        if (ia == null) {
            ia = new IA();
        }
        return ia;
    }

    public static IA reset() {
        ia = new IA();
        return ia;
    }
    protected boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            if (directionCourante != null){
                if(e.regarderDansLaDirection(directionCourante) != null && !e.regarderDansLaDirection(directionCourante).peutEtreTraversee() ||
                        (e.regarderDansLaDirection(Direction.bas) != null && !(e.regarderDansLaDirection(Direction.bas).peutPermettreDeMonterDescendre()) && e.regarderDansLaDirection(Direction.bas).peutServirDeSupport() && e.regarderDansLaDirection(Direction.bas).regarderDansLaDirection(directionCourante) == null )){

                    reverseDirection();
                }

                if (e.regarderDansLaDirection(Direction.haut) != null && e.regarderDansLaDirection(Direction.haut).peutPermettreDeMonterDescendre() && directionCourante != Direction.bas) {
                    if (directionCourante != Direction.haut) {
                        directionPrecedente = directionCourante;
                    }
                    directionCourante = Direction.haut;
                }
                else if (e.regarderDansLaDirection(Direction.bas) != null &&e.regarderDansLaDirection(Direction.bas).peutPermettreDeMonterDescendre() && directionCourante != Direction.haut) {
                    if (directionCourante != Direction.bas) {
                        directionPrecedente = directionCourante;
                    }
                    directionCourante = Direction.bas;
                }
                else if(directionCourante == Direction.bas || directionCourante == Direction.haut){
                    directionCourante = directionPrecedente;
                }
                /*else*/ if(e.avancerDirectionChoisie(directionCourante)) { ret = true;}

            }
        }
        return ret;
    }

    public void resetDirection() {
        directionCourante = null;
    }

    public void reverseDirection(){
        switch(directionCourante){
            case droite: directionCourante = Direction.gauche; break;
            case gauche: directionCourante = Direction.droite; break;
            default:
                break;
        }
    }
}

