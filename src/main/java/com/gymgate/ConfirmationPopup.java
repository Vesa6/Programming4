package com.gymgate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.logging.Logger;

public class ConfirmationPopup extends JDialog {

    private static final Logger logger = DbgLogger.getLogger();
    private JButton yesButton;
    private JButton noButton;

    public ConfirmationPopup(Component comp) {
        super(SwingUtilities.getWindowAncestor(comp));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        logger.fine("Showing confirmation popup message");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel msgLabel = new JLabel("Haluatko varmasti poistaa asiakkaan " + CustomerView.confirmationHelper + "?");
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        yesButton = new JButton("Kyllä");

        noButton = new JButton("Ei");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        panel.add(Box.createVerticalGlue());
        panel.add(msgLabel);
        panel.add(Box.createVerticalGlue());
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        add(panel);

        pack();
        setLocationRelativeTo(comp);
    }

    public void showPopup() {
        setVisible(true);
    }

    public JButton getYesButton() {
        return this.yesButton;
    }

    public JButton getNoButton() {
        return this.noButton;
    }
}
