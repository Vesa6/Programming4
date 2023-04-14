import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.TextArea;

public class EventHandler implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public EventHandler(JTextField usernameField, JPasswordField passwordField) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
    }

    private void createAddUserGUI() {
        JFrame addUserFrame = new JFrame("Lisää asiakas");
        addUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JLabel addUserLabel = new JLabel("Lisää asiakas");
        addUserLabel.setHorizontalAlignment(JLabel.CENTER);
        addUserLabel.setVerticalAlignment(JLabel.TOP);

        //Add padding. For whatever reason, struts don't work here.
        addUserLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    
        addUserFrame.add(addUserLabel, BorderLayout.NORTH);
    
        // Get screen dimensions and calculate the window size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int)(screenSize.width * 0.6);
        int frameHeight = (int)(screenSize.height * 0.8);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        Dimension textBoxDimension = new Dimension(400, 20);
        Dimension textBoxDimensionRight = new Dimension(200, 20);

        //This creates an empty space between the first row of text boxes and the title of the view.
        leftPanel.add(Box.createVerticalStrut(100));
        //And this creates some space from left corner.
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

        JRadioButton membershipType = new JRadioButton("Kuukausijäsenyys");
        JRadioButton membershipType2 = new JRadioButton("Kertakäynti");
        leftPanel.add(membershipType);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(membershipType2);
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
        notesField.setMaximumSize(new Dimension(400,100));
        notesField.setLineWrap(true);
        rightPanel.add(new JLabel("Lisätiedot:"));
        rightPanel.add(notesField);
        rightPanel.add(Box.createVerticalGlue());

        JButton cancelButton = new JButton("Peruuta");
        rightPanel.add(cancelButton);
        rightPanel.add(Box.createVerticalGlue());
         ///////// STUFF IN THE RIGHT PANE /////////

        JPanel mainPanel = new JPanel(new GridLayout(1,2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        addUserFrame.add(mainPanel, BorderLayout.CENTER);
    
        addUserFrame.setSize(frameWidth, frameHeight);
        addUserFrame.setLocationRelativeTo(null);
        addUserFrame.setVisible(true);
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
         /*
         * 
         */

        if (enteredUsername.equals("testuser") && enteredPassword.equals("testpassword")) {
            // Close the current login frame
            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();
    
            // Open a new view
            JFrame newView = new JFrame("New View");
            newView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
            ImageIcon imageIcon = new ImageIcon("GymGate.png");
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
    
            JButton viewCustomersButton = new JButton("Selaa asiakkaita");
            viewCustomersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    
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
