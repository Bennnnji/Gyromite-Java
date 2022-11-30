package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

/**
 * Controle4Directions permet d'appliquer une direction (connexion avec le clavier) à un ensemble d'entités dynamiques
 */
public class ColonneDeplB extends RealisateurDeDeplacement {
    private Direction directionCourante;
    // Design pattern singleton
    private static ColonneDeplB cCol;
    private boolean estEnHaut = true; /** position of the column. false for down, true for up */
    private static final int HAUTEUR_DEPLACEMENT = 2; /** nombre de mouvement des colonnes */
    private int nbrDeplacement = 0; /** nombre courant de deplacement */

    public static ColonneDeplB getInstance() {
        if (cCol == null) {
            cCol = new ColonneDeplB();
        }
        return cCol;
    }

    public static ColonneDeplB reset() {
        cCol = new ColonneDeplB();
        return cCol;
    }

    public void setDirectionCourante() {
        directionCourante = estEnHaut ? Direction.bas : Direction.haut;
        System.out.println("direction courante : " + directionCourante);
    }

    public boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            int nbrColonne = lstEntitesDynamiques.size();
            if (directionCourante != null && nbrDeplacement < (HAUTEUR_DEPLACEMENT * nbrColonne)){
                System.out.print("nbDeplacement : ");
                System.out.println(nbrDeplacement);
                System.out.print("hauteur max : ");
                System.out.println(HAUTEUR_DEPLACEMENT);
                ret = e.avancerDirectionChoisie(directionCourante);
                if (ret)
                    nbrDeplacement++;
                //HAUTEUR_DEPLACEMENT * (lstEntitesDynamiques.size() + 1)
            }
            else if (nbrDeplacement >= (HAUTEUR_DEPLACEMENT * nbrColonne)) {
                resetDirection();
                estEnHaut = !estEnHaut;
                nbrDeplacement = 0;
            }
        }

        return ret;
    }

    public void resetDirection() {
        directionCourante = null;
    }

}