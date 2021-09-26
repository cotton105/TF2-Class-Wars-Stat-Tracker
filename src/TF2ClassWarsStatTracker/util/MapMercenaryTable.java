package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.GameMap;
import TF2ClassWarsStatTracker.GamemodeGrid;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapMercenaryTable {
    public static void main(String[] args) {
    }

    public static GameMap getGameMapFromCSV(String filename) {
        GameMap map = new GameMap(FileHandler.removeFileExtension(filename));
        String constructedName = "/maps/" + filename;
        try (InputStream is = MapMercenaryTable.class.getResourceAsStream(constructedName)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is));
                List<String[]> lines = reader.readAll();
                int intsStartIndex = getIntegerStartIndex(lines);
                for (int gamemode=0; gamemode<5; gamemode++) {
                    GamemodeGrid grid = new GamemodeGrid();
                    for (int row=0; row<9; row++) {
                        int[] lineTrimmed = trimLineToInts(lines.get(intsStartIndex+(row*2)));
                        int[] nextLineTrimmed = trimLineToInts(lines.get(intsStartIndex+(row*2)+1));
                        for (int column=0; column<9; column++) {
                            int bluWins = lineTrimmed[column + (9 * gamemode)]-1;
                            int redWins = nextLineTrimmed[column + (9 * gamemode)]-1;
                            grid.setMercenaryWins(column, row, bluWins, redWins);
                        }
                    }
                    map.addGamemodeGrid(grid);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private static int[] trimLineToInts(String[] line) {
        ArrayList<Integer> lineArrayListTrimmed = new ArrayList<>();
        for (String item : line) {
            if (item.matches("-?\\d+"))
                lineArrayListTrimmed.add(Integer.parseInt(item));
        }
        int[] lineArrayTrimmed = new int[lineArrayListTrimmed.size()];
        for (int i=0; i<lineArrayListTrimmed.size(); i++)
            lineArrayTrimmed[i] = lineArrayListTrimmed.get(i);
        return lineArrayTrimmed;
    }

    // Find the line in the .csv that has the integer values
    private static int getIntegerStartIndex(List<String[]> lines) throws IOException {
        for (int line=0; line<lines.size(); line++) {
            String[] lineStrArray = lines.get(line);
            for (String s : lineStrArray) {
                String itemStr = String.valueOf(s);
                if (itemStr.matches("-?\\d+")) {
                    return line;
                }
            }
        }
        return -1;
    }

    public static void mapCSVsToJSON() {
        List<File> fileList = FileHandler.getFilesInDirectory(new File("res/maps"));
        List<GameMap> maps = new ArrayList<>();
        for (File file : fileList) {
            GameMap map = MapMercenaryTable.getGameMapFromCSV(file.getName());
            maps.add(map);
        }
        FileHandler.convertToJSONFile(maps, "maps");
    }
}
