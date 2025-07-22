package g14javaapplication;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class PayrollPanel extends JFrame {
	private JEditorPane payslipArea;

	public PayrollPanel(String[] data, String month) {
		setTitle("Payroll Payslip");
		setSize(480, 650);
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
			try {
				payslipArea.print();
			} catch (Exception ex) {
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

		Payroll payroll = new Payroll();

		// STEP 1: Compute dynamic base salary from attendance
		double hourlyRate = parseNumeric(d[18]);
		double hoursWorked = getActualHoursWorked(d[0]); // Employee ID

		if (hourlyRate > 0 && hoursWorked > 0) {
			payroll.computeBaseSalaryFromHoursWorked(hoursWorked, hourlyRate);
		}

		double rice = parseNumeric(d[14]);
		double phone = parseNumeric(d[15]);
		double clothing = parseNumeric(d[16]);
		double totalAllowance = rice + phone + clothing;
		payroll.setAllowance(totalAllowance);

		Bonuses bonuses = new Bonuses();
		bonuses.setThirteenMonthPay(1000);
		bonuses.setIncentiveBonus(500);
		bonuses.setLoyaltyBonus(300);
		bonuses.setPerformanceBonus(700);
		payroll.setBonuses(bonuses);

		double gross = payroll.calculateGrossPay();

		Deduction deductions = new Deduction();
		deductions.computeAllDeductions(gross);
		payroll.setDeductions(deductions);

		double net = payroll.calculateNetPay();

		StringBuilder sb = new StringBuilder();
		sb.append("<html><body style='font-family:monospace; text-align:center;'>");
		sb.append("<h3 style='text-align:center;'>================ PAYSLIP ================</h3>");
		sb.append(String.format("<p><strong>Employee:</strong> %s, %s<br>", d[1], d[2]));
		sb.append(String.format("<strong>ID:</strong> %s<br>", d[0]));
		sb.append(String.format("<strong>Position:</strong> %s<br>", d[11]));
		sb.append(String.format("<strong>Period:</strong> %s</p>", month));
		sb.append("<hr>");

		sb.append(String.format("<p style='color:green;'>Basic Salary       : ₱ %s<br>",
				df.format(payroll.getBaseSalary())));
		sb.append(String.format("Allowances Total    : ₱ %s</p>", df.format(totalAllowance)));

		sb.append("<p style='color:blue;'>");
		sb.append(String.format("13th Month Bonus    : ₱ %s<br>", df.format(bonuses.getThirteenMonthPay())));
		sb.append(String.format("Incentive Bonus     : ₱ %s<br>", df.format(bonuses.getIncentiveBonus())));
		sb.append(String.format("Loyalty Bonus       : ₱ %s<br>", df.format(bonuses.getLoyaltyBonus())));
		sb.append(String.format("Performance Bonus   : ₱ %s</p>", df.format(bonuses.getPerformanceBonus())));

		sb.append("<p style='color:red;'>");
		sb.append(String.format("SSS Contribution    : ₱ %s<br>", df.format(deductions.getSSS())));
		sb.append(String.format("PhilHealth          : ₱ %s<br>", df.format(deductions.getPhilHealth())));
		sb.append(String.format("Pag-IBIG            : ₱ %s<br>", df.format(deductions.getPagIbig())));
		sb.append(String.format("Withholding Tax     : ₱ %s</p>", df.format(deductions.getTax())));

		sb.append("<hr>");
		sb.append(String.format("<p><strong>Gross Pay</strong> : ₱ <span style='color:green;'>%s</span><br>",
				df.format(gross)));
		sb.append(String.format("<strong>Total Deductions</strong> : ₱ <span style='color:red;'>%s</span></p>",
				df.format(deductions.getTotalDeductions())));

		sb.append("<hr>");
		sb.append(String.format(
				"<p><strong><span style='color:black; font-weight:bold;'>NET PAY</span> : ₱ <span style='font-weight:bold;'>%s</span></strong></p>",
				df.format(net)));
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

	private double getActualHoursWorked(String employeeId) {
		double totalHours = 0.0;
		try (BufferedReader br = new BufferedReader(new FileReader("employee_attendance.csv"))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts[0].equals(employeeId)) {
					String logIn = parts[4].trim();
					String logOut = parts[5].trim();

					SimpleDateFormat format = new SimpleDateFormat("HH:mm");
					Date in = format.parse(logIn);
					Date out = format.parse(logOut);

					long diff = out.getTime() - in.getTime();
					double hours = diff / (1000.0 * 60.0 * 60.0);
					totalHours += hours;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalHours;
	}
}
