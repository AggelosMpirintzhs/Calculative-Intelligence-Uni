package ask1;

public class Gradients {
    public final double[][] dW1, dW2, dW3, dW4;
    public final double[] db1, db2, db3, db4;

    public Gradients(int h1, int h2, int h3) {
        dW1 = new double[h1][Config.D];
        dW2 = new double[h2][h1];
        dW3 = new double[h3][h2];
        dW4 = new double[Config.K][h3];

        db1 = new double[h1];
        db2 = new double[h2];
        db3 = new double[h3];
        db4 = new double[Config.K];
    }

    public void addInPlace(Gradients g) {
        addMat(dW1, g.dW1); addMat(dW2, g.dW2); addMat(dW3, g.dW3); addMat(dW4, g.dW4);
        addVec(db1, g.db1); addVec(db2, g.db2); addVec(db3, g.db3); addVec(db4, g.db4);
    }

    public void scaleInPlace(double s) {
        scaleMat(dW1, s); scaleMat(dW2, s); scaleMat(dW3, s); scaleMat(dW4, s);
        scaleVec(db1, s); scaleVec(db2, s); scaleVec(db3, s); scaleVec(db4, s);
    }

    private static void addVec(double[] a, double[] b) { for (int i=0;i<a.length;i++) a[i]+=b[i]; }
    private static void scaleVec(double[] a, double s) { for (int i=0;i<a.length;i++) a[i]*=s; }

    private static void addMat(double[][] A, double[][] B) {
        for (int i=0;i<A.length;i++) for (int j=0;j<A[i].length;j++) A[i][j]+=B[i][j];
    }
    private static void scaleMat(double[][] A, double s) {
        for (int i=0;i<A.length;i++) for (int j=0;j<A[i].length;j++) A[i][j]*=s;
    }
}
