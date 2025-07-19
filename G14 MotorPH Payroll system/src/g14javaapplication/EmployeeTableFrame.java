package g14javaapplication;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.opencsv.*;

public class EmployeeTableFrame extends JFrame {
	private final String employeeId;
	private final String role;
	private JTable employeeTable;
    private DefaultTableModel tableModel;
    private static final String EMPLOYEE_CSV_PATH = "employees.csv";

    public EmployeeTableFrame(String username, String role) {
    	this.employeeId = username;
    	this.role = role;
    	
        setTitle("All Employee Records");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu logMenu = new JMenu("Logs");

        JMenuItem viewEditLogMenuItem = new JMenuItem("View Edit Log");
        viewEditLogMenuItem.addActionListener(e -> new EditLogViewer().setVisible(true));

        JMenuItem viewDeletedLogMenuItem = new JMenuItem("View Deleted Log");
        viewDeletedLogMenuItem.addActionListener(e -> viewDeletedLog());

        logMenu.add(viewEditLogMenuItem);
        logMenu.add(viewDeletedLogMenuItem);
        menuBar.add(logMenu);
        setJMenuBar(menuBar);

        String[] columnNames = {
            "EmployeeID", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position", "Immediate Supervisor",
            "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        JButton viewButton = new JButton("View Employee");
        JButton editButton = new JButton("Edit Employee");
        JButton deleteButton = new JButton("Delete Employee");
        JButton restoreButton = new JButton("Restore Backup");

        if (!"admin".equalsIgnoreCase(role)) {
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            restoreButton.setEnabled(false);
        }

        viewButton.addActionListener(e -> viewSelectedEmployee());
        editButton.addActionListener(e -> editSelectedEmployee());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        restoreButton.addActionListener(e -> restoreFromBackup());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(restoreButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        
        Color darkBlue = new Color(0x0E3172);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 13);

        JButton[] buttons = {viewButton, editButton, deleteButton, restoreButton, searchButton, clearButton};

        for (JButton button : buttons) {
            button.setBackground(darkBlue);
            button.setForeground(Color.WHITE);
            button.setFont(buttonFont);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);

        if (role.equalsIgnoreCase("admin")) {
            loadEmployeeData(null); // admin sees all
        } else {
            loadEmployeeData(employeeId); // employee sees their own
        }

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term.");
                return;
            }

            String[] filterCols = new String[tableModel.getColumnCount()];
            for (int i = 0; i < filterCols.length; i++) {
                filterCols[i] = tableModel.getColumnName(i);
            }

            DefaultTableModel filteredModel = new DefaultTableModel(filterCols, 0);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String empID = tableModel.getValueAt(i, 0).toString().toLowerCase();
                String lastName = tableModel.getValueAt(i, 1).toString().toLowerCase();
                String firstName = tableModel.getValueAt(i, 2).toString().toLowerCase();

                if (empID.contains(keyword) || lastName.contains(keyword) || firstName.contains(keyword)) {
                    Object[] row = new Object[tableModel.getColumnCount()];
                    for (int j = 0; j < row.length; j++) {
                        row[j] = tableModel.getValueAt(i, j);
                    }
                    filteredModel.addRow(row);
                }
            }

