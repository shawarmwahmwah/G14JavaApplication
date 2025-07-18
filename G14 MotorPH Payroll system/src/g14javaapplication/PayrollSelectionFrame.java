package g14javaapplication;

import com.opencsv.CSVReader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PayrollSelectionFrame extends JFrame {
    private JComboBox<String> monthComboBox;
    private JTable table;
    private List<String[]> employeeDataList = new ArrayList<>();

    public PayrollSelectionFrame() {
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
        // Load employee data
        try (CSVReader reader = new CSVReader(new FileReader("employees.csv"))) {
            String[] header = reader.readNext(); // skip header
            String[] row;
            while ((row = reader.readNext()) != null) {
                employeeDataList.add(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading employees.csv:\n" + e.getMessage());
            dispose();
            return;
        }

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Employee ID", "Last Name", "First Name"}, 0);
        for (String[] d : employeeDataList) {
            model.addRow(new Object[]{d[0], d[1], d[2]});
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
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }
        String month = (String) monthComboBox.getSelectedItem();
        if (monthComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Select a month first.");
            return;
        }
        String[] data = employeeDataList.get(sel);
        new PayrollPanel(data, month).setVisible(true);
    }
}