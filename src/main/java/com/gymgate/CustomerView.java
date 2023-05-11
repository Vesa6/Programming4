package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.IllegalFormatFlagsException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Logger;

public class CustomerView {
    private static final Logger logger = DbgLogger.getLogger();

    public static String confirmationHelper;
    private JTextField nameField;
    private JTable table;

    CustomerView() {
        /*
         * Shows list of customers with a possibility to edit or remove them
         */
        logger.info("Created a new instance of CustomerView");
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
                    } catch (SQLException sqe) {
                        logger.warning("Unable to modify customers: " + sqe);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
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
                                        "Asiakas poistettu", 2000, "Icons/checkMark.png");
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

    private static void openModifyCustomerView(ResultSet customer) throws SQLException, ParseException {

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

        JLabel text = new JLabel("Päivämäärä (vvvv-kk-pp):");

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
                    text.setVisible(true);
                } else if (e.getSource() == membershipType2) {
                    months.setVisible(false);
                    visits.setVisible(true);
                    text.setVisible(false);
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
        leftPanel.add(text);
        leftPanel.add(months);
        leftPanel.add(Box.createVerticalGlue());

        JTextField membershipField = new JTextField(20);
        membershipField.setMaximumSize(textBoxDimension);
        String membershipDate = customer.getString("membership_end");        

        if (customer.getString("membership_type").equals("Kuukausijäsenyys")) {
            membershipType.setSelected(true);
            text.setVisible(true);
            months.setText(membershipDate);
            months.setVisible(true);
            visits.setVisible(false);
        } else {
            membershipType2.setSelected(true);
            text.setVisible(false);
            visits.setText(customer.getString("visits"));
            visits.setVisible(true);
            months.setVisible(false);
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
    
                // Check phone number. Only the first character can be '+', all the rest need to
                // be digits
                int u = 0;
                for (char num : phoneNumberField.getText().toCharArray()) {
                    if (num == '+' && u == 0) {
                        u++;
                        continue;
                    }
                    if (!Character.isDigit(num)) {
                        HidingPopup popup = new HidingPopup(editFrame,
                                "Muutos epäonnistui, puhelinnumero on virheellisessä muodossa.", 2000);
                        popup.showPopup();
                        logger.info("Modifying user failed, phone number is not in correct format.");
                        return;
                    }
                }
    
                if ((e.getSource() == saveButton) && (membershipType.isSelected())) {
    
                    DateTimeFormatter sqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate currentDate = LocalDate.now();
                    String currentDateString = currentDate.format(sqlDateFormat);
                    String endDateString = months.getText();
    
                    String memberTypeSelected = "Kuukausijäsenyys";
    
                    if ((firstNameField.getText().isEmpty()) || (lastNameField.getText().isEmpty()) || (phoneNumberField.getText().isEmpty()) || (emailField.getText().isEmpty()) ||
                    months.getText().isEmpty() || (addressField.getText().isEmpty())) {
                        HidingPopup popup = new HidingPopup(editFrame,
                                "Muutos epäonnistui, kaikki kentät on täytettävä.", 2000);
                        popup.showPopup();
                        logger.info("Modifying user failed, all fields are not filled.");
                        return;
                    }
    
                    try {
                        CustomerDatabase.getInstance().updateCustomer(firstNameField.getText(), lastNameField.getText(),
                                addressField.getText(), phoneNumberField.getText(), emailField.getText(),
                                notesField.getText(), endDateString, 0, currentDateString, memberTypeSelected, customer.getInt("customer_id"));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
    
                    HidingPopup popup = new HidingPopup(editFrame,
                            "Muutos tallennettu.", 2000, "Icons/checkMark.png");
                    popup.showPopup();
                    editFrame.dispose();
    
                } else if ((e.getSource() == saveButton) && (membershipType2.isSelected())) {
    
                    String memberTypeSelected = "Kertakäynti";
    
                    if ((firstNameField.getText().isEmpty()) || (lastNameField.getText().isEmpty()) || (phoneNumberField.getText().isEmpty()) || (emailField.getText().isEmpty()) ||
                    visits.getText().isEmpty() || (addressField.getText().isEmpty())) {
                        HidingPopup popup = new HidingPopup(editFrame,
                                "Muutos epäonnistui, kaikki kentät on täytettävä.", 2000);
                        popup.showPopup();
                        logger.info("Modifying user failed, all fields are not filled.");
                        return;
                    }
    
                    try {
                        CustomerDatabase.getInstance().updateCustomer(firstNameField.getText(),
                                lastNameField.getText(), addressField.getText(),
                                phoneNumberField.getText(), emailField.getText(), notesField.getText(),
                                "NULL", Integer.valueOf(visits.getText()),
                                "NULL", memberTypeSelected, customer.getInt("customer_id"));
    
                        HidingPopup popup = new HidingPopup(editFrame,
                                "Muutos talennettu.", 2000, "Icons/checkMark.png");
                        popup.showPopup();
                        editFrame.dispose();
    
                    }
    
                    catch (NumberFormatException nE) 

                    {
                    HidingPopup popup = new HidingPopup(editFrame,
                                "Muutos epäonnistui, käyntikerrat voivat olla vain kokonaislukuja.", 2000);
                                logger.info("Modifying user failed, visits can only be integers.");
                        popup.showPopup();
                    } catch (SQLException e1) {
                            
                            e1.printStackTrace();
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
            logger.warning("Error retrieving customers from DB " + e.getMessage());
        }

    }

    private static void updateCustomerTableLabels(JTable table, String name) {
        String temp[] = name.split(" ", 2); // splits into first name and last name
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String fName = "";
        String lName = "";
        try {
            fName = temp[0].substring(0, 1).toUpperCase() + temp[0].substring(1).toLowerCase();
            lName = temp[1].substring(0, 1).toUpperCase() + temp[1].substring(1).toLowerCase();
        } catch (ArrayIndexOutOfBoundsException ae) {
            
        }
        try {

            ResultSet resultSet = CustomerDatabase.getInstance().searchByName(fName, lName);

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
            logger.warning("Error retrieving customers from DB " + e.getMessage());
        }

    }
}
