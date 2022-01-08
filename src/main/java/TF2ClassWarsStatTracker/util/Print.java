package TF2ClassWarsStatTracker.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Print {
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static void print(String msg) {
        print(msg, true);
    }

    public static void print(String msg, boolean timestamp) {
        if (timestamp) {
            timestamp(msg);
        } else {
            System.out.println(msg);
        }
    }

    public static void format(String msg, Object ... args) {
        print(String.format(msg, args));
    }

    /**
     * Print a string with a timestamp prepended.
     * @param msg message to output.
     */
    private static void timestamp(String msg) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.printf("[%s] %s\n", timeFormat.format(timestamp), msg);
    }

    public static void error(String msg) {
        timestamp(String.format("ERR: %s", msg));
    }

    public static void formatError(String msg, Object ... args) {
        error(String.format(msg, args));
    }

    public static void debug(String msg, Object ... args) {
        format("<DEBUG> " + msg, args);
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
