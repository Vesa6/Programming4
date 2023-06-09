package com.gymgate;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class HomeView implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final Logger logger = DbgLogger.getLogger();

    // This is to dynamically change sizes of all buttons at toolbar at once
    private static int TB_HEIGHT = 50;

    public HomeView(JTextField usernameField, JPasswordField passwordField) {
        /*
         * Creates a UI for the home screen.
         */
        logger.info("Created an instance of HomeView");

        this.usernameField = usernameField;
        this.passwordField = passwordField;

        if (DbgLogger.isDisplayDummyRFID()) {
            displayDummyRFID();
        }
    }

    private void displayDummyRFID() {
        /*
         * Dummy RFID is used for testing to simulate the
         * functionalities of the gym access control system.
         * If enabled, will appear when running the program
         * This will be turned off by default in the configurations
         */
        JFrame dummyRFIDFrame = new JFrame("Dummy RFID");
        dummyRFIDFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dummyRFIDFrame.setSize(300, 300);
        dummyRFIDFrame.setLocation(50, 300);
        dummyRFIDFrame.setAlwaysOnTop(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(Box.createVerticalStrut(20));
        JLabel CustomerIdLabel = new JLabel("Asiakas ID");
        CustomerIdLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        CustomerIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(CustomerIdLabel);

        JTextField customerField = new JTextField(10);
        customerField.setMaximumSize(customerField.getPreferredSize());
        customerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(customerField);
        mainPanel.add(Box.createVerticalStrut(50));

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel();

        JButton svButton = new JButton("Tallenna");
        svButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPanel.add(svButton);

        svButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = customerField.getText();
                try{
                    Integer.parseInt(input);
                }catch (NumberFormatException ne){
                    logger.warning("Invalid format of customer id: " + input + " : " + e);
                    customerField.setText("");
                    return;
                }
                CustomerDatabase.getInstance().addRFIDEvent(Integer.valueOf(customerField.getText()));
                customerField.setText("");

            }
        });

        btnPanel.add(Box.createHorizontalGlue()); // Creates horizontal space between elements

        JButton resetButton = new JButton("Nollaa");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerField.setText("");
            }
        });

        btnPanel.add(resetButton);

        mainPanel.add(btnPanel);
        dummyRFIDFrame.add(mainPanel);
        dummyRFIDFrame.setVisible(true);
    }

    private void openCustomerView() {
        new CustomerView();
    }

    private void openEventViewer() {
        new EventViewer();
    }

    private void openHelpWindow() {
        /*
         * Opens help window
         */

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

        JLabel text = new JLabel("Kesto (kk):");

        // Make the fields invisible unless radiobutton activated
        months.setVisible(false);
        visits.setVisible(false);
        text.setVisible(false);

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
                    text.setVisible(true);
                    visits.setVisible(false);
                } else if (e.getSource() == membershipType2) {
                    months.setVisible(false);
                    text.setVisible(false);
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
        leftPanel.add(text);
        leftPanel.add(months);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(membershipType2);
        leftPanel.add(visits);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(Box.createVerticalStrut(260));

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

        JTextArea additionalInfo = new JTextArea(20, 20);
        additionalInfo.setLineWrap(true);
        additionalInfo.setMaximumSize(new Dimension(400, 100));
        additionalInfo.setLineWrap(true);
        rightPanel.add(new JLabel("Lisätiedot:"));
        rightPanel.add(additionalInfo);
        rightPanel.add(Box.createVerticalGlue());

        JButton rfidButton = new JButton("Lisää RFID");

        JPanel rfidPanel = new JPanel();
        rfidPanel.setLayout(new BoxLayout(rfidPanel, BoxLayout.X_AXIS));
        rfidPanel.add(Box.createHorizontalStrut(78));

        rfidPanel.add(rfidButton);

        rightPanel.add(rfidPanel);

        rightPanel.add(Box.createVerticalStrut(30));
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

        rfidButton.addActionListener(e -> {

            HidingPopup popup = new HidingPopup(mainPanel, "RFID lisätty", 2000, "Icons/checkMark.png");
            popup.showPopup();

        });

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

                 //Check phonenumber. Only the first character can be +, all the rest need to be numbers.
                 for (char num : phoneNumberField.getText().toCharArray()) {

                    int u = 0;

                    if (num == '+' && u == 0) {
                        continue;
                    }
                    if (!Character.isDigit(num)) {
                        HidingPopup popup = new HidingPopup(addUserFrame,
                            "Muutos epäonnistui, puhelinnumero on virheellisessä muodossa.", 2000);
                        popup.showPopup();
                        logger.info("Adding user failed, phone number is not in correct format.");
                        return;
                    }
                }

                if ((e.getSource() == saveButton) && (membershipType.isSelected())) {
                    DateTimeFormatter sqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate currentDate = LocalDate.now();
                    String currentDateString = currentDate.format(sqlDateFormat);
                    LocalDate endDate = currentDate.plusMonths(Integer.valueOf(months.getText()));
                    String endDateString = endDate.format(sqlDateFormat);

                    String memberTypeSelected = "Kuukausijäsenyys";

                    if ((firstNameField.getText().isEmpty()) || (lastNameField.getText().isEmpty()) || (phoneNumberField.getText().isEmpty()) || (emailField.getText().isEmpty()) ||
                    months.getText().isEmpty() || (homeAddressField.getText().isEmpty()))
                    {
                        HidingPopup popup = new HidingPopup(addUserFrame,
                            "Muutos epäonnistui, kaikki kentät on täytettävä.", 2000);
                        popup.showPopup();
                        logger.info("Adding user failed, all fields are not filled.");
                        return;
                    }
                    

                    CustomerDatabase.getInstance().addCustomerMonthly(firstNameField.getText(), lastNameField.getText(),
                            phoneNumberField.getText(), emailField.getText(), memberTypeSelected, currentDateString,
                            endDateString, homeAddressField.getText(), 0, additionalInfo.getText());

                        HidingPopup popup = new HidingPopup(addUserFrame,
                            "Asiakas lisätty.", 2000, "Icons/checkMark.png");
                        popup.showPopup();
                        addUserFrame.dispose();

                } else if ((e.getSource() == saveButton) && (membershipType2.isSelected())) {

                    String memberTypeSelected = "Kertakäynti";


                    if ((firstNameField.getText().isEmpty()) || (lastNameField.getText().isEmpty()) || (phoneNumberField.getText().isEmpty()) || (emailField.getText().isEmpty()) ||
                    visits.getText().isEmpty() || (homeAddressField.getText().isEmpty()))
                    {
                        HidingPopup popup = new HidingPopup(addUserFrame,
                                "Muutos epäonnistui, kaikki kentät on täytettävä.", 2000);
                        popup.showPopup();
                        logger.info("Adding user failed, all fields are not filled.");
                        return;
                    }

                    try {
                        CustomerDatabase.getInstance().addCustomerVisits(firstNameField.getText(),
                                lastNameField.getText(),
                                phoneNumberField.getText(), emailField.getText(), memberTypeSelected,
                                homeAddressField.getText(), Integer.valueOf(visits.getText()),
                                additionalInfo.getText());

                                HidingPopup popup = new HidingPopup(addUserFrame,
                             "Asiakas lisätty.", 2000, "Icons/checkMark.png");
                                popup.showPopup();
                                addUserFrame.dispose();

                    } catch (NumberFormatException nE) {

                        HidingPopup popup = new HidingPopup(addUserFrame,
                                "Muutos epäonnistui, käyntikerrat voivat olla vain kokonaislukuja.", 2000);
                        popup.showPopup();
                    }

                    // Close the frame
                    Component component = (Component) e.getSource();
                    JFrame frame = (JFrame) SwingUtilities.getRoot(component);
                    frame.dispose();
                }
            }
        };

        saveButton.addActionListener(saveListener);
    }

    private void createPostLoginGUI(ActionEvent e, String enteredUsername, String enteredPassword) {

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

        if (CustomerDatabase.getInstance().checkCredentials(enteredUsername, enteredPassword)) {

            ImageIcon lisääAsiakasIcon = iconFactory("Icons/Add_customer_icon.png", 24, 24);
            ImageIcon selaaTapahtumiaIcon = iconFactory("Icons/List_icon.png", 24, 24);
            ImageIcon selaaAsikkaitaIcon = iconFactory("Icons/List_customers_icon.png", 24, 24);

            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();

            JFrame home = new JFrame("GymGate client");
            home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            URL urlGymGate = Main.class.getResource("Icons/GymGate.png");
            ImageIcon imageIcon = new ImageIcon(urlGymGate);
            Image image = imageIcon.getImage();
            image = image.getScaledInstance(400, 228, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(image);

            JLabel label = new JLabel();
            label.setText("Tervetuloa GymGateen!");
            label.setIcon(imageIcon);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);

            Font arialFont = new Font("Arial", Font.PLAIN, 24);
            label.setFont(arialFont);

            JButton addMemberButton = new JButton("Lisää asiakas", lisääAsiakasIcon);
            addMemberButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            addMemberButton.addActionListener(addUserButtonListener);

            JButton viewLogsButton = new JButton("Selaa tapahtumia", selaaTapahtumiaIcon);
            viewLogsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewLogsButton.addActionListener(viewEventsButtonListener);

            JButton viewCustomersButton = new JButton("Selaa asiakkaita", selaaAsikkaitaIcon);
            viewCustomersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewCustomersButton.addActionListener(viewCustomersButtonListener);

            createMenuBar(addUserButtonListener, helpButtonListener, viewEventsButtonListener, home);

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

            home.add(mainPanel, BorderLayout.CENTER);

            home.setExtendedState(JFrame.MAXIMIZED_BOTH);
            home.setLocationRelativeTo(null);
            home.setVisible(true);
        }
    }

    private void createMenuBar(ActionListener addUserButtonListener, ActionListener helpButtonListener,
            ActionListener viewEventsButtonListener, JFrame home) {

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("");
        menu.setIcon(iconFactory("Icons/Menu_icon.png", TB_HEIGHT, TB_HEIGHT));
        ImageIcon manageCustomersIcon = iconFactory("Icons/Edit_users_icon.png", TB_HEIGHT, TB_HEIGHT);

        JMenu manage = new JMenu("Asiakkaiden hallinta");
        manage.setIcon(manageCustomersIcon);

        ImageIcon viewCustomersIcon = iconFactory("Icons/List_customers_icon.png", TB_HEIGHT - 15,
                TB_HEIGHT - 15);
        ImageIcon addCustomersIcon = iconFactory("Icons/List_icon.png", TB_HEIGHT, TB_HEIGHT);
        ImageIcon viewEventsIcon = iconFactory("Icons/Add_customer_icon.png", TB_HEIGHT - 15, TB_HEIGHT - 15);

        JMenuItem adder, browser, events;

        events = new JMenuItem("Tapahtumat");
        events.addActionListener(viewEventsButtonListener);
        events.setIcon(addCustomersIcon);
        adder = new JMenuItem("Lisää asiakas");
        adder.addActionListener(addUserButtonListener);
        adder.setIcon(viewEventsIcon);
        browser = new JMenuItem("Selaa asiakkaita");
        browser.addActionListener(viewEventsButtonListener);
        browser.setIcon(viewCustomersIcon);
        menu.add(manage);
        menu.add(events);
        manage.add(adder);
        manage.add(browser);
        mb.add(menu);

        JButton sendHelp = new JButton("");
        sendHelp.setIcon(iconFactory("Icons/Help_icon.png", TB_HEIGHT, TB_HEIGHT));
        sendHelp.setPreferredSize(new Dimension(TB_HEIGHT, TB_HEIGHT));
        sendHelp.setMargin(new Insets(2, 2, 2, 2));
        sendHelp.setBorderPainted(false);
        sendHelp.setContentAreaFilled(false);
        sendHelp.addActionListener(helpButtonListener);
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.white);
        mb.add(sep);
        mb.add(sendHelp);

        home.setJMenuBar(mb);

        JMenu userMenu = new JMenu(usernameField.getText());

        userMenu.setIcon(iconFactory("Icons/Profile_icon.png", TB_HEIGHT, TB_HEIGHT));
        JMenuItem logout = new JMenuItem("Kirjaudu ulos",
                iconFactory("Icons/Sign_out_Icon.png", TB_HEIGHT, TB_HEIGHT));
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JMenuItem changePassword = new JMenuItem("Vaihda salasana",
                iconFactory("Icons/Settings_icon.png", TB_HEIGHT, TB_HEIGHT));
        changePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChangePasswordGUI(usernameField.getText());
            }
        });

        userMenu.add(changePassword);
        userMenu.add(logout);
        mb.add(userMenu);

    }

    /*
     * This is used as a helper when the user logs in.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());

        createPostLoginGUI(e, enteredUsername, enteredPassword);

    }

    public ImageIcon iconFactory(String iName, int iHeight, int iWidth) {
        URL urlImg = Main.class.getResource(iName);
        ImageIcon imgIcon = new ImageIcon(urlImg);
        Image img = imgIcon.getImage().getScaledInstance(iHeight, iWidth, Image.SCALE_SMOOTH);
        ImageIcon imgScaled = new ImageIcon(img);

        return imgScaled;
    }
}
