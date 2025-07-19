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
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Employee Attendance");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleBar.setBackground(new Color(0x3F66B0));
        titleBar.setPreferredSize(new Dimension(800, 45));
        titleBar.add(titleLabel);

        JLabel searchLabel = new JLabel("Search by Name or ID:");
        searchField = new JTextField();
        JButton backButton = new JButton("← Back");

        topPanel.add(searchLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.EAST);

        add(titleBar, BorderLayout.NORTH);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table and scroll
        String[] columnNames = { "Employee ID", "Last Name", "First Name", "Date", "Log In", "Log Out" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setGridColor(new Color(220, 220, 220));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        String[] buttonLabels = { "View", "Delete", "Edit", "Save" };
        for (String text : buttonLabels) {
            JButton button = new JButton(text);
            button.setPreferredSize(new Dimension(100, 35));
            button.setBackground(new Color(0x3F66B0));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.BOLD, 13));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // Load CSV data
        loadAttendanceData();

        // Search filter
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterTable(query);
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            dispose();
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
            boolean isAllowed = "admin".equalsIgnoreCase(loggedInUserRole) || row[0].equalsIgnoreCase(employeeID);

            if (matches && isAllowed) {
                tableModel.addRow(row);
            }
        }
    }
}