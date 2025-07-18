package g14javaapplication;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class EditLogViewer extends JFrame {
    private JTable logTable;
    private DefaultTableModel logModel;
    private static final String[] columns = {"Timestamp", "Employee ID", "Field", "Old Value", "New Value"};
    private static final File logFile = new File("edit_log.csv");

    public EditLogViewer() {
        setTitle("Edit Log Viewer");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Colors
        Color darkBlue = Color.decode("#0E3172");
        Color lightBlue = Color.decode("#3F66B0");
        Color white = Color.decode("#F5F5F5");

        // Title bar background
        getContentPane().setBackground(white);

        logModel = new DefaultTableModel(columns, 0);
        logTable = new JTable(logModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing
            }
        };

        // Font and row colors
        logTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logTable.setRowHeight(24);
        logTable.setBackground(white);
        logTable.setForeground(Color.BLACK);

        // Header styling
        JTableHeader header = logTable.getTableHeader();
        header.setBackground(lightBlue);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(logTable);

        JButton clearButton = new JButton("Clear Logs");
        clearButton.setFocusPainted(false);
        clearButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        clearButton.setBackground(darkBlue);
        clearButton.setForeground(Color.WHITE);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(120, 35));
        clearButton.addActionListener(e -> clearLogs());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(white);
        bottomPanel.add(clearButton);

        // Load CSV logs
        try {
            loadLogs();
        } catch (CsvValidationException e) {
            JOptionPane.showMessageDialog(this, "Invalid CSV format: " + e.getMessage());
            e.printStackTrace();
        }

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadLogs() throws CsvValidationException {
        logModel.setRowCount(0);
        if (!logFile.exists()) return;

        try (CSVReader reader = new CSVReader(new FileReader(logFile))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length == 5) {
                    logModel.addRow(row);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading edit_log.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all logs?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(logFile))) {
                logModel.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Logs cleared successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error clearing logs: " + e.getMessage());
            }
        }
    }
}