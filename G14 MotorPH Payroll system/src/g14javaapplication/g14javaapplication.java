package g14javaapplication;

import com.opencsv.CSVReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;

public class g14javaapplication {
	private String loggedInUserRole = "";
	private String loggedInUsername = "";
	private String loggedInEmployeeID = "";
	
	private JFrame frame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new g14javaapplication());
	}

	public g14javaapplication() {
		frame = new JFrame("MOTORPH PAYROLL SYSTEM");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(450, 330);
		frame.setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(null); // Absolute positioning
		mainPanel.setBackground(new Color(0xFF6B6B)); // Pastel red

		// Title Label
		JLabel titleLabel = new JLabel("MOTORPH");
		titleLabel.setFont(new Font("Montserrat", Font.BOLD, 34));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBounds(145, 20, 200, 39);
		mainPanel.add(titleLabel);

		// Username Label
		JLabel userLabel = new JLabel("Username:");
		userLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
		userLabel.setForeground(Color.WHITE);
		userLabel.setBounds(30, 70, 100, 17);
		mainPanel.add(userLabel);

		// Username Field
		JTextField userField = new JTextField();
		userField.setFont(new Font("Montserrat", Font.PLAIN, 14));
		userField.setBounds(30, 90, 390, 38);
		userField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		mainPanel.add(userField);

		// Password Label
		JLabel passLabel = new JLabel("Password:");
		passLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
		passLabel.setForeground(Color.WHITE);
		passLabel.setBounds(30, 140, 100, 17);
		mainPanel.add(passLabel);

		// Password Field
		JPasswordField passField = new JPasswordField();
		passField.setFont(new Font("Montserrat", Font.PLAIN, 14));
		passField.setBounds(30, 160, 390, 38);
		passField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		mainPanel.add(passField);

		// Enter Button
		JButton enterButton = new JButton("ENTER");
		enterButton.setFont(new Font("Montserrat", Font.BOLD, 18));
		enterButton.setForeground(Color.WHITE);
		enterButton.setBackground(new Color(0x4A90E2));
		enterButton.setFocusPainted(false);
		enterButton.setBounds(155, 220, 140, 42);
		enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		enterButton.setBorder(BorderFactory.createLineBorder(new Color(0x4A90E2), 1, true));

		// Hover effect
		enterButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				enterButton.setBackground(new Color(0x357ABD));
			}

			public void mouseExited(MouseEvent e) {
				enterButton.setBackground(new Color(0x4A90E2));
			}
		});

		enterButton.addActionListener(e -> {
			String username = userField.getText().trim();
			String password = new String(passField.getPassword()).trim();

			if (validateLogin(username, password)) {
				showCustomDialog(frame, "Login successful!");
				frame.dispose();
				showDashboard(loggedInUsername, loggedInUserRole);
			} else {
				showCustomDialog(frame, "Invalid credentials. Please try again.");
			}
		});

		mainPanel.add(enterButton);
		frame.getRootPane().setDefaultButton(enterButton);
		frame.setContentPane(mainPanel);
		frame.setVisible(true);
	}

	private boolean validateLogin(String username, String password) {
		System.out.println("Looking for user.csv in: " + new java.io.File("user.csv").getAbsolutePath());

		try (CSVReader reader = new CSVReader(new FileReader("user.csv"))) {
			reader.readNext(); // skip header
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length >= 3) {
					if (nextLine[0].equals(username) && nextLine[1].equals(password)) {
						loggedInUserRole = nextLine[2]; // role (admin or employee)
						loggedInUsername = nextLine[0]; // username
						if (nextLine.length > 3 && !nextLine[3].isEmpty()) {
						    loggedInEmployeeID = nextLine[3]; // employeeID
						} else {
						    loggedInEmployeeID = ""; // fallback
						}
						return true;
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Error reading user.csv: " + e.getMessage());
		}

		return false;
	}

	private void showDashboard(String empId, String role) {
		frame = new JFrame("MOTORPH Dashboard");
		frame.setSize(630, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setResizable(false);
		initComponents();
		frame.setVisible(true);
	}

	private void initComponents() {
		// Sidebar Panel
		JPanel sidebar = new JPanel();
		sidebar.setBackground(new Color(0x0E3172));
		sidebar.setBounds(0, 0, 150, 400); // Sidebar height = frame height
		sidebar.setLayout(null);
		frame.add(sidebar);

		// Sidebar MOTOPH logo
		JLabel motorphLabel = new JLabel("MOTORPH");
		motorphLabel.setForeground(Color.WHITE);
		motorphLabel.setFont(new Font("Montseratt", Font.BOLD, 20));
		motorphLabel.setBounds(24, 20, 130, 30);
		sidebar.add(motorphLabel);

		// Sidebar Buttons
		sidebar.add(createSidebarButton("Employee Info", 70));
		sidebar.add(createSidebarButton("Attendance", 120));
		sidebar.add(createSidebarButton("Payroll", 170));
		if (loggedInUserRole.equalsIgnoreCase("admin")) {
		    sidebar.add(createSidebarButton("New Employee", 220));
		}

		// Log out label
		JLabel logoutLabel = new JLabel("Log out");
		logoutLabel.setForeground(Color.WHITE);
		logoutLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		logoutLabel.setBounds(50, 330, 100, 20);
		logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?",
						"Confirm Logout", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					frame.dispose(); // close dashboard
					new g14javaapplication(); // re-open login
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				logoutLabel.setForeground(Color.LIGHT_GRAY); // subtle hover effect
			}

			@Override
			public void mouseExited(MouseEvent e) {
				logoutLabel.setForeground(Color.WHITE);
			}
		});
		sidebar.add(logoutLabel);

		// Main Button Panels (fits 2x2 grid in remaining area)
		addMainButton("Employee Info", "employee.png", 180, 40);
		addMainButton("Payroll", "payroll.png", 400, 40);
		addMainButton("Attendance", "attendance.png", 180, 200);
		if (loggedInUserRole.equalsIgnoreCase("admin")) {
		    addMainButton("New Employee", "add_user.png", 400, 200);
		}
	}

	private JButton createSidebarButton(String text, int y) {
		JButton button = new JButton(text);
		button.setBounds(10, y, 130, 35);
		button.setBackground(new Color(0x3F66B0));
		button.setForeground(Color.WHITE);
		button.setFont(new Font("SansSerif", Font.PLAIN, 13));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Hover effect
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(60, 100, 180));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(0x3F66B0));
			}
		});

		// Add action based on text
		button.addActionListener(e -> {
			switch (text) {
			case "Employee Info":
			    if (loggedInUserRole.equalsIgnoreCase("admin")) {
			        new EmployeeTableFrame(loggedInUsername, loggedInUserRole).setVisible(true);
			    } else {
			        // Load employee's own data from employees.csv
			    	try (CSVReader reader = new CSVReader(new FileReader("employees.csv"))) {
			    	    reader.readNext(); // Skip header
			    	    String[] nextLine;
			    	    boolean found = false;

			    	    while ((nextLine = reader.readNext()) != null) {
			    	    	if (nextLine.length >= 13 && nextLine[0].equalsIgnoreCase(loggedInEmployeeID)) {
			    	    		new EmployeeDetailsFrame(nextLine);
			    	    		found = true;
			    	    		break;
			    	    	}
			    	    }

			    	    if (!found) {
			    	        JOptionPane.showMessageDialog(frame, "No matching employee record found for username: " + loggedInUsername);
			    	    }
			    	} catch (Exception ex) {
			    	    JOptionPane.showMessageDialog(frame, "Error loading employee data (employees.csv): " + ex.getMessage());
			    	    ex.printStackTrace();
			    	}
			    }
			    break;
			case "Attendance":
			    new AttendanceFrame(loggedInUsername, loggedInUserRole, loggedInEmployeeID).setVisible(true);
			    break;
			case "Payroll":
			    new PayrollSelectionFrame(loggedInUsername, loggedInUserRole, loggedInEmployeeID).setVisible(true);
			    break;
			case "New Employee":
				new NewEmployeeForm(new EmployeeTableFrame(loggedInUsername, loggedInUserRole)).setVisible(true);
				break;
			}
		});

		return button;
	}

	private void addMainButton(String label, String iconPath, int x, int y) {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setBounds(x, y, 180, 120);
		buttonPanel.setLayout(null);
		frame.add(buttonPanel);

		// Icon
		JLabel iconLabel = new JLabel(new ImageIcon(iconPath));
		iconLabel.setBounds(11, 0, 118, 90);
		buttonPanel.add(iconLabel);

		// Text
		JLabel textLabel = new JLabel(label, SwingConstants.CENTER);
		textLabel.setForeground(new Color(0x0E3172));
		textLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
		textLabel.setBounds(0, 95, 140, 20);
		buttonPanel.add(textLabel);
		buttonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				switch (label) {
				case "Employee Info":
				    if (loggedInUserRole.equalsIgnoreCase("admin")) {
				        new EmployeeTableFrame(loggedInUsername, loggedInUserRole).setVisible(true);
				    } else {
				        // Load employee's own data from employee.csv
				    	try (CSVReader reader = new CSVReader(new FileReader("employees.csv"))) {
				    	    reader.readNext(); // Skip header
				    	    String[] nextLine;
				    	    boolean found = false;

				    	    while ((nextLine = reader.readNext()) != null) {
				    	    	if (nextLine.length >= 13 && nextLine[0].equalsIgnoreCase(loggedInEmployeeID)) {
				    	    		new EmployeeDetailsFrame(nextLine);
				    	    		found = true;
				    	    		break;
				    	    	}
				    	    }

				    	    if (!found) {
				    	        JOptionPane.showMessageDialog(frame, "No matching employee record found for username: " + loggedInUsername);
				    	    }
				    	} catch (Exception ex) {
				    	    JOptionPane.showMessageDialog(frame, "Error loading employee data (employees.csv): " + ex.getMessage());
				    	    ex.printStackTrace();
				    	}
				    }
				    break;
				case "Attendance":
				    new AttendanceFrame(loggedInUsername, loggedInUserRole, loggedInEmployeeID).setVisible(true);
				    break;
				case "Payroll":
				    new PayrollSelectionFrame(loggedInUsername, loggedInUserRole, loggedInEmployeeID).setVisible(true);
				    break;
				case "New Employee":
					new NewEmployeeForm(new EmployeeTableFrame(loggedInUsername, loggedInUserRole)).setVisible(true);
					break;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				buttonPanel.setBackground(new Color(0xF0F0F0));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				buttonPanel.setBackground(Color.WHITE);
			}
		});
	}

	// ✅ Custom Dialog Method
	private void showCustomDialog(JFrame parent, String message) {
		JDialog dialog = new JDialog(parent, true);
		dialog.setUndecorated(true);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(300, 100);
		dialog.setLocationRelativeTo(parent);

		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(new Color(0x6B9FFF));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
		messageLabel.setForeground(Color.WHITE);
		messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

		JButton okayButton = new JButton("OKAY");
		okayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		okayButton.setFont(new Font("Montserrat", Font.BOLD, 10));
		okayButton.setFocusPainted(false);
		okayButton.setForeground(Color.BLACK);
		okayButton.setBackground(Color.WHITE);
		okayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		okayButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

		// 🖱️ Hover effect (unchanged)
		okayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				okayButton.setBackground(Color.LIGHT_GRAY);
				okayButton.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
								BorderFactory.createEmptyBorder(8, 20, 8, 20)));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				okayButton.setBackground(Color.WHITE);
				okayButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
			}
		});

		okayButton.addActionListener(e -> dialog.dispose());

		contentPanel.add(messageLabel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(okayButton);

		dialog.add(contentPanel, BorderLayout.CENTER);

		dialog.getRootPane().setDefaultButton(okayButton); // ← this line lets ENTER press OK
		dialog.setVisible(true);
	}

	private void showEmployeeInfo() {
		JOptionPane.showMessageDialog(frame, "Showing Employee Info...");
	}

	private void showAttendance() {
		JOptionPane.showMessageDialog(frame, "Showing Attendance Records...");
	}

	private void showPayroll() {
		JOptionPane.showMessageDialog(frame, "Displaying Payroll Info...");
	}

	private void showNewEmployeeForm() {
		JOptionPane.showMessageDialog(frame, "Opening New Employee Form...");
	}
}