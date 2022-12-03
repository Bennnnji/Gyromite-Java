package modele.plateau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CaseChoixTileEditor extends JLabel {

    public CaseChoixTileEditor() {
        super();
        setOpaque(true);
        setBackground(Color.black);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.lightGray);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.black);
            }

        });
    }

}
