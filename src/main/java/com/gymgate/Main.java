package com.gymgate;

import java.net.URL;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

public class Main {

    public static void main(String[] args) throws SQLException {

        CustomerDatabase db = new CustomerDatabase();

        createLoginGUI();
        
    }

    private static void createLoginGUI() {
        Dimension textBoxDimension = new Dimension(200, 20);

        URL url = Main.class.getResource("GymGate.png");
        ImageIcon imageIcon = new ImageIcon(url);
        Image image = imageIcon.getImage();
        image = image.getScaledInstance(200, 150, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);

        JLabel label = new JLabel();
        label.setText("Tervetuloa GymGateen!");
        label.setIcon(imageIcon);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        Font arialFont = new Font("Arial", Font.PLAIN, 24);
        label.setFont(arialFont);

        JLabel usernameLabel = new JLabel("Käyttäjänimi:");
        JTextField usernameField = new JTextField(20);
        usernameField.setMaximumSize(textBoxDimension);

        JLabel passwordLabel = new JLabel("Salasana:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(textBoxDimension);

        JButton loginButton = new JButton("Kirjaudu");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //This passes the username and password to the other class.
        loginButton.addActionListener(new HomeView(usernameField, passwordField));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createHorizontalStrut(10));
        usernamePanel.add(usernameField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createHorizontalStrut(10));
        passwordPanel.add(passwordField);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalStrut(200)); // Add a 200-pixel vertical space at the top
        mainPanel.add(label);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(200)); // Add a 200-pixel vertical space at the bottom

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        //make fullscreen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
