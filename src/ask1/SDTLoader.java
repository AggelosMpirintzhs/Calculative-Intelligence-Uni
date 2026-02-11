package ask1;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SDTLoader {

    // CSV: x1,x2,class  (class in {1,2,3,4})
    public static List<LabeledExample> load(String path) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));
        List<LabeledExample> out = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            // skip header if present
            if (i == 0 && line.toLowerCase().contains("x1")) continue;

            line = line.replace(" ", "");
            //String[] p = line.split(",");
            String[] p = line.contains(";") ? line.split(";") : line.split(",");

            if (p.length < 3) continue;

            double x1 = Double.parseDouble(p[0]);
            double x2 = Double.parseDouble(p[1]);
            int cls1to4 = Integer.parseInt(p[2]);
            int label0to3 = cls1to4 - 1;

            out.add(new LabeledExample(x1, x2, label0to3));
        }
        return out;
    }
}
