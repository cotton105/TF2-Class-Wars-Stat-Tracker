package TF2ClassWarsStatTracker.util;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.Scanner;

public class MapMercenaryTable {
    public static void main(String[] args) {
//        CSVReader reader = null;
        Scanner scanner = new Scanner(System.in);
        System.out.print(".csv file to read:\n> ");
        String filename = scanner.nextLine();
        try (InputStream is = MapMercenaryTable.class.getResourceAsStream("/" + filename)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is));
                for (String[] all : reader.readAll()) {
                    for (String s : all) {
                        System.out.print(s + " ");
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
