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
    private String loggedInUsername;
    private String loggedInUserRole;
    private String loggedInEmployeeID;
    private String getEmployeeIDFromUsername(String username) {
        try (CSVReader reader = new CSVReader(new FileReader("employees.csv"))) {
            String[] nextLine;
            reader.readNext(); // skip header
            while ((nextLine = reader.readNext()) != null) {
                String fileUsername = nextLine[3]; // assuming Username is column 4 (index 3)
                if (fileUsername.equalsIgnoreCase(username)) {
                    return nextLine[0]; // Return Employee ID
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error finding employee ID: " + e.getMessage());
        }
        return null;
    }

    public AttendanceFrame(String username, String role, String employeeID) {
        this.loggedInUsername = username;
        this.loggedInUserRole = role;
        this.loggedInEmployeeID = employeeID;
        
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

        // Load data from CSV for specific employee only
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
                if ("admin".equalsIgnoreCase(loggedInUserRole)) {
                    allData.add(nextLine);
                    tableModel.addRow(nextLine);
                } else {
                    if (loggedInEmployeeID != null && nextLine[0].equals(loggedInEmployeeID)) {
                        allData.add(nextLine);
                        tableModel.addRow(nextLine);
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV: " + e.getMessage());
        }
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0); // Clear table

        String employeeID = loggedInEmployeeID;

        for (String[] row : allData) {
            String empId = row[0].toLowerCase();
            String fullName = (row[1] + " " + row[2]).toLowerCase();

            boolean matches = empId.contains(query) || fullName.contains(query);

            // Only allow admin or employee's own records to show on filter
            boolean isAllowed = "admin".equalsIgnoreCase(loggedInUserRole) || row[0].equalsIgnoreCase(employeeID);

            if (matches && isAllowed) {
                tableModel.addRow(row);
            }
        }
    }
}
