package ask2;

public class KMeansResult {
    public final Point[] centers;   // size k
    public final int[] assignment;  // size N, cluster id per point
    public final double sse;        // sum of squared errors

    public KMeansResult(Point[] centers, int[] assignment, double sse) {
        this.centers = centers;
        this.assignment = assignment;
        this.sse = sse;
    }
}
