package ask2;

import java.util.*;

public class KMeans {

    public static KMeansResult fit(List<Point> data, int k, Random rnd, int maxIters, double tol) {
        int n = data.size();
        if (k <= 0 || k > n) throw new IllegalArgumentException("k must be in [1, N]");

        Point[] centers = initCentersFromData(data, k, rnd);    // random points from dataset
        int[] assign = new int[n];
        Arrays.fill(assign, -1);

        for (int iter = 0; iter < maxIters; iter++) {
            boolean changed = false;

            // 1) Assignment step
            for (int i = 0; i < n; i++) {
                Point p = data.get(i);

                int bestC = 0;
                double bestD = p.dist2(centers[0]);

                for (int c = 1; c < k; c++) {
                    double d = p.dist2(centers[c]);
                    if (d < bestD) {
                        bestD = d;
                        bestC = c;
                    }
                }

                if (assign[i] != bestC) {
                    assign[i] = bestC;
                    changed = true;
                }
            }

            // 2) Update step
            double[] sx = new double[k];
            double[] sy = new double[k];
            int[] cnt = new int[k];

            for (int i = 0; i < n; i++) {
                int c = assign[i];
                Point p = data.get(i);
                sx[c] += p.x;
                sy[c] += p.y;
                cnt[c]++;
            }

            Point[] newCenters = new Point[k];
            for (int c = 0; c < k; c++) {
                if (cnt[c] == 0) {
                    // empty cluster
                    newCenters[c] = data.get(rnd.nextInt(n));
                } else {
                    newCenters[c] = new Point(sx[c] / cnt[c], sy[c] / cnt[c]);
                }
            }

            // 3) Convergence check
            double maxMove2 = 0.0;
            for (int c = 0; c < k; c++) {
                double move2 = centers[c].dist2(newCenters[c]);
                if (move2 > maxMove2) maxMove2 = move2;
            }
            centers = newCenters;

            if (!changed) break;          // no assignment changes
            if (maxMove2 < tol * tol) break; // centers barely moved
        }

        double sse = computeSSE(data, centers, assign);
        return new KMeansResult(centers, assign, sse);
    }

    private static Point[] initCentersFromData(List<Point> data, int k, Random rnd) {
        int n = data.size();
        List<Integer> idx = new ArrayList<>(n);
        for (int i = 0; i < n; i++) idx.add(i);
        Collections.shuffle(idx, rnd);

        Point[] centers = new Point[k];
        for (int c = 0; c < k; c++) centers[c] = data.get(idx.get(c));
        return centers;
    }

    private static double computeSSE(List<Point> data, Point[] centers, int[] assign) {
        double sse = 0.0;
        for (int i = 0; i < data.size(); i++) {
            Point p = data.get(i);
            sse += p.dist2(centers[assign[i]]);
        }
        return sse;
    }
}
