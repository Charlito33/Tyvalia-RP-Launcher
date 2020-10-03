package fr.tyvaliarp.launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LauncherAlertPanel extends JPanel {
    int frameWidth, frameHeight;
    String title, text;
    JButton closeButton;

    LauncherAlertPanel INSTANCE;

    public LauncherAlertPanel(JFrame frame, String title, String text) {
        this.INSTANCE = this;

        this.frameWidth = frame.getWidth();
        this.frameHeight = frame.getHeight();

        this.title = title;
        this.text = text;

        setBackground(new Color(0, 0, 0, 0));
        setForeground(new Color(255, 255, 255, 255));
        setLayout(null);

        closeButton = new JButton("Fermer");
        closeButton.setForeground(new Color(255, 255, 255, 255));
        closeButton.setBounds(frameWidth - 10 - 100, frameHeight - 10 - 25, 100, 25);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(INSTANCE).dispose();
            }
        });

        add(closeButton);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(frameWidth - 10 - 100, frameHeight - 10 - 25, 100, 25);

        g.setColor(Color.WHITE);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        g.drawString("Fermer", frameWidth - 80, frameHeight - 18);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frameWidth, 38);

        g.setColor(Color.WHITE);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 28));
        g.drawString(title, 5, 28);

        g.setColor(new Color(255, 255, 255, 191));
        g.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        g.drawString(text, 5, 60);
    }
}
