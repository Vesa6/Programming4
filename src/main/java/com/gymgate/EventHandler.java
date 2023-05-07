package com.gymgate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.TextArea;

public class EventHandler implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;

    // This is to dynamically change sizes of all buttons at toolbar at once
    private static int BTNTBAR_HEIGHT = 50;

    public EventHandler(JTextField usernameField, JPasswordField passwordField) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
    }

    private void openCustomerView() {
        new CustomerView();
    }

    private void openEventViewer(){
        new EventViewer();
    }

    private void openHelpWindow() {

        JLabel ggLabel = new JLabel();
        ggLabel.setText("GYMGATE OY");
        ggLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        ggLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ggLabel.setFont(new Font("Arial", Font.PLAIN, 28));

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);

        panel.add(ggLabel);
        String[] contactInfo = { "tuki@gymgate.com", "040423123" };
        for (int i = 0; i < contactInfo.length; i++) {
            JLabel contactLabel = new JLabel();
            contactLabel.setText(contactInfo[i]);
            contactLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            contactLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contactLabel.setFont(new Font("Arial", Font.PLAIN, 22));
            panel.add(contactLabel);
        }

        JButton closeButton = new JButton("Sulje");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(closeButton);
        // Calculating the center of the screen to get the help window open in the
        // middle
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centX = (int) screenSize.getWidth() / 2;
        int centY = (int) screenSize.getHeight() / 2;

        JFrame helpFrame = new JFrame("Tuki");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        closeButton.addActionListener(e -> {
            helpFrame.dispose();
        });
        // Empty space above gymgate oy text
        ggLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        helpFrame.setSize(240, 180);
        helpFrame.setLocation(centX - helpFrame.getWidth() / 2, centY - helpFrame.getHeight() / 2);
        helpFrame.setVisible(true);
        helpFrame.add(panel);
    }

    public static void createAddUserGUI() {

        JFrame addUserFrame = new JFrame("Lisää asiakas");
        addUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel addUserLabel = new JLabel("Lisää asiakas");
        addUserLabel.setHorizontalAlignment(JLabel.CENTER);
        addUserLabel.setVerticalAlignment(JLabel.TOP);

        // Add padding. For whatever reason, struts don't work here.
        addUserLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addUserFrame.add(addUserLabel, BorderLayout.NORTH);

        // Get screen dimensions and calculate the window size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (screenSize.width * 0.6);
        int frameHeight = (int) (screenSize.height * 0.8);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        Dimension textBoxDimension = new Dimension(400, 20);
        Dimension textBoxDimensionRight = new Dimension(200, 20);

        // This creates an empty space between the first row of text boxes and the title
        // of the view.
        leftPanel.add(Box.createVerticalStrut(100));
        // And this creates some space from left corner.
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        /////////// First row of text boxes ///////////

        /////////// STUFF IN THE LEFT PANEL ///////////

        JTextField firstNameField = new JTextField(20);
        firstNameField.setMaximumSize(textBoxDimension);
        leftPanel.add(new JLabel("Etunimi:"));
        leftPanel.add(firstNameField);
        leftPanel.add(Box.createVerticalGlue());

        JTextField homeAddressField = new JTextField(20);
        homeAddressField.setMaximumSize(textBoxDimension);
        leftPanel.add(new JLabel("Kotiosoite:"));
        leftPanel.add(homeAddressField);
        leftPanel.add(Box.createVerticalGlue());

        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(textBoxDimension);
        leftPanel.add(new JLabel("Sähköposti:"));
        leftPanel.add(emailField);
        leftPanel.add(Box.createVerticalGlue());

        ButtonGroup membershipTypeGroup = new ButtonGroup();
        JTextField months = new JTextField(20);
        months.setMaximumSize(textBoxDimension);
        JTextField visits = new JTextField(20);
        visits.setMaximumSize(textBoxDimension);

        // Make the fields invisible unless radiobutton activated
        months.setVisible(false);
        visits.setVisible(false);

        JRadioButton membershipType = new JRadioButton("Kuukausijäsenyys");
        JRadioButton membershipType2 = new JRadioButton("Kertakäynti");

        /*
         * This action listener is for the radio buttons.
         * It makes the text fields visible when the radio button is selected.
         * It also makes the other text field invisible.
         */
        ActionListener membershipListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == membershipType) {
                    months.setVisible(true);
                    visits.setVisible(false);
                } else if (e.getSource() == membershipType2) {
                    months.setVisible(false);
                    visits.setVisible(true);
                }
                leftPanel.revalidate();
                leftPanel.repaint();
            }
        };

        membershipType.addActionListener(membershipListener);
        membershipType2.addActionListener(membershipListener);

        membershipTypeGroup.add(membershipType);
        membershipTypeGroup.add(membershipType2);

        leftPanel.add(membershipType);
        leftPanel.add(months);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(membershipType2);
        leftPanel.add(visits);
        leftPanel.add(Box.createVerticalGlue());

        JButton saveButton = new JButton("Tallenna");
        leftPanel.add(saveButton);
        leftPanel.add(Box.createVerticalGlue());

        /////////// STUFF IN THE LEFT PANEL ///////////

        ///////// STUFF IN THE RIGHT PANEL /////////
        JPanel rightPanel = new JPanel();
        rightPanel.add(Box.createVerticalStrut(100));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JTextField lastNameField = new JTextField(20);
        lastNameField.setMaximumSize(textBoxDimensionRight);
        rightPanel.add(new JLabel("Sukunimi:"));
        rightPanel.add(lastNameField);
        rightPanel.add(Box.createVerticalGlue());

        JTextField phoneNumberField = new JTextField(20);
        phoneNumberField.setMaximumSize(textBoxDimensionRight);
        rightPanel.add(new JLabel("Puhelinnumero:"));
        rightPanel.add(phoneNumberField);
        rightPanel.add(Box.createVerticalGlue());

        JTextArea notesField = new JTextArea(20, 20);
        notesField.setLineWrap(true);
        notesField.setMaximumSize(new Dimension(400, 100));
        notesField.setLineWrap(true);
        rightPanel.add(new JLabel("Lisätiedot:"));
        rightPanel.add(notesField);
        rightPanel.add(Box.createVerticalGlue());

        JButton cancelButton = new JButton("Peruuta");
        rightPanel.add(cancelButton);
        rightPanel.add(Box.createVerticalGlue());

        cancelButton.addActionListener(e -> {
            addUserFrame.dispose();
        });
        ///////// STUFF IN THE RIGHT PANE /////////

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        addUserFrame.add(mainPanel, BorderLayout.CENTER);

        addUserFrame.setSize(frameWidth, frameHeight);
        addUserFrame.setLocationRelativeTo(null);
        addUserFrame.setVisible(true);

        /*
         * This action listener is for the save button.
         */
        ActionListener saveListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if ((e.getSource() == saveButton) && (membershipType.isSelected())) {
                    DateTimeFormatter sqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate currentDate = LocalDate.now();
                    String currentDateString = currentDate.format(sqlDateFormat);
                    LocalDate endDate = currentDate.plusMonths(Integer.valueOf(months.getText()));
                    String endDateString = endDate.format(sqlDateFormat);

                    // For now, only monthly customers exist.
                    String memberTypeSelected = "Kuukausijäsenyys";

                    CustomerDatabase.getInstance().addCustomerMonthly(firstNameField.getText(), lastNameField.getText(),
                            phoneNumberField.getText(), emailField.getText(), memberTypeSelected, currentDateString,
                            endDateString, homeAddressField.getText(), 0, notesField.getText());
                } else if ((e.getSource() == saveButton) && (membershipType2.isSelected())) {

                    // For now, only monthly customers exist.
                    String memberTypeSelected = "Kertakäynti";

                    CustomerDatabase.getInstance().addCustomerVisits(firstNameField.getText(), lastNameField.getText(),
                            phoneNumberField.getText(), emailField.getText(), memberTypeSelected,
                            homeAddressField.getText(), Integer.valueOf(visits.getText()), notesField.getText());
                }

                //Close the frame
                Component component = (Component) e.getSource();
                JFrame frame = (JFrame) SwingUtilities.getRoot(component);
                frame.dispose();
            }
        };

        saveButton.addActionListener(saveListener);
    }

    private void createPostLoginGUI(ActionEvent e, String enteredUsername, String enteredPassword) {

        /*
         * N.B!
         * LIST ALL THE LISTENERS BETWEEN THESE COMMENTS FOR CLARITY.
         * ...They get lost easily.
         */
        ActionListener addUserButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAddUserGUI();
            }
        };

        ActionListener helpButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHelpWindow();
            }
        };

        ActionListener viewCustomersButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomerView();
            }
        };

        ActionListener viewEventsButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEventViewer();
            }
        };
        /*
        * 
        */

        if (CustomerDatabase.getInstance().checkCredentials(enteredUsername, enteredPassword)) {
            // Close the current login frame
            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();

            // Open a new view
            JFrame newView = new JFrame("New View");
            newView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            URL urlGymGate = Main.class.getResource("GymGate.png");
            ImageIcon imageIcon = new ImageIcon(urlGymGate);
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

            JButton addMemberButton = new JButton("Lisää asiakas");
            addMemberButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            addMemberButton.addActionListener(addUserButtonListener);

            JButton viewLogsButton = new JButton("Selaa tapahtumia");
            viewLogsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewLogsButton.addActionListener(viewEventsButtonListener);

            JButton viewCustomersButton = new JButton("Selaa asiakkaita");
            viewCustomersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewCustomersButton.addActionListener(viewCustomersButtonListener);

            URL urlHelp = Main.class.getResource("Help_icon.png");
            ImageIcon helpIcon = new ImageIcon(urlHelp);

            Image helpImage = helpIcon.getImage().getScaledInstance(BTNTBAR_HEIGHT, BTNTBAR_HEIGHT, Image.SCALE_SMOOTH);
            ImageIcon helpScaled = new ImageIcon(helpImage);
            JButton helpButton = new JButton(helpScaled);
            // helpButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            helpButton.setBorder(BorderFactory.createEmptyBorder());
            helpButton.setContentAreaFilled(false);
            helpButton.setPreferredSize(new Dimension(BTNTBAR_HEIGHT, BTNTBAR_HEIGHT));
            helpButton.addActionListener(helpButtonListener);

            JPanel toolPanel = new JPanel();
            toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.X_AXIS));
            Box rightBox = Box.createHorizontalBox();
            rightBox.add(Box.createHorizontalGlue());
            rightBox.add(helpButton);
            rightBox.add(Box.createRigidArea(new Dimension(200, 0)));

            toolPanel.add(rightBox);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(Box.createVerticalStrut(200)); // Add a 200-pixel vertical space at the top
            mainPanel.add(label);
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(addMemberButton);
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(viewLogsButton);
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(viewCustomersButton);
            mainPanel.add(Box.createVerticalStrut(200)); // Add a 200-pixel vertical space at the bottom

            newView.add(mainPanel, BorderLayout.CENTER);
            newView.add(toolPanel, BorderLayout.NORTH);
            newView.setExtendedState(JFrame.MAXIMIZED_BOTH);
            newView.setLocationRelativeTo(null);
            newView.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());

        createPostLoginGUI(e, enteredUsername, enteredPassword);

    }
}
