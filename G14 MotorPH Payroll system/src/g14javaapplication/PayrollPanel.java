package g14javaapplication;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class PayrollPanel extends JFrame {
    private JEditorPane payslipArea;

    public PayrollPanel(String[] data, String month) {
        setTitle("Payroll Payslip");
        setSize(480, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JLabel header = new JLabel("PAYSLIP for " + month, SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        payslipArea = new JEditorPane();
        payslipArea.setContentType("text/html");
        payslipArea.setEditable(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(payslipArea), BorderLayout.CENTER);

        JButton printBtn = new JButton("Print / Save as PDF");
        printBtn.addActionListener(e -> {
            try { payslipArea.print(); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print error");
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(printBtn);
        add(bottom, BorderLayout.SOUTH);

        generatePayslip(data, month);
    }

    private void generatePayslip(String[] d, String month) {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        double basic     = parseNumeric(d[13]);
        double rice      = parseNumeric(d[14]);
        double phone     = parseNumeric(d[15]);
        double clothing  = parseNumeric(d[16]);

        // Sample deductions
        double sss       = parseNumeric("500");
        double philhealth= parseNumeric("300");
        double pagibig   = parseNumeric("200");

        double gross     = basic + rice + phone + clothing;
        double totalDeduction = sss + philhealth + pagibig;
        double net       = gross - totalDeduction;

        StringBuilder sb = new StringBuilder();

        sb.append("<html><body style='font-family:monospace; text-align:center;'>");
        sb.append("<h3 style='text-align:center;'>================ PAYSLIP ================</h3>");
        sb.append(String.format("<p><strong>Employee:</strong> %s, %s<br>", d[1], d[2]));
        sb.append(String.format("<strong>ID:</strong> %s<br>", d[0]));
        sb.append(String.format("<strong>Position:</strong> %s<br>", d[11]));
        sb.append(String.format("<strong>Period:</strong> %s</p>", month));
        sb.append("<hr>");

        // Incomes
        sb.append(String.format("<p style='color:green;'>Basic Salary       : ₱ %s<br>", df.format(basic)));
        sb.append(String.format("Rice Subsidy       : ₱ %s<br>", df.format(rice)));
        sb.append(String.format("Phone Allowance    : ₱ %s<br>", df.format(phone)));
        sb.append(String.format("Clothing Allowance : ₱ %s</p>", df.format(clothing)));

        // Deductions
        sb.append("<p style='color:red;'>");
        sb.append(String.format("SSS Deduction      : ₱ %s<br>", df.format(sss)));
        sb.append(String.format("PhilHealth         : ₱ %s<br>", df.format(philhealth)));
        sb.append(String.format("Pag-IBIG           : ₱ %s</p>", df.format(pagibig)));

        sb.append("<hr>");
        sb.append(String.format("<p><strong>Gross Pay</strong> : ₱ <span style='color:green;'>%s</span><br>", df.format(gross)));
        sb.append(String.format("<strong>Total Deductions</strong> : ₱ <span style='color:red;'>%s</span></p>", df.format(totalDeduction)));

        sb.append("<hr>");
        sb.append(String.format("<p><strong><span style='color:black; font-weight:bold;'>NET PAY</span> : ₱ <span style='font-weight:bold;'>%s</span></strong></p>", df.format(net)));
        sb.append("<hr>");
        sb.append("</body></html>");

        payslipArea.setText(sb.toString());
    }

    private double parseNumeric(String input) {
        try {
            return Double.parseDouble(input.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}