package g14javaapplication;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClockInOutHandler {

    private static final String FILE_PATH = "timelogs.csv";

    public static String logTime(String empID, String username, String action) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        boolean recordUpdated = false;

        File file = new File(FILE_PATH);
        List<String[]> lines = new ArrayList<>();
        boolean fileExists = file.exists();

        try {
            if (fileExists) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean isFirstLine = true;
                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) { // skip header
                            isFirstLine = false;
                            continue;
                        }

                        String[] parts = line.split(",", -1);
                        if (parts.length >= 5 &&
                            parts[0].equals(empID) &&
                            parts[2].equals(date)) {

                            if (action.equals("Clock In") && parts[3].isEmpty()) {
                                parts[3] = time;
                                recordUpdated = true;
                            } else if (action.equals("Clock Out") && parts[4].isEmpty()) {
                                parts[4] = time;
                                recordUpdated = true;
                            }
                        }
                        lines.add(parts);
                    }
                }
            }

            if (!recordUpdated) {
                String[] newLine;
                if (action.equals("Clock In")) {
                    newLine = new String[]{empID, username, date, time, ""};
                } else {
                    newLine = new String[]{empID, username, date, "", time};
                }
                lines.add(newLine);
            }

            // Write back including header
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("EmployeeID,Username,Date,ClockIn,ClockOut");
                bw.newLine();
                for (String[] parts : lines) {
                    bw.write(String.join(",", parts));
                    bw.newLine();
                }
            }

            return time;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}