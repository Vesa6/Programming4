package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChangePasswordGUI {


    public ChangePasswordGUI() {

        JFrame changePassFrame = new JFrame("Vaihda salasanasi");
        changePassFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (screenSize.width * 0.2);
        int frameHeight = (int) (screenSize.height * 0.4);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        Dimension textBoxDimension = new Dimension(200, 20);

        mainPanel.add(Box.createVerticalStrut(40));

        JTextField oldPassField = new JTextField(20);
        JLabel oldPassLabel = new JLabel("Vanha salasana:"); 
        oldPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        oldPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        oldPassField.setMaximumSize(textBoxDimension);
        mainPanel.add(oldPassLabel);
        mainPanel.add(oldPassField);
        mainPanel.add(Box.createVerticalGlue());

        JTextField newPassField = new JTextField(20);
        JLabel newPassLabel = new JLabel("Uusi salasana:"); 
        newPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPassField.setMaximumSize(textBoxDimension);
        mainPanel.add(newPassLabel);
        mainPanel.add(newPassField);
        mainPanel.add(Box.createVerticalGlue());

        JTextField newPassRepeatField = new JTextField(20);
        JLabel newPassRepeatLabel = new JLabel("Uusi salasana uudelleen:"); 
        newPassRepeatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPassRepeatField.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPassRepeatField.setMaximumSize(textBoxDimension);
        mainPanel.add(newPassRepeatLabel);
        mainPanel.add(newPassRepeatField);
        mainPanel.add(Box.createVerticalGlue());

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
        containerPanel.add(Box.createHorizontalGlue());
        containerPanel.add(mainPanel);
        containerPanel.add(Box.createHorizontalGlue());

        changePassFrame.add(containerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        JButton saveButton = new JButton("Tallenna");
        JButton cancelButton = new JButton("Peruuta");
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        
        changePassFrame.add(buttonPanel, BorderLayout.SOUTH);

        changePassFrame.setSize(frameWidth, frameHeight);
        changePassFrame.setLocationRelativeTo(null);
        changePassFrame.setVisible(true);



 
    }
}
