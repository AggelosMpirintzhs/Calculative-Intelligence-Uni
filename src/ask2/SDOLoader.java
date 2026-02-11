package ask2;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SDOLoader {

    // Reads CSV with header: x1,x2  (or without header)
    public static List<Point> load(String path) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));
        List<Point> data = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            // skip header if present
            if (i == 0 && line.toLowerCase().contains("x1")) continue;

            line = line.replace(" ", "");
            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            double x1 = Double.parseDouble(parts[0]);
            double x2 = Double.parseDouble(parts[1]);
            data.add(new Point(x1, x2));
        }
        return data;
    }
}
