package ask1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class GenerateSDT {

    static Random rnd = new Random();

    public static void main(String[] args) throws IOException {

        Locale.setDefault(Locale.US);

        int N = 8000;
        double[][] data = new double[N][3];

        for (int i = 0; i < N; i++) {
            double x1 = 2 * rnd.nextDouble(); // [0,2]
            double x2 = 2 * rnd.nextDouble(); // [0,2]
            int cls = classify(x1, x2);       // 1..4

            data[i][0] = x1;
            data[i][1] = x2;
            data[i][2] = cls;
        }

        shuffle(data);

        saveFile("sdt_train.csv", data, 0, 4000);
        saveFile("sdt_test.csv", data, 4000, 8000);

        System.out.println("Created sdt_train.csv and sdt_test.csv");
    }

    static int classify(double x1, double x2) {

        double r1 = (x1 - 0.5)*(x1 - 0.5) + (x2 - 0.5)*(x2 - 0.5);
        double r2 = (x1 - 1.5)*(x1 - 1.5) + (x2 - 0.5)*(x2 - 0.5);
        double r3 = (x1 - 0.5)*(x1 - 0.5) + (x2 - 1.5)*(x2 - 1.5);
        double r4 = (x1 - 1.5)*(x1 - 1.5) + (x2 - 1.5)*(x2 - 1.5);

        if (r1 < 0.2) {
            if (x1 > 0.5 && x2 > 0.5) return 1;
            if (x1 < 0.5 && x2 > 0.5) return 2;
            if (x1 > 0.5 && x2 < 0.5) return 2;
            if (x1 < 0.5 && x2 < 0.5) return 1;
        }

        if (r2 < 0.2) {
            if (x1 > 1.5 && x2 > 0.5) return 1;
            if (x1 < 1.5 && x2 > 0.5) return 2;
            if (x1 > 1.5 && x2 < 0.5) return 2;
            if (x1 < 1.5 && x2 < 0.5) return 1;
        }

        if (r3 < 0.2) {
            if (x1 > 0.5 && x2 > 1.5) return 1;
            if (x1 < 0.5 && x2 > 1.5) return 2;
            if (x1 > 0.5 && x2 < 1.5) return 2;
            if (x1 < 0.5 && x2 < 1.5) return 1;
        }

        if (r4 < 0.2) {
            if (x1 > 1.5 && x2 > 1.5) return 1;
            if (x1 < 1.5 && x2 > 1.5) return 2;
            if (x1 > 1.5 && x2 < 1.5) return 2;
            if (x1 < 1.5 && x2 < 1.5) return 1;
        }

        double prod = (x1 - 1)*(x2 - 1);

        if (prod > 0) return 3;
        if (prod < 0) return 4;

        return 3;
    }

    static void shuffle(double[][] data) {
        for (int i = data.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            double[] tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
    }

    static void saveFile(String filename, double[][] data, int from, int to) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("x1,x2,class\n");
        for (int i = from; i < to; i++) {
            bw.write(String.format(Locale.US, "%.6f,%.6f,%d%n",
                    data[i][0], data[i][1], (int)data[i][2]));
        }
        bw.close();
    }
}
