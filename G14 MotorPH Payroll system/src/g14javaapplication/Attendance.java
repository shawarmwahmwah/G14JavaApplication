package g14javaapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Attendance {
    private String employeeId;
    private String date;
    private String timeIn;
    private String timeOut;
    private int logId;
    private String timestamp;
    private String activity;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}