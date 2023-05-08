package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class EventViewer {
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable table;

    public EventViewer() {
        createEventViewerGUI();
    }

    private void createEventViewerGUI() {

        JFrame frame = new JFrame("Tapahtumat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (screenSize.width * 0.6);
        int frameHeight = (int) (screenSize.height * 0.6);
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);



        table = getEventTableLabels();

        JPanel searchPanel = new JPanel();
        startDateField = new JTextField();
        startDateField.setPreferredSize(new Dimension(60, 20));
        endDateField = new JTextField();
        endDateField.setPreferredSize(new Dimension(60, 20));
        JButton searchButton = new JButton("Hae");
        JButton resetButton = new JButton("Nollaa");
        ActionListener searchButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String startDate = startDateField.getText();
                String endDate = endDateField.getText();
                updateEventTableLabels(table, startDate, endDate);
            }

        };
        ActionListener resetButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                defaultEventTableLabels(table);
            }

        };
        searchButton.addActionListener(searchButtonListener);
        resetButton.addActionListener(resetButtonListener);


        searchPanel.add(new JLabel("Aloitus"));
        searchPanel.add(startDateField);
        searchPanel.add(new JLabel("Lopetus"));
        searchPanel.add(endDateField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        table.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    private static JTable getEventTableLabels() {
        String[] columnNames = {
                "Päivämäärä", "Kellonaika", "Nimi", "Asiakasnro"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        int defaultWidth = table.getColumnModel().getColumn(2).getWidth();
        table.getColumnModel().getColumn(0).setMaxWidth(defaultWidth);
        table.getColumnModel().getColumn(1).setMaxWidth(defaultWidth);
        table.getColumnModel().getColumn(3).setMaxWidth(defaultWidth);
        defaultEventTableLabels(table);

        return table;
    }



    private static String formatDateToDisplay(String uDate) {
        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date datetemp = null;
        try {
            datetemp = toFormat.parse(uDate);
        } catch (ParseException pe) {
            System.out.println("Error on parsing datetime for events " + pe.getMessage());
        }
        SimpleDateFormat formatted = new SimpleDateFormat("dd.MM.yyyy");
        return formatted.format(datetemp);
    }

    private static String formatDateForSearch(String uDate) {
        SimpleDateFormat toFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date datetemp = null;
        try {
            datetemp = toFormat.parse(uDate);
        } catch (ParseException pe) {
            System.out.println("Error on parsing datetime for events " + pe.getMessage());
        }
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        String temp = formatted.format(datetemp) + "T00:00:00";
        return temp;
    }

    private static void updateEventTableLabels(JTable table, String start, String end) {
        start = formatDateForSearch(start);
        end = formatDateForSearch(end);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {

            ResultSet resultSet = CustomerDatabase.getInstance().selectEventDate(start, end);

            while (resultSet.next()) {
                String temp = resultSet.getString("date");
                String name = resultSet.getString("name");
                int customerId = resultSet.getInt("customer_id");

                String[] dateTime = temp.split("T");
                String date = formatDateToDisplay(dateTime[0]);

                Object[] rowData = {
                        date, dateTime[1], name, customerId
                };

                model.addRow(rowData);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers from DB " + e.getMessage());
        }

    }

    private static void defaultEventTableLabels(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {

            ResultSet resultSet = CustomerDatabase.getInstance().getEvents();

            while (resultSet.next()) {
                String temp = resultSet.getString("date");
                String name = resultSet.getString("name");
                int customerId = resultSet.getInt("customer_id");

                String[] dateTime = temp.split("T");
                String date = formatDateToDisplay(dateTime[0]);

                Object[] rowData = {
                        date, dateTime[1], name, customerId
                };

                model.addRow(rowData);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers from DB " + e.getMessage());
        }

    }
}
