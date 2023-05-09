package com.gymgate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class HidingPopup extends JDialog {

    private int delay_in_ms;
    private final Timer timer;

    public HidingPopup(Component parentComponent, String message, int delay) {
        super(SwingUtilities.getWindowAncestor(parentComponent));
        this.delay_in_ms = delay;

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(new Dimension(450,100));

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(messageLabel);
        add(Box.createVerticalGlue());

        //This makes the window size match the components
        
        setLocationRelativeTo(parentComponent);

        /*
         * This timer is used to hide the popup after a certain amount of time.
         */
        timer = new Timer(this.delay_in_ms, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        timer.setRepeats(false);
    }

    public HidingPopup(Component parentComponent, String message, int delay, String imagePath) {
        super(SwingUtilities.getWindowAncestor(parentComponent));
        this.delay_in_ms = delay;

        URL url = HidingPopup.class.getResource(imagePath);
        ImageIcon imageIcon = new ImageIcon(url);
        Image image = imageIcon.getImage();
        image = image.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel(message, imageIcon, SwingConstants.CENTER);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(messageLabel);
        add(Box.createVerticalGlue());

        //This makes the window size match the components
        pack();
        setLocationRelativeTo(parentComponent);

        /*
         * This timer is used to hide the popup after a certain amount of time.
         */
        timer = new Timer(this.delay_in_ms, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        timer.setRepeats(false);
    }

    public void showPopup() {
        timer.start();
        setVisible(true);
    }
}
