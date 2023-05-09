package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class CustomerView {

    public static String confirmationHelper;
    private JTextField nameField;
    private JTable table;

    CustomerView() {

        createCustomerViewGUI();
    }

    private void createCustomerViewGUI() {

        JFrame frame = new JFrame("Customer Data");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (screenSize.width * 0.6);
        int frameHeight = (int) (screenSize.height * 0.8);
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);

        table = getCustomerTableLabels();

        JPanel searchPanel = new JPanel();
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(80, 20));
        JButton searchButton = new JButton("Hae");
        JButton resetButton = new JButton("Nollaa");
        ActionListener searchButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String name = nameField.getText();
                updateCustomerTableLabels(table, name);
            }

        };
        ActionListener resetButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                defaultCustomerTableLabels(table);
            }

        };
        searchButton.addActionListener(searchButtonListener);
        resetButton.addActionListener(resetButtonListener);

        searchPanel.add(new JLabel("Hae nimellä: "));
        searchPanel.add(nameField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        // This is a workaround to make the table uneditable without overriding the base
        // class.
        table.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private static JTable getCustomerTableLabels() {

        String[] columnNames = {
                "ID", "NIMI", " "
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        int defaultWidth = table.getColumnModel().getColumn(1).getWidth();
        table.getColumnModel().getColumn(0).setMaxWidth(defaultWidth / 2);
        table.getColumnModel().getColumn(2).setMaxWidth(defaultWidth);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                // This is the only column that has a popup menu
                if (column != 2) {
                    return;
                }

                String name = table.getValueAt(row, 1).toString();

                confirmationHelper = name;

                Object muokkaaColumn = table.getValueAt(row, 0);
                int customerId = Integer.parseInt(muokkaaColumn.toString());

                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem muokkaaAsiakasta = new JMenuItem("Muokkaa asiakasta");
                JMenuItem poistaAsiakas = new JMenuItem("Poista asiakas");
                popupMenu.add(muokkaaAsiakasta);
                popupMenu.add(poistaAsiakas);

                popupMenu.show(table, e.getX(), e.getY());

                muokkaaAsiakasta.addActionListener(muokkaa -> {

                    try {
                        openModifyCustomerView(CustomerDatabase.getInstance().getCustomerById(customerId));
                    } catch (SQLException exception) {
                        // TODO Auto-generated catch block
                        exception.printStackTrace();
                    }

                });

                poistaAsiakas.addActionListener(poista -> {

                    ConfirmationPopup confirmPop = new ConfirmationPopup(SwingUtilities.getRootPane(table));
                    confirmPop.showPopup();

                    confirmPop.getYesButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (CustomerDatabase.getInstance().deleteCustomer(customerId)) {

                                HidingPopup popup = new HidingPopup(SwingUtilities.getRootPane(table),
                                        "Asiakas poistettu", 2000, "checkMark.png");
                                popup.showPopup();
                                model.removeRow(row);
                                confirmPop.dispose();
                            } else {
                                
                            }
                        }
                    });

                    confirmPop.getNoButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            confirmPop.dispose();
                        }
                    });

                });
            }
        });

        defaultCustomerTableLabels(table);

        return table;

    }

    private static void openModifyCustomerView(ResultSet customer) throws SQLException {

        JFrame editFrame = new JFrame("Muokkaa asiakasta");
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel editLabel = new JLabel("Muokkaa asiakasta");
        editLabel.setHorizontalAlignment(JLabel.CENTER);
        editLabel.setVerticalAlignment(JLabel.TOP);

        // Add padding. For whatever reason, struts don't work here.
        editLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        editFrame.add(editLabel, BorderLayout.NORTH);

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
        String firstName = customer.getString("first_name");
        firstNameField.setText(firstName);
        leftPanel.add(new JLabel("Etunimi:"));
        leftPanel.add(firstNameField);
        leftPanel.add(Box.createVerticalGlue());

        JTextField addressField = new JTextField(20);
        addressField.setMaximumSize(textBoxDimension);
        String address = customer.getString("address");
        addressField.setText(address);
        leftPanel.add(new JLabel("Kotiosoite:"));
        leftPanel.add(addressField);
        leftPanel.add(Box.createVerticalGlue());

        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(textBoxDimension);
        String email = customer.getString("email");
        emailField.setText(email);
        leftPanel.add(new JLabel("Sähköposti:"));
        leftPanel.add(emailField);
        leftPanel.add(Box.createVerticalGlue());

        ButtonGroup membershipButtons = new ButtonGroup();
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

        membershipButtons.add(membershipType);
        membershipButtons.add(membershipType2);

        leftPanel.add(membershipType);
        leftPanel.add(months);
        leftPanel.add(Box.createVerticalGlue());

        JTextField membershipField = new JTextField(20);
        membershipField.setMaximumSize(textBoxDimension);
        String membershipDate = customer.getString("membership_end");
        membershipField.setText(membershipDate);

        if (customer.getString("membership_type").equals("Kuukausijäsenyys")) {
            membershipType2.setEnabled(false);
            membershipType.setSelected(true);

            leftPanel.add(new JLabel("Jäsenyys päättyy:"));
            leftPanel.add(membershipField);
            leftPanel.add(Box.createVerticalGlue());
            leftPanel.remove(membershipType);

        } else {
            membershipType2.setSelected(true);
            visits.setText(customer.getString("visits"));
            membershipType.setEnabled(false);
            visits.setVisible(true);
        }

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
        String lastName = customer.getString("last_name");
        lastNameField.setText(lastName);
        rightPanel.add(new JLabel("Sukunimi:"));
        rightPanel.add(lastNameField);
        rightPanel.add(Box.createVerticalGlue());

        JTextField phoneNumberField = new JTextField(20);
        phoneNumberField.setMaximumSize(textBoxDimensionRight);
        String phoneNumber = customer.getString("phone_number");
        phoneNumberField.setText(phoneNumber);
        rightPanel.add(new JLabel("Puhelinnumero:"));
        rightPanel.add(phoneNumberField);
        rightPanel.add(Box.createVerticalGlue());

        JTextArea notesField = new JTextArea(20, 20);
        notesField.setLineWrap(true);
        notesField.setMaximumSize(new Dimension(400, 100));
        notesField.setLineWrap(true);
        String notes = customer.getString("additional_information");
        notesField.setText(notes);
        rightPanel.add(new JLabel("Lisätiedot:"));
        rightPanel.add(notesField);
        rightPanel.add(Box.createVerticalGlue());

        JButton cancelButton = new JButton("Peruuta");
        rightPanel.add(cancelButton);
        rightPanel.add(Box.createVerticalGlue());

        cancelButton.addActionListener(e -> {
            editFrame.dispose();
        });
        ///////// STUFF IN THE RIGHT PANE /////////

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        editFrame.add(mainPanel, BorderLayout.CENTER);

        editFrame.setSize(frameWidth, frameHeight);
        editFrame.setLocationRelativeTo(null);
        editFrame.setVisible(true);

        /*
         * This action listener is for the save button.
         */
        ActionListener saveListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == saveButton) {

                    try {
                        String membershipType = customer.getString("membership_type");
                        if (membershipType.equals("Kuukausijäsenyys")) {
                            CustomerDatabase.getInstance().updateCustomer(customer.getInt("customer_id"),
                                    firstNameField.getText(), lastNameField.getText(), addressField.getText(),
                                    phoneNumberField.getText(), emailField.getText(), notesField.getText(),
                                    membershipField.getText(), 0);
                        } else if (customer.getString("membership_type").equals("Kertakäynti")) {
                            membershipType2.setEnabled(false);
                            CustomerDatabase.getInstance().updateCustomer(customer.getInt("customer_id"),
                                    firstNameField.getText(), lastNameField.getText(), addressField.getText(),
                                    phoneNumberField.getText(), emailField.getText(), notesField.getText(),
                                    membershipField.getText(), Integer.parseInt(visits.getText()));
                        }

                        HidingPopup popup = new HidingPopup(editFrame, "Muutos tallennettu", 2000, "checkMark.png");
                        popup.showPopup();

                    } catch (NumberFormatException e1) {

                        HidingPopup popup = new HidingPopup(editFrame, "Muutos epäonnistui, käyntikerrat voivat olla vain kokonaislukuja.", 2000);
                        popup.showPopup();

                    } catch (SQLException e1) {
                        
                        HidingPopup popup = new HidingPopup(editFrame, "Muutos epäonnistui, tietokantaongelma. Ottakaa yhteyttä tukeen.", 2000);
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

    private static void defaultCustomerTableLabels(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {

            ResultSet resultSet = CustomerDatabase.getInstance().getCustomers();

            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                Object[] rowData = {
                        customerId, firstName.concat(" " + lastName), " Muokkaa ..."
                };

                model.addRow(rowData);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers from DB " + e.getMessage());
        }

    }

    private static void updateCustomerTableLabels(JTable table, String name) {
        String temp[] = name.split(" ", 2); // splits into first name and last name
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {

            ResultSet resultSet = CustomerDatabase.getInstance().searchByName(temp[0], temp[1]);

            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                Object[] rowData = {
                        customerId, firstName.concat(" " + lastName), " Muokkaa ..."
                };

                model.addRow(rowData);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers from DB " + e.getMessage());
        }

    }
}