            employeeTable.setModel(filteredModel);
        });

        clearButton.addActionListener(e -> {
            searchField.setText("");
            employeeTable.setModel(tableModel);
        });
    }

    private void loadEmployeeData(String filterEmployeeId) {
        String filePath = EMPLOYEE_CSV_PATH;
        File csvFile = new File(filePath);

        if (!csvFile.exists()) {
            JOptionPane.showMessageDialog(this, "CSV file not found at:\n" + filePath);
            return;
            
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            tableModel.setRowCount(0);
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replace("\"", "").trim();
                }

                if (filterEmployeeId == null || filterEmployeeId.isEmpty() || data[0].equals(filterEmployeeId)) {
                    Object[] row = new Object[tableModel.getColumnCount()];
                    for (int i = 0; i < row.length; i++) {
                        row[i] = i < data.length ? data[i] : "";
                    }
                    tableModel.addRow(row);
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No employee records found.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSelectedEmployee() {
        int[] selectedRows = employeeTable.getSelectedRows();
        if (selectedRows.length == 0) {
        	showCustomMessageDialog(this, "Please select at least one employee to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            Files.copy(Paths.get(EMPLOYEE_CSV_PATH), Paths.get("employees_backup_" + timestamp + ".csv"), StandardCopyOption.REPLACE_EXISTING);

            File originalFile = new File(EMPLOYEE_CSV_PATH);
            File tempFile = new File("employees_temp.csv");

            CSVReader reader = new CSVReader(new FileReader(originalFile));
            CSVWriter writer = new CSVWriter(new FileWriter(tempFile));
            BufferedWriter logWriter = new BufferedWriter(new FileWriter("deleted_log.txt", true));

            String[] nextLine;
            Set<String> selectedIds = new HashSet<>();
            for (int row : selectedRows) {
                selectedIds.add(tableModel.getValueAt(row, 0).toString());
            }

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 19 || nextLine[0].equalsIgnoreCase("EmployeeID")) {
                    writer.writeNext(nextLine);
                    continue;
                }

                if (selectedIds.contains(nextLine[0])) {
                    logWriter.write("Deleted: " + Arrays.toString(nextLine) + " at " + new Date() + "\n");
                } else {
                    writer.writeNext(nextLine);
                }
            }

            reader.close();
            writer.close();
            logWriter.close();

            if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
                JOptionPane.showMessageDialog(this, "Failed to replace original file.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Deleted successfully.");
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void viewSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
        	JLabel messageLabel = new JLabel("Please select an employee from the table.");
        	messageLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        	messageLabel.setForeground(Color.WHITE);  // Font color

        	JPanel panel = new JPanel();
        	panel.setBackground(new Color(0x0E3172));  // Dark blue background
        	panel.add(messageLabel);

        	JOptionPane.showMessageDialog(this, panel, "Notice", JOptionPane.INFORMATION_MESSAGE);
        	return;
        }

        String[] employeeData = new String[tableModel.getColumnCount()];
        for (int i = 0; i < employeeData.length; i++) {
            employeeData[i] = tableModel.getValueAt(selectedRow, i).toString();
        }

        new EmployeeDetailsFrame(employeeData).setVisible(true);
    }

    private void editSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee from the table.");
            return;
        }

        String[] employeeData = new String[tableModel.getColumnCount()];
        for (int i = 0; i < employeeData.length; i++) {
            employeeData[i] = tableModel.getValueAt(selectedRow, i).toString();
        }

        if ("admin".equalsIgnoreCase(role) || employeeData[0].equals(employeeId)) {
            new EditEmployeeForm(employeeData, this).setVisible(true);
        } else {
            showCustomMessageDialog(this, "You are not allowed to edit other employee's information.");
        }
    }

    private void restoreFromBackup() {
        File dir = new File(".");
        File[] backupFiles = dir.listFiles((d, name) -> name.startsWith("employees_backup_") && name.endsWith(".csv"));

        if (backupFiles == null || backupFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No backup files found.");
            return;
        }

        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());
        File latestBackup = backupFiles[0];

        int confirm = JOptionPane.showConfirmDialog(this, "Restore from: " + latestBackup.getName(), "Confirm Restore", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Files.copy(latestBackup.toPath(), new File(EMPLOYEE_CSV_PATH).toPath(), StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(this, "Restore successful.");
            refreshTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Restore failed: " + e.getMessage());
        }
    }
    
    private void viewDeletedLog() {
        File logFile = new File("deleted_log.txt");
        if (!logFile.exists()) {
            JOptionPane.showMessageDialog(this, "No deleted log found.");
            return;
        }

        try {
            // Create frame
            JFrame logFrame = new JFrame("Deleted Employee Log");
            logFrame.setSize(600, 300);
            logFrame.setLocationRelativeTo(null);
            logFrame.setResizable(false);
            logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Colors and fonts
            Color darkBlue = Color.decode("#0E3172");
            Color lightBlue = Color.decode("#3F66B0");
            Color white = Color.decode("#F5F5F5");
            Font systemFont = UIManager.getFont("Label.font");

            // Create text area and read file
            JTextArea textArea = new JTextArea();
            textArea.read(new FileReader(logFile), null);
            textArea.setEditable(false);
            textArea.setFont(systemFont);
            textArea.setBackground(white);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(white);

            // Title panel
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(darkBlue);
            JLabel titleLabel = new JLabel("Deleted Employee Log");
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(systemFont.deriveFont(Font.BOLD, 16));
            titlePanel.add(titleLabel);

            // Add everything to frame
            logFrame.add(titlePanel, BorderLayout.NORTH);
            logFrame.add(scrollPane, BorderLayout.CENTER);

            logFrame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to read deleted log: " + e.getMessage());
        }
    }
    
    private void showCustomMessageDialog(Component parent, String message) {
        Color darkBlue = Color.decode("#0E3172");
        Color white = Color.decode("#F5F5F5");

        UIManager.put("OptionPane.background", darkBlue);
        UIManager.put("Panel.background", darkBlue);
        UIManager.put("OptionPane.messageForeground", white);
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.BOLD, 14));

        JOptionPane.showMessageDialog(parent, message);

        // Reset to defaults after showing
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("OptionPane.messageFont", null);
    }

    public void refreshTable() {
        int selectedRow = employeeTable.getSelectedRow();
        String selectedEmployeeId = selectedRow != -1 ? tableModel.getValueAt(selectedRow, 0).toString() : null;

        tableModel.setRowCount(0);
        loadEmployeeData(null);

        if (selectedEmployeeId != null) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(selectedEmployeeId)) {
                    employeeTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }
}