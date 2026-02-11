package ask1;

public class Config {
    // Βήμα 1: d=2, K=4
    public static final int D = 2;
    public static final int K = 4;

    // Βήμα 1: H1,H2,H3 σαν #define
    public static final int H1 = 20;
    public static final int H2 = 20;
    public static final int H3 = 12;

    // Exists in ExperimentMain
    //public static final double LR = 0.05;
    //public static final double LR = 0.01;


    // Βήμα 7: stop condition
    public static final int MIN_EPOCHS = 800;
    //    public static final double EPS = 1e-6; too many epochs
    public static final double EPS = 1e-5;
    // FOR SUM EPS
    //public static final double EPS = 0,1;



    // Βήμα 6: max epochs safety
    public static final int MAX_EPOCHS = 5000;

    // ΠΡΟΣΩΡΙΝΟ DEBUG MODE
//    public static final int MIN_EPOCHS = 50;
//    public static final int MAX_EPOCHS = 200;
//    public static final double EPS = 1e-2;

}
