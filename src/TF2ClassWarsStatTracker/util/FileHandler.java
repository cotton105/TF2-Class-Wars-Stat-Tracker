package TF2ClassWarsStatTracker.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FileHandler {
    public static final String MAPS = "/maps.txt";

    public static ArrayList<String> readTextFileLines(String filename) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        try (InputStream is = FileHandler.class.getResourceAsStream(filename)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return lines;
    }

    public static void readXLFile(String filename) throws IOException {
        try {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> it = sheet.iterator();
            while (it.hasNext()) {
                Row row = it.next();
                Iterator<Cell> cellIt = row.cellIterator();
                while (cellIt.hasNext()) {
                    Cell cell = cellIt.next();
                    System.out.println(cell.getStringCellValue() + "\t\t\t");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
//        try (InputStream is = FileHandler.class.getResourceAsStream(filename)) {
//            assert is != null;
//            XSSFWorkbook wb = new XSSFWorkbook(is);
//            XSSFSheet sheet = wb.getSheetAt(0);
//            Iterator<Row> it = sheet.iterator();
//            while (it.hasNext()) {
//                Row row = it.next();
//                Iterator<Cell> cellIt = row.cellIterator();
//                while (cellIt.hasNext()) {
//                    Cell cell = cellIt.next();
//                    System.out.println(cell.getStringCellValue() + "\t\t\t");
//                }
//            }
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}