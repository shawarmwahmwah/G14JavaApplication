package g14javaapplication;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.File;
import java.util.Arrays;

public class TestOpenCSV {
    public static void main(String[] args) {
        try {
            File csvFile = new File("employees.csv");
            System.out.println("CSV FILE PATH: " + csvFile.getAbsolutePath());

            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            int lineCount = 0;

            while ((line = reader.readNext()) != null) {
                System.out.println("Line " + (++lineCount) + ": " + Arrays.toString(line));
            }

            reader.close();

            if (lineCount == 0) {
                System.out.println("❌ WALANG NA-READ NA LINES");
            } else {
                System.out.println("✅ SUCCESSFULLY READ " + lineCount + " LINES");
            }

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }
    }
}