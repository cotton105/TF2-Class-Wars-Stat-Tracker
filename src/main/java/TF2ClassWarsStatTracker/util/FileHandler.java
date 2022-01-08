package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.exceptions.InvalidFileNameException;
import TF2ClassWarsStatTracker.game.GameMap;
import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileHandler {
    public static final String DEFAULT_MAPS_JSON = "res\\maps.json";

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
        Reader reader = new InputStreamReader(new FileInputStream(filename));
        JsonElement element = JsonParser.parseReader(reader);
        return element.getAsJsonArray();
    }

    public static List<String[]> readCSVLines(String filename) throws IOException {
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(filename));
            CSVReader csvReader = new CSVReader(is);
            return csvReader.readAll();
        } catch (CsvException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
        }
        throw new FileNotFoundException();
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

    public static void initialiseJsonMapsFile() {
        try {
            File file = new File(FileHandler.DEFAULT_MAPS_JSON);
            String dirPath = DEFAULT_MAPS_JSON.substring(0, DEFAULT_MAPS_JSON.lastIndexOf("\\") + 1);
            createDirectory(dirPath);
            if (file.createNewFile())
                Print.format("File %s created", DEFAULT_MAPS_JSON);
            else
                Print.format("File %s already exists.", DEFAULT_MAPS_JSON);
            List<GameMap> maps = new ArrayList<>();
            writeToJSONFile(maps, DEFAULT_MAPS_JSON);
        } catch (IOException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void newServerJsonFile(String serverName) throws InvalidFileNameException {
        try {
            if (!validFileName(serverName))
                throw new InvalidFileNameException(serverName);
            String filePath = "res\\servers\\" + serverName + ".json";
            File file = new File(filePath);
            String dirPath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
            createDirectory(dirPath);
            if (file.createNewFile()) {
                Print.format("File %s created", filePath);
                List<GameMap> maps = new ArrayList<>();
                writeToJSONFile(maps, filePath);
            }
            else
                Print.format("File %s already exists.", filePath);
        } catch (IOException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void createDirectory(String newPath) {
        if (Files.isDirectory(Paths.get(newPath)))
            Print.format("Path %s already exists.", newPath);
        else {
            Path path = Paths.get(newPath);
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String removeFileExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    public static boolean validFileName(String filename) {
        return !(filename.equals("") || filename == null);
    }
}