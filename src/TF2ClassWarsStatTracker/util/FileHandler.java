package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.GameMap;
import com.google.gson.*;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static void readXLFile(String filename) {
        try {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
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
    }

    public static void convertToJSONFile(Object obj, String filename) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        try (PrintWriter pw = new PrintWriter(filename + ".json")) {
            gson.toJson(obj, pw);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static JsonArray readJSONArray(String filename) throws NullPointerException {
        try (InputStream is = FileHandler.class.getResourceAsStream(filename)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            Reader reader = new InputStreamReader(is);
            JsonElement element = JsonParser.parseReader(reader);
            return element.getAsJsonArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        throw new NullPointerException();
    }

    public static List<File> getFilesInDirectory(File directory) {
        List<File> fileList = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                fileList.addAll(getFilesInDirectory(file));
            else
                fileList.add(file);
        }
        return fileList;
    }

    public static String removeFileExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }
}