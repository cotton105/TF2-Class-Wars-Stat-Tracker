package TF2ClassWarsStatTracker.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Print {
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    /**
     * Print a string with a timestamp prepended.
     * @param str message to output.
     */
    public static void timestamp(String str) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.printf("[%s] %s\n", timeFormat.format(timestamp), str);
    }

    public static void commaSeparated(ArrayList<String> strs, boolean timestamp) {
        StringBuilder msg = new StringBuilder();
        for (String str : strs) {
            if (msg.length() != 0)
                msg.append(", ").append(str);
            else
                msg.append(str);
        }
        String msgStr = msg.toString();
        if (timestamp)
            Print.timestamp(msgStr);
        else
            System.out.println(msgStr);
    }
}
