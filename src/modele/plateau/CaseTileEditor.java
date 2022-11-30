package modele.plateau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import VueControleur.VueControleurGyromite;

public class CaseTileEditor extends JLabel {
    private int x;
    private int y;
    private int tile;

    private Entite EntiteCourante;

    public Entite getEntiteCourante() {
        return EntiteCourante;
    }

    public CaseTileEditor(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        this.tile = y * 28 + x;
        this.EntiteCourante = null;
        setOpaque(true);
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.WHITE);
            }
        });
    }

    public void setEntite(Entite e) {
        EntiteCourante = e;
    }

}
