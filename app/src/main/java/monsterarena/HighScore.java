package monsterarena;

import lombok.Getter;
import monsterarena.model.Difficulty;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class HighScore {

    private record HighScoreEntry(String name, int kills, Difficulty difficulty){

        public static HighScoreEntry parse(String line){
            String[] values = line.split(";");
            return new HighScoreEntry(values[0], Integer.parseInt(values[1]), Difficulty.valueOf(values[2]));
        }

        String toLine() {
            return name + ";" + kills + ";" + difficulty.name();
        }

        String toReadableLine() {
            return name + "         " + kills + "           " + difficulty.name();

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
    }

    public void writeToHighScore(String playerName, int kills, Difficulty difficulty) {
        entries.add(new HighScoreEntry(playerName, kills, difficulty));

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

    public ArrayList<String> getScoreLines() {
        ArrayList<String> scoreLines = new ArrayList<>();
        for (HighScoreEntry hse : entries) {
            scoreLines.add(hse.toReadableLine());
        }
        return scoreLines;
    }
}
