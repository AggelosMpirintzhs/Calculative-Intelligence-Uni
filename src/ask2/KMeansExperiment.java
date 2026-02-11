package ask2;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class KMeansExperiment {

    static final int RUNS = 50;
    static final int MAX_ITERS = 500;
    static final double TOL = 1e-8;

    public static void main(String[] args) throws Exception {
        String path = (args.length > 0) ? args[0] : "sdo.csv";
        List<Point> data = SDOLoader.load(path);

        System.out.println("Loaded N=" + data.size() + " points from " + path);

        int[] ks = {3, 5, 7, 9, 11, 13};    // like #define M

        try (PrintWriter summary = new PrintWriter(new FileWriter("sse_vs_k.csv"))) {
            summary.println("k,best_sse");

            for (int k : ks) {
                System.out.println("\n=== K = " + k + " ===");

                KMeansResult best = null;

                for (int r = 1; r <= RUNS; r++) {
                    Random rnd = new Random(System.nanoTime() + 31L * r + 997L * k);
                    KMeansResult res = KMeans.fit(data, k, rnd, MAX_ITERS, TOL);

                    System.out.printf(Locale.US, "run %02d: SSE = %.8f%n", r, res.sse);

                    if (best == null || res.sse < best.sse) best = res;
                }

                System.out.printf(Locale.US, "BEST K=%d: SSE=%.8f%n", k, best.sse);
                summary.printf(Locale.US, "%d,%.10f%n", k, best.sse);

                saveCenters("best_centers_k" + k + ".csv", best.centers);
                saveAssignment("best_assignment_k" + k + ".csv", data, best.assignment);
            }
        }

        System.out.println("\nDone. Created: sse_vs_k.csv, best_centers_k*.csv, best_assignment_k*.csv");
    }

    private static void saveCenters(String filename, Point[] centers) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("cluster,cx,cy");
            for (int c = 0; c < centers.length; c++) {
                pw.printf(Locale.US, "%d,%.8f,%.8f%n", c, centers[c].x, centers[c].y);
            }
        }
    }

    private static void saveAssignment(String filename, List<Point> data, int[] assign) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("x1,x2,cluster");
            for (int i = 0; i < data.size(); i++) {
                Point p = data.get(i);
                pw.printf(Locale.US, "%.8f,%.8f,%d%n", p.x, p.y, assign[i]);
            }
        }
    }
}
