import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventHandler implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public EventHandler(JTextField usernameField, JPasswordField passwordField) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
    }

    private void createPostLoginGUI(ActionEvent e, String enteredUsername, String enteredPassword) {

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
            label.setText("Welcome to GymGate!");
            label.setIcon(imageIcon);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
    
            Font arialFont = new Font("Arial", Font.PLAIN, 24);
            label.setFont(arialFont);
    
            JButton addMemberButton = new JButton("Add member");
            addMemberButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    
            JButton viewLogsButton = new JButton("View logs");
            viewLogsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    
            JButton viewCustomersButton = new JButton("View customers");
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
