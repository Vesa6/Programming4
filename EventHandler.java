import javax.swing.*;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventHandler implements ActionListener {
    
    private JTextField usernameField;
    private JPasswordField passwordField;

    public EventHandler(JTextField usernameField, JPasswordField passwordField) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());

        if (enteredUsername.equals("testuser") && enteredPassword.equals("testpassword")) {
            // Close the current login frame
            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();

            // Open a new view
            JFrame newView = new JFrame("New View");
            newView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            newView.setSize(800, 600);
            newView.setLocationRelativeTo(null);
            newView.setVisible(true);
        }
    }
}
