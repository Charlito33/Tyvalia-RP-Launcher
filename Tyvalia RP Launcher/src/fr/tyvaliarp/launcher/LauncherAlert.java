package fr.tyvaliarp.launcher;

import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

public class LauncherAlert extends JFrame {
    LauncherAlertPanel panel;
    LauncherAlert INSTANCE;

    public LauncherAlert(String title, String text) {
        super("Erreur");

        this.INSTANCE = this;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addVetoableChangeListener("focusedWindow", new VetoableChangeListener() {
            private boolean gained = false;

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                    if (evt.getNewValue() == INSTANCE) {
                        gained = true;
                    }
                    if (gained && evt.getNewValue() != INSTANCE) {
                        INSTANCE.dispose();
                    }
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(480, 240);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0.5f));

        panel = new LauncherAlertPanel(this, title, text);

        add(panel);

        AWTUtilities.setWindowShape(this, new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 10, 10));

        setVisible(true);
        setLocationRelativeTo(null);
        setBounds(getX(), getY() - 50, getWidth(), getHeight());
        toFront();
    }


}
