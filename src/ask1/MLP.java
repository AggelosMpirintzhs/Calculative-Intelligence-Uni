package ask1;

import java.util.Random;

public class MLP {

    // Βήμα 2: hidden1=tanh, hidden2=tanh, hidden3=παράμετρος
    private final Activation act1 = Activation.TANH;
    private final Activation act2 = Activation.TANH;
    private final Activation act3;

    private final int h1, h2, h3;

    // Weights & biases
    public final double[][] W1, W2, W3, W4;
    public final double[] b1, b2, b3, b4;

    public MLP(int h1, int h2, int h3, Activation act3, Random rnd) {
        this.h1 = h1; this.h2 = h2; this.h3 = h3;
        this.act3 = act3;

        // Βήμα 1: 2–H1–H2–H3–4
        W1 = new double[h1][Config.D];
        W2 = new double[h2][h1];
        W3 = new double[h3][h2];
        W4 = new double[Config.K][h3];

        b1 = new double[h1];
        b2 = new double[h2];
        b3 = new double[h3];
        b4 = new double[Config.K];

        // Βήμα 3: init στο [-1,1]
        initUniformMinus1Plus1(rnd);
    }

    private void initUniformMinus1Plus1(Random rnd) {
        fillMat(W1, rnd); fillMat(W2, rnd); fillMat(W3, rnd); fillMat(W4, rnd);
        fillVec(b1, rnd); fillVec(b2, rnd); fillVec(b3, rnd); fillVec(b4, rnd);
    }

    private static void fillVec(double[] v, Random rnd) {
        for (int i=0;i<v.length;i++) v[i] = -1.0 + 2.0 * rnd.nextDouble();
    }
    private static void fillMat(double[][] m, Random rnd) {
        for (int i=0;i<m.length;i++) for (int j=0;j<m[i].length;j++)
            m[i][j] = -1.0 + 2.0 * rnd.nextDouble();
    }

    //Βήμα 4: κρατάμε u και activations
    public static class Cache {
        public final double[] u1, a1, u2, a2, u3, a3, u4, y;
        Cache(int h1, int h2, int h3) {
            u1=new double[h1]; a1=new double[h1];
            u2=new double[h2]; a2=new double[h2];
            u3=new double[h3]; a3=new double[h3];
            u4=new double[Config.K]; y=new double[Config.K];
        }
    }

    // Βήμα 4: forward pass
    public Cache forward(double[] x) {
        Cache c = new Cache(h1,h2,h3);

        affine(W1, b1, x, c.u1);
        apply(act1, c.u1, c.a1);

        affine(W2, b2, c.a1, c.u2);
        apply(act2, c.u2, c.a2);

        affine(W3, b3, c.a2, c.u3);
        apply(act3, c.u3, c.a3);

        affine(W4, b4, c.a3, c.u4);

        // Βήμα 2 ii: softmax για 4 κλάσεις
        softmax(c.u4, c.y);

        return c;
    }

    // Βήμα 5: backprop gradients για όλα W,b
    public Gradients backprop(double[] x, double[] t) {
        Cache c = forward(x);

        // delta4: softmax + cross-entropy => y - t
        double[] d4 = new double[Config.K];
        for (int k=0;k<Config.K;k++) d4[k] = c.y[k] - t[k];

        // dW4, db4
        Gradients g = new Gradients(h1,h2,h3);
        for (int k=0;k<Config.K;k++) {
            g.db4[k] += d4[k];
            for (int j=0;j<h3;j++) g.dW4[k][j] += d4[k] * c.a3[j];
        }

        // delta3 = (W4^T d4) * f3'(u3)
        double[] d3 = new double[h3];
        for (int j=0;j<h3;j++) {
            double s = 0.0;
            for (int k=0;k<Config.K;k++) s += W4[k][j] * d4[k];
            d3[j] = s * act3.df(c.u3[j]);
        }
        for (int j=0;j<h3;j++) {
            g.db3[j] += d3[j];
            for (int i=0;i<h2;i++) g.dW3[j][i] += d3[j] * c.a2[i];
        }

        // delta2 = (W3^T d3) * tanh'(u2)
        double[] d2 = new double[h2];
        for (int i=0;i<h2;i++) {
            double s = 0.0;
            for (int j=0;j<h3;j++) s += W3[j][i] * d3[j];
            d2[i] = s * act2.df(c.u2[i]);
        }
        for (int i=0;i<h2;i++) {
            g.db2[i] += d2[i];
            for (int p=0;p<h1;p++) g.dW2[i][p] += d2[i] * c.a1[p];
        }

        // delta1 = (W2^T d2) * tanh'(u1)
        double[] d1 = new double[h1];
        for (int p=0;p<h1;p++) {
            double s = 0.0;
            for (int i=0;i<h2;i++) s += W2[i][p] * d2[i];
            d1[p] = s * act1.df(c.u1[p]);
        }
        for (int p=0;p<h1;p++) {
            g.db1[p] += d1[p];
            for (int d=0; d<Config.D; d++) g.dW1[p][d] += d1[p] * x[d];
        }

        return g;
    }

    public int predictClass(double[] x) {
        Cache c = forward(x);
        int best = 0;
        for (int k=1;k<Config.K;k++) if (c.y[k] > c.y[best]) best = k;
        return best;
    }

    // Cross-entropy loss
    public double loss(double[] x, double[] t) {
        Cache c = forward(x);
        double s = 0.0;
        for (int k=0;k<Config.K;k++) {
            if (t[k] > 0.5) s -= Math.log(Math.max(1e-12, c.y[k]));
        }
        return s;
    }

    public void applyUpdate(Gradients g, double lr) {
        subMatInPlace(W1, g.dW1, lr); subVecInPlace(b1, g.db1, lr);
        subMatInPlace(W2, g.dW2, lr); subVecInPlace(b2, g.db2, lr);
        subMatInPlace(W3, g.dW3, lr); subVecInPlace(b3, g.db3, lr);
        subMatInPlace(W4, g.dW4, lr); subVecInPlace(b4, g.db4, lr);
    }

    private static void affine(double[][] W, double[] b, double[] in, double[] outU) {
        for (int i=0;i<W.length;i++) {
            double s = b[i];
            for (int j=0;j<W[i].length;j++) s += W[i][j] * in[j];
            outU[i] = s;
        }
    }
    private static void apply(Activation a, double[] u, double[] out) {
        for (int i=0;i<u.length;i++) out[i] = a.f(u[i]);
    }
    private static void softmax(double[] u, double[] y) {
        double max = u[0];
        for (int i=1;i<u.length;i++) if (u[i] > max) max = u[i];
        double sum = 0.0;
        for (int i=0;i<u.length;i++) { y[i] = Math.exp(u[i] - max); sum += y[i]; }
        for (int i=0;i<u.length;i++) y[i] /= sum;
    }

    private static void subVecInPlace(double[] w, double[] gw, double lr) {
        for (int i=0;i<w.length;i++) w[i] -= lr * gw[i];
    }
    private static void subMatInPlace(double[][] W, double[][] gW, double lr) {
        for (int i=0;i<W.length;i++) for (int j=0;j<W[i].length;j++) W[i][j] -= lr * gW[i][j];
    }
}
