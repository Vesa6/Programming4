package com.gymgate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Logger;

public class EventViewer {
    private static final Logger logger = DbgLogger.getLogger();
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable table;
    public static JFrame frame = null;

    public EventViewer() {
        createEventViewerGUI();
        logger.info("Created an instance of EventViewer");
    }

    private void createEventViewerGUI() {
        frame = new JFrame("Tapahtumat");
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
            logger.warning("Error on formating date for display: " + pe.getMessage());
        }
        SimpleDateFormat formatted = new SimpleDateFormat("dd.MM.yyyy");
        return formatted.format(datetemp);
    }

    private static String formatDateForSearch(String uDate, boolean end) throws ParseException {
        SimpleDateFormat toFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date datetemp = null;
        datetemp = toFormat.parse(uDate);
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        if (!end){
            return formatted.format(datetemp) + "T00:00:00";
        }
           return formatted.format(datetemp) + "T23:59:59";
    }

    private static void updateEventTableLabels(JTable table, String start, String end) {
        try {
            start = formatDateForSearch(start, false);
            end = formatDateForSearch(end, true);
            System.out.println(start + " " + end);
        } catch (ParseException e) {
            logger.warning("Error on parsing datetime for events: " + e.getMessage());
            falseResults(table, start, end);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {

            ResultSet resultSet = CustomerDatabase.getInstance().selectEventDate(start, end);
            if (resultSet == null) {
                falseResults(table, start, end);
            }
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
            logger.warning("Error retrieving customers from DB " + e.getMessage());
        }

    }

    private static void falseResults(JTable table, String s1, String s2) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String[] message = { "Ei hakutuloksia kyselyllä: " + s1 + " - " + s2,
                "Tarkista myös, että päivämäärät ovat muotoa dd.mm.YYYY" };

        for (int i = 0; i <= 1; i++) {
            Object[] rowData = {
                    "", "", message[i], "", 0
            };
            model.addRow(rowData);
        }
    }

    public static void defaultEventTableLabels(JTable table) {
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
            logger.warning("Error retrieving customers from DB " + e.getMessage());
        }

    }
}
