package g14javaapplication;

import com.opencsv.CSVReader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class PayrollSelectionFrame extends JFrame {
    private String username;
    private String employeeID;
    private String role;
    private JComboBox<String> monthComboBox;
    private JTable table;
    private List<String[]> employeeDataList = new ArrayList<>();
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

    public PayrollSelectionFrame(String username, String role, String employeeID) {
        this.username = username;
        this.role = role;
        this.employeeID = employeeID; 
        setTitle("Payroll");
        setSize(630, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null); // 🔄 switch to null layout for precise positioning
        setResizable(false);

        // 🔷 Top Bar
        JPanel topBar = new JPanel(null);
        topBar.setBackground(new Color(10, 45, 90)); // navy blue
        topBar.setBounds(0, 0, 630, 45);
        add(topBar);
        

        // 🟨 Logo
        ImageIcon logoIcon = new ImageIcon("motoph_logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(20, 4, 179, 38);
        topBar.add(logoLabel);

        // 🗓️ Month Label
        JLabel monthLabel = new JLabel("Select Month");
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        monthLabel.setBounds(400, 0, 100, 12);
        topBar.add(monthLabel);

        // 🔽 Month Dropdown
        String[] months = {"-- Select Month --", "January", "February", "March",
                           "April", "May", "June", "July", "August", 
                           "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setBounds(400, 13, 86, 25);
        monthComboBox.setBackground(new Color(46, 72, 113));
        monthComboBox.setForeground(Color.WHITE);
        topBar.add(monthComboBox);

        // 🧾 Generate Payslip Button
        JButton genBtn = new JButton("Generate");
        genBtn.setBounds(510, 13, 100, 25);
        genBtn.setBackground(new Color(46, 72, 113));
        genBtn.setForeground(Color.WHITE);
        genBtn.setFocusPainted(false);
        topBar.add(genBtn);

        // ❌ Exit Button
        JButton exitButton = new JButton("⨉");
        exitButton.setBounds(580, 0, 30, 25);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setOpaque(false);
        topBar.add(exitButton);

        exitButton.addActionListener(e -> dispose());

     // 📋 Employee Table (Center Content)
     // Load employee data and build table based on role
     DefaultTableModel model = new DefaultTableModel(
         new String[]{"Employee ID", "Last Name", "First Name"}, 0);

     try (CSVReader reader = new CSVReader(new FileReader("employees.csv"))) {
    	    reader.readNext(); // skip header
    	    String[] row;
    	    while ((row = reader.readNext()) != null) {
    	        if ("admin".equalsIgnoreCase(role)) {
    	            employeeDataList.add(row);
    	            model.addRow(new Object[]{row[0], row[1], row[2]});
    	        } else if ("employee".equalsIgnoreCase(role) && row[0].equals(employeeID)) {
    	            employeeDataList.add(row);
    	            model.addRow(new Object[]{row[0], row[1], row[2]});
    	        }
    	    }
    	} catch (Exception e) {
    	    JOptionPane.showMessageDialog(this,
    	            "Error reading employees.csv:\n" + e.getMessage());
    	    dispose();
    	    return;
    	}

     table = new JTable(model);
     JScrollPane scrollPane = new JScrollPane(table);
     scrollPane.setBounds(20, 55, 590, 320);
     add(scrollPane);

        // ▶️ Action Listener for Generate
        genBtn.addActionListener(e -> onGenerate());

        setVisible(true);
    }

    private void onGenerate() {
        String month = (String) monthComboBox.getSelectedItem();
        if (monthComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Select a month first.");
            return;
        }

        String[] data;

        if ("admin".equalsIgnoreCase(role)) {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this, "Select an employee first.");
                return;
            }
            data = employeeDataList.get(sel);
        } else {
            // Employee should not need to click — just use first and only row
            if (employeeDataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee data not loaded.");
                return;
            }
            data = employeeDataList.get(0);
        }

        new PayrollPanel(data, month).setVisible(true);
    }
}