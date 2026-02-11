package ask1;

public class LabeledExample {
    public final double[] x;   // length 2
    public final int label;    // 0..3
    public final double[] t;

    public LabeledExample(double x1, double x2, int label0to3) {
        this.x = new double[]{x1, x2};
        this.label = label0to3;
        this.t = new double[Config.K];
        this.t[label0to3] = 1.0;
    }
}
