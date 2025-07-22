package g14javaapplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AttendanceUtils {

    public static Map<String, Double> computeWorkedHours(String attendanceFilePath) {
        Map<String, Double> hoursWorkedMap = new HashMap<>();

        String line;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(attendanceFilePath))) {
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String employeeId = data[0];
                String date = data[1];
                String login = data[2];
                String logout = data[3];

                LocalDateTime loginTime = LocalDateTime.parse(date + " " + login, formatter);
                LocalDateTime logoutTime = LocalDateTime.parse(date + " " + logout, formatter);

                double hoursWorked = Duration.between(loginTime, logoutTime).toMinutes() / 60.0;

                hoursWorkedMap.put(employeeId, hoursWorkedMap.getOrDefault(employeeId, 0.0) + hoursWorked);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return hoursWorkedMap;
    }
}