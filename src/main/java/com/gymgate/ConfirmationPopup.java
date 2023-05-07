package com.gymgate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfirmationPopup extends JDialog {

    private int choice = 0;
    private JButton yesButton;
    private JButton noButton;

    public ConfirmationPopup(Component parentComponent) {
        super(SwingUtilities.getWindowAncestor(parentComponent));

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        int topPadding = 10;
        int leftPadding = 10;
        int bottomPadding = 10;
        int rightPadding = 10;
        contentPanel.setBorder(new EmptyBorder(topPadding, leftPadding, bottomPadding, rightPadding));

        JLabel messageLabel = new JLabel("Haluatko varmasti poistaa asiakkaan " + CustomerView.confirmationHelper + "?");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        yesButton = new JButton("Kyllä");

        noButton = new JButton("Ei");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel);

        pack();
        setLocationRelativeTo(parentComponent);
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
