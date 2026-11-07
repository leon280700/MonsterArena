package monsterarena;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class HighScore {

    public record HighScoreEntry(String playerName, int kills, String mapSize){

        public static HighScoreEntry parse(String line){
            String[] values = line.split(";");
            return new HighScoreEntry(values[0], Integer.parseInt(values[1]), String.valueOf(values[2]));
        }

        String toLine() {
            return playerName + ";" + kills + ";" + mapSize;
        }
    }

    private List<HighScoreEntry> entries = new ArrayList<>();

    public HighScore() throws IOException {
        String fileName = "highscore.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            try {
                entries.add(HighScoreEntry.parse(line));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        reader.close();
    }

    public void writeToHighScore(String playerName, int kills, String mapSize) {
        entries.add(new HighScoreEntry(playerName, kills, mapSize));

        entries.sort(Comparator.comparingInt(HighScoreEntry::kills).reversed());

        if (entries.size() > 10) {
            entries = new ArrayList<>(entries.subList(0, 10));
        }

        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            for (HighScoreEntry entry : entries) {
                writer.write(entry.toLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
