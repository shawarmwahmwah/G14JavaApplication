package g14javaapplication;

import com.opencsv.CSVWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;

public class NewEmployeeForm extends JFrame {
    private JTextField[] fields = new JTextField[7];
    private String[] labels = {
        "Birthday", "Address", "Phone Number",
        "SSS #", "PhilHealth #", "TIN #", "Pag-ibig #"
    };

    private Color darkBlue = new Color(0x0E3172);

    public NewEmployeeForm(EmployeeTableFrame tableFrameRef) {
        setTitle("New Employee");
        setSize(600, 400); // Ideal size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        for (int i = 0; i < labels.length; i++) {
            // Label - left side
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.EAST;
            JLabel label = new JLabel("<html>" + labels[i] + " <font color='red'>*</font></html>");
            panel.add(label, gbc);

            // Text Field - right side
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.WEST;
            fields[i] = new JTextField(20);
            panel.add(fields[i], gbc);
        }

        // Add Employee Button - centered
        JButton addButton = new JButton("Add Employee");
        styleButton(addButton);

        addButton.addActionListener(e -> {
            if (saveToCSV()) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                if (tableFrameRef != null) {
                    tableFrameRef.refreshTable();
                }
                this.dispose();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        add(panel);
    }

    private void styleButton(JButton button) {
        button.setBackground(darkBlue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(160, 35));
    }

    private boolean saveToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter("employees.csv", true))) {
            String[] newRow = new String[labels.length];
            StringBuilder errorMessages = new StringBuilder();

            for (int i = 0; i < fields.length; i++) {
                newRow[i] = fields[i].getText().trim();

                if (newRow[i].isEmpty()) {
                    errorMessages.append("- ").append(labels[i]).append(" cannot be empty.\n");
                    continue;
                }

                switch (labels[i]) {
                    case "Birthday":
                        if (!newRow[i].matches("\\d{4}-\\d{2}-\\d{2}")) {
                            errorMessages.append("- Birthday must be in format: YYYY-MM-DD (e.g., 1999-12-31)\n");
                        }
                        break;

                    case "Phone Number":
                        if (!newRow[i].matches("\\+63\\s09\\d{2}\\s\\d{3}\\s\\d{4}")) {
                            errorMessages.append("- Phone Number must be in format: +63 09XX XXX XXXX (e.g., +63 0917 123 4567)\n");
                        }
                        break;

                    case "SSS #":
                        if (!newRow[i].matches("\\d{2}-\\d{7}-\\d")) {
                            errorMessages.append("- SSS # must be in format: ##-#######-# (e.g., 34-1234567-9)\n");
                        }
                        break;

                    case "PhilHealth #":
                        if (!newRow[i].matches("\\d{2}-\\d{9}-\\d")) {
                            errorMessages.append("- PhilHealth # must be in format: ##-#########-# (e.g., 12-123456789-0)\n");
                        }
                        break;

                    case "TIN #":
                        if (!newRow[i].matches("\\d{3}-\\d{3}-\\d{3}-\\d")) {
                            errorMessages.append("- TIN # must be in format: ###-###-###-# (e.g., 123-456-789-0)\n");
                        }
                        break;

                    case "Pag-ibig #":
                        if (!newRow[i].matches("\\d{4}-\\d{4}-\\d{4}")) {
                            errorMessages.append("- Pag-ibig # must be in format: ####-####-#### (e.g., 1234-5678-9012)\n");
                        }
                        break;
                }
            }

            if (errorMessages.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all the required fields (*) with the correct data format.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            writer.writeNext(newRow);
            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}