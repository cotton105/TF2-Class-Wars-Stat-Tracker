package TF2ClassWarsStatTracker.util;

import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileHandler {
    public static final String MAPS_JSON = "/maps.json";

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

    public static void writeToJSONFile(Object obj, String filename) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        try (PrintWriter pw = new PrintWriter(filename)) {
            gson.toJson(obj, pw);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static JsonArray readJSONArray(String filename) throws FileNotFoundException {
        InputStream is = FileHandler.class.getResourceAsStream(filename);
        if (is == null)
            throw new FileNotFoundException(String.format("File \"%s\" not found.", filename));
        Reader reader = new InputStreamReader(is);
        JsonElement element = JsonParser.parseReader(reader);
        return element.getAsJsonArray();
    }

    public static List<String[]> readCSVLines(String filename) throws IOException {
        try (InputStream is = FileHandler.class.getResourceAsStream(filename)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is));
                return reader.readAll();
            } catch (IOException | CsvException ex) {
                Print.error(ex.getMessage());
                ex.printStackTrace();
            }
        }
        throw new NullPointerException();
    }

    public static List<File> getFilesInDirectory(File directory) {
        List<File> fileList = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
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