package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CustomerView {


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

        JTable table = getCustomerTableLabels();

        //This is a workaround to make the table uneditable without overriding the base class.
        table.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(table);
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
        table.getColumnModel().getColumn(0).setMaxWidth(defaultWidth / 4);
        table.getColumnModel().getColumn(2).setMaxWidth(defaultWidth);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
    
                //This is the only column that has a popup menu
                if (column != 2) {
                    return;
                }
    
                //This makes the magic only happen with double click
                //Remove this if it feels unintuitive
                if (e.getClickCount() == 2) {
                    Object MuokkaaColumn = table.getValueAt(row, 0);
                    int customerId = Integer.parseInt(MuokkaaColumn.toString());
    
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem muokkaaAsiakasta = new JMenuItem("Muokkaa asiakasta");
                    JMenuItem poistaAsiakas = new JMenuItem("Poista asiakas");
                    popupMenu.add(muokkaaAsiakasta);
                    popupMenu.add(poistaAsiakas);
    
                    

                    muokkaaAsiakasta.addActionListener(muokkaa -> {
                        System.out.println("Muokkaa asiakasta: " + customerId);
                        
                    });
    
                    poistaAsiakas.addActionListener(poista -> {
                        System.out.println("Poista asiakas: " + customerId);
                        
                    });
    
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        });

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

        return table;
    }
}
