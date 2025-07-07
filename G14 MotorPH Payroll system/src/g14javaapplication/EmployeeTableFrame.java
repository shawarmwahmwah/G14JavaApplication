package g14javaapplication;

import com.opencsv.CSVReader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;

public class EmployeeTableFrame extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public EmployeeTableFrame(String filterEmployeeId) {
        setTitle("All Employee Records");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {
            "EmployeeID", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position", "Immediate Supervisor",
            "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        JButton viewButton = new JButton("View Employee");
        viewButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        viewButton.addActionListener(e -> viewSelectedEmployee());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEmployeeData(filterEmployeeId);
    }

    private void loadEmployeeData(String filterEmployeeId) {
        try {
            CSVReader reader = new CSVReader(new FileReader("C:/Users/Niko Giron/eclipse-workspace/G14 MotorPH Payroll system/employees.csv"));
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (filterEmployeeId == null || filterEmployeeId.isEmpty() || line[0].equals(filterEmployeeId)) {
                    tableModel.addRow(line);
                }
            }

            reader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading employees.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee from the table.");
            return;
        }

        // Extract selected row data as String[]
        String[] employeeData = new String[tableModel.getColumnCount()];
        for (int i = 0; i < employeeData.length; i++) {
            employeeData[i] = tableModel.getValueAt(selectedRow, i).toString();
        }

        // Open EmployeeDetailsFrame
        new EmployeeDetailsFrame(employeeData).setVisible(true);
    }
}