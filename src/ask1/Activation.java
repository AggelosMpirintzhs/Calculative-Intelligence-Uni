package ask1;

public enum Activation {
    TANH {
        @Override public double f(double u) { return Math.tanh(u); }
        @Override public double df(double u) {
            double t = Math.tanh(u);
            return 1.0 - t * t;
        }
    },
    LOGISTIC {
        @Override public double f(double u) { return 1.0 / (1.0 + Math.exp(-u)); }
        @Override public double df(double u) {
            double s = 1.0 / (1.0 + Math.exp(-u));
            return s * (1.0 - s);
        }
    },
    RELU {
        @Override public double f(double u) { return Math.max(0.0, u); }
        @Override public double df(double u) { return (u > 0.0) ? 1.0 : 0.0; }
    };

    public abstract double f(double u);
    public abstract double df(double u);
}
