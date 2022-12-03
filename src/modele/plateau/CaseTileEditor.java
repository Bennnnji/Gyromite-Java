package modele.plateau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CaseTileEditor extends JLabel {

    public CaseTileEditor() {
        super();
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

}
