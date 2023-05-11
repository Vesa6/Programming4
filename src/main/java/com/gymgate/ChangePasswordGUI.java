package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.Crypt;

public class ChangePasswordGUI {
    /*
     * Creates a GUI for password changing
     */
    String newPass = "";
    String newPassRepeat = "";
    String username = "";
    String oldPass = "";
    JPasswordField oldPassField = null;
    JPasswordField newPassField = null;
    JPasswordField newPassRepeatField = null;
    JFrame changePassFrame = null;
    private static final Logger logger = DbgLogger.getLogger();

    public ChangePasswordGUI(String username) {
        logger.info("Created an instance of ChangePasswordGUI");
        changePassFrame = new JFrame("Vaihda salasanasi");

        ActionListener saveButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setOldPass(String.valueOf(oldPassField.getPassword()));
                setNewPass(String.valueOf(newPassField.getPassword()));
                setNewPassRepeat(String.valueOf(newPassRepeatField.getPassword()));
                setUsername(username);
                changePassword();
            }
        };

        ActionListener cancelButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassFrame.dispose();
            }
        };

        changePassFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (screenSize.width * 0.2);
        int frameHeight = (int) (screenSize.height * 0.4);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        Dimension textBoxDimension = new Dimension(200, 20);

        mainPanel.add(Box.createVerticalStrut(40));

        oldPassField = new JPasswordField(20);
        JLabel oldPassLabel = new JLabel("Vanha salasana:");
        oldPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        oldPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        oldPassField.setMaximumSize(textBoxDimension);
        mainPanel.add(oldPassLabel);
        mainPanel.add(oldPassField);
        mainPanel.add(Box.createVerticalGlue());

        newPassField = new JPasswordField(20);
        JLabel newPassLabel = new JLabel("Uusi salasana:");
        newPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPassField.setMaximumSize(textBoxDimension);
        mainPanel.add(newPassLabel);
        mainPanel.add(newPassField);
        mainPanel.add(Box.createVerticalGlue());

        newPassRepeatField = new JPasswordField(20);
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

        saveButton.addActionListener(saveButtonListener);

        JButton cancelButton = new JButton("Peruuta");
        buttonPanel.add(saveButton);
        cancelButton.addActionListener(cancelButtonListener);

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);

        changePassFrame.add(buttonPanel, BorderLayout.SOUTH);

        changePassFrame.setSize(frameWidth, frameHeight);
        changePassFrame.setLocationRelativeTo(null);
        changePassFrame.setVisible(true);

    }

    public void changePassword() {
        /* 
         * User can't change password if:
         * 1. The old password is invalid
         * 2. The new passwords needs to be written twice and they aren't identical
         * 3. The new password is identical to the old one
         */
        if (!checkIfCorrectPass()) {
            logger.info("User failed to change password. Reason: Wrong old password");
            HidingPopup inCorrectPass = new HidingPopup(changePassFrame, "Väärä salasana", 1000,
                    "Icons/Denied_icon.png");
            emptyFields();
            inCorrectPass.showPopup();
        } else if (!comparePasswords()) {
            logger.info("User failed to change password. Reason: New passwords do not match");
            HidingPopup nonMatchPass = new HidingPopup(changePassFrame, "Salasanat eivät täsmää", 1000,
                    "Icons/Denied_icon.png");
            emptyFields();
            nonMatchPass.showPopup();
        } else if (identicalPasswords()) {
            logger.info("User failed to change password. Reason: Attempted to use same password as the present one");
            HidingPopup samePassPopup = new HidingPopup(changePassFrame, "Uusi salasana ei voi olla sama kuin vanha",
                    2000, "Icons/Denied_icon.png");
            emptyFields();

            samePassPopup.showPopup();
        } else {
            CustomerDatabase.getInstance().changePassword(getUsername(), getNewPass());
            logger.info("User changed the password successfully");
            HidingPopup successPopup = new HidingPopup(changePassFrame, "Salasana vaihdettu", 2000,
                    "Icons/checkMark.png");
            successPopup.showPopup();
            changePassFrame.dispose();
        }

    }

    public void emptyFields() {
        this.oldPassField.setText("");
        this.newPassField.setText("");
        this.newPassRepeatField.setText("");
    }

    public boolean checkIfCorrectPass() {
        String hPassToCompare = CustomerDatabase.getInstance().getCryptedPassword(getUsername());
        return (hPassToCompare.equals(Crypt.crypt(getOldPass(), hPassToCompare)));
    }

    public boolean comparePasswords() {
        return (getNewPass().equals(getNewPassRepeat()));
    }

    public boolean identicalPasswords() {
        return getNewPass().equals(getOldPass());
    }

    public String getNewPass() {
        return this.newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getNewPassRepeat() {
        return this.newPassRepeat;
    }

    public void setNewPassRepeat(String newPassRepeat) {
        this.newPassRepeat = newPassRepeat;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPass() {
        return this.oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

}
