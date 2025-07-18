package g14javaapplication;

import javax.swing.*;
import java.awt.*;

public class EmployeeDetailsFrame extends JFrame {
    public EmployeeDetailsFrame(String[] employeeData) {
        setTitle("Employee Information");
        setSize(630, 400);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JPanel topBar = new JPanel(null);
        topBar.setBackground(new Color(0x0E3172)); // Dark Blue
        topBar.setBounds(0, 0, 630, 45);
        add(topBar);

        JLabel titleLabel = new JLabel("Employee Info");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBounds(20, 10, 200, 25);
        topBar.add(titleLabel);

        JButton backButton = new JButton("← Back");
        backButton.setBounds(530, 10, 80, 25);
        backButton.setBackground(new Color(46, 72, 113));
        backButton.setForeground(Color.WHITE);
        topBar.add(backButton);
        backButton.addActionListener(e -> dispose());

        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        infoPanel.setBounds(20, 60, 590, 280);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        infoPanel.setBackground(Color.WHITE);
        add(infoPanel);

        String[] labels = {
            "Employee ID", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #", "Status", "Position", "Immediate Supervisor"
        };

        for (int i = 0; i < 13; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            JLabel value = new JLabel(employeeData[i]);
            label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            infoPanel.add(label);
            infoPanel.add(value);
        }

        setVisible(true);
    }
}