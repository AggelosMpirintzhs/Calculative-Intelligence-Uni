package ask2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class GenerateSDO {

    static Random rnd = new Random();

    public static void main(String[] args) throws IOException {

        Locale.setDefault(Locale.US);

        BufferedWriter bw = new BufferedWriter(new FileWriter("sdo.csv"));
        bw.write("x1,x2\n");

        gen(bw,150,0.75,1.25,0.75,1.25);
        gen(bw,150,0,0.5,0,0.5);
        gen(bw,150,0,0.5,1.5,2);
        gen(bw,150,1.5,2,0,0.5);
        gen(bw,150,1.5,2,1.5,2);

        gen(bw,75,0.6,0.8,0,0.4);
        gen(bw,75,0.6,0.8,1.6,2);
        gen(bw,75,1.2,1.4,0,0.4);
        gen(bw,75,1.2,1.4,1.6,2);

        gen(bw,150,0,2,0,2); // θόρυβος

        bw.close();
        System.out.println("Created sdo.csv");
    }

    static void gen(BufferedWriter bw, int n,
                    double xmin, double xmax,
                    double ymin, double ymax) throws IOException {

        for (int i = 0; i < n; i++) {
            double x = xmin + (xmax - xmin) * rnd.nextDouble();
            double y = ymin + (ymax - ymin) * rnd.nextDouble();
            bw.write(String.format(Locale.US, "%.6f,%.6f%n", x, y));
        }
    }
}
