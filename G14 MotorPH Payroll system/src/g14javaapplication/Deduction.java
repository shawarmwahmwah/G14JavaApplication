package g14javaapplication;

public class Deduction {
    private String employeeId;
    private double tax;
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double totalDeductions;

    public void computeAllDeductions(double salary) {
        sss = computeSSS(salary);
        philHealth = computePhilHealth(salary);
        pagIbig = computePagIbig(salary);
        tax = computeTax(salary - (sss + philHealth + pagIbig)); // taxable income

        calculateTotalDeductions();
    }

    private double computeSSS(double salary) {
        double minMSC = 5000.0;
        double maxMSC = 35000.0;
        double applicableMSC = Math.max(minMSC, Math.min(salary, maxMSC));
        return applicableMSC * 0.15; // employee + employer share, adjust if needed
    }

    private double computePhilHealth(double salary) {
        double rate = 0.05;
        double min = 10000;
        double max = 100000;
        double base = Math.max(min, Math.min(salary, max));
        return base * rate / 2; // divide by 2 for employee share
    }

    private double computePagIbig(double salary) {
        double pagIbig;
        if (salary >= 1000 && salary <= 1500) {
            pagIbig = salary * 0.01;
        } else {
            pagIbig = salary * 0.02;
        }
        return Math.min(pagIbig, 100); // max limit
    }

    private double computeTax(double taxableIncome) {
        if (taxableIncome <= 20833) return 0;
        else if (taxableIncome <= 33333) return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome <= 66667) return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166667) return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    private void calculateTotalDeductions() {
        totalDeductions = tax + sss + philHealth + pagIbig;
    }

    // Getters and setters
    public double getTax() { return tax; }
    public double getSSS() { return sss; }
    public double getPhilHealth() { return philHealth; }
    public double getPagIbig() { return pagIbig; }
    public double getTotalDeductions() { return totalDeductions; }
    public String getEmployeeId() { return employeeId; }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}