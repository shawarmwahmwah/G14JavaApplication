package g14javaapplication;

import com.opencsv.CSVReader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AttendanceFrame extends JFrame {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<String[]> allData = new ArrayList<>();

    public AttendanceFrame() {
        setTitle("Employee Attendance");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Search by Name or ID:");
        searchField = new JTextField();
        JButton backButton = new JButton("← Back");

        topPanel.add(searchLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Correct column headers
        String[] columnNames = { "Employee ID", "Last Name", "First Name", "Date", "Log In", "Log Out" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load data from CSV
        loadAttendanceData();

        // Search functionality
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterTable(query);
            }
        });

        // Back button
        backButton.addActionListener(e -> {
            dispose(); // close frame
        });
    }

    private void loadAttendanceData() {
        try (CSVReader reader = new CSVReader(new FileReader("employee_attendance.csv"))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                allData.add(nextLine); // Save for filtering
                tableModel.addRow(nextLine);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV: " + e.getMessage());
        }
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0); // Clear table
        for (String[] row : allData) {
            String empId = row[0].toLowerCase();
            String fullName = (row[1] + " " + row[2]).toLowerCase();
            if (empId.contains(query) || fullName.contains(query)) {
                tableModel.addRow(row);
            }
        }
    }
}
