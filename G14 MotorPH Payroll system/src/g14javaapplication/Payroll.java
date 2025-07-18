package g14javaapplication;

public class Payroll {
	private Employee employee;
	private double baseSalary;
	private double allowance;
	private double overtimeRate;
	private double overtimeHours;
	private double grossPay;
	private double netPay;

	private Bonuses bonuses;
	private Deduction deductions;

	public double calculateOvertimePay() {
		return overtimeHours * overtimeRate;
	}

	public double calculateGrossPay() {
		grossPay = baseSalary + allowance + calculateOvertimePay();
		if (bonuses != null) {
			grossPay += bonuses.getTotalBonuses();
		}
		return grossPay;
	}

	public double calculateNetPay() {
		calculateGrossPay(); // ensure gross is calculated

		if (deductions == null) {
			deductions = new Deduction();
		}

		deductions.computeAllDeductions(grossPay); // pass grossPay for correct tax computation
		netPay = grossPay - deductions.getTotalDeductions();
		return netPay;
	}

	// Getters and Setters

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public double getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(double baseSalary) {
		this.baseSalary = baseSalary;
	}

	public double getAllowance() {
		return allowance;
	}

	public void setAllowance(double allowance) {
		this.allowance = allowance;
	}

	public double getOvertimeHours() {
		return overtimeHours;
	}

	public void setOvertimeHours(double overtimeHours) {
		this.overtimeHours = overtimeHours;
	}

	public double getOvertimeRate() {
		return overtimeRate;
	}

	public void setOvertimeRate(double overtimeRate) {
		this.overtimeRate = overtimeRate;
	}

	public void setBonuses(Bonuses bonuses) {
		this.bonuses = bonuses;
	}

	public Bonuses getBonuses() {
		return bonuses;
	}

	public void setDeductions(Deduction deductions) {
		this.deductions = deductions;
	}

	public Deduction getDeductions() {
		return deductions;
	}

	public double getGrossPay() {
		return grossPay;
	}

	public double getNetPay() {
		return netPay;
	}
}
