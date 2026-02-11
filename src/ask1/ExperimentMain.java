package ask1;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class ExperimentMain {

    public static void main(String[] args) throws Exception {

        String trainPath = (args.length > 0) ? args[0] : "sdt_train.csv";
        String testPath  = (args.length > 1) ? args[1] : "sdt_test.csv";

        List<LabeledExample> train = SDTLoader.load(trainPath);
        List<LabeledExample> test  = SDTLoader.load(testPath);

        System.out.println("Train size = " + train.size());
        System.out.println("Test  size = " + test.size());


        int N = train.size();

        // Βήμα 6: L in {N/10, N/20, N/100, N/200}
        int[] Ls = {N/10, N/20, N/100, N/200};

        // Βήμα 2: 3ο hidden: tanh/logistic/relu (τα 2 πρώτα είναι tanh μέσα στο MLP)
        Activation[] act3s = {Activation.TANH, Activation.LOGISTIC, Activation.RELU};

//        Activation[] act3s = {Activation.TANH}; // μόνο 1
//        int[] Ls = {N/20};                      // μόνο 1


        System.out.println("WD = " + new java.io.File(".").getAbsolutePath());
        System.out.println("results path = " + new java.io.File("ask1_results.csv").getAbsolutePath());
        System.out.println("marks   path = " + new java.io.File("ask1_best_test_marks.csv").getAbsolutePath());


        try (PrintWriter results = new PrintWriter(new FileWriter("ask1_results.csv"))) {
            results.println("H1,H2,H3,act3,L,epochs,train_error,test_acc");

            double bestAcc = Double.NEGATIVE_INFINITY;
            String bestDesc = "";
            MLP bestModel = null;

            for (Activation act3 : act3s) {
                for (int L : Ls) {

                    //Learning Rate μεταβαλλομενο
                    double lr;
                    if (L >= 400) lr = 0.03;
                    else if (L >= 200) lr = 0.02;
                    else if (L >= 40)  lr = 0.005;
                    else               lr = 0.002;

                    Random rnd = new Random(12345L + L * 31L + act3.ordinal() * 997L);

                    MLP model = new MLP(Config.H1, Config.H2, Config.H3, act3, rnd);

                    Trainer.TrainReport rep = Trainer.train(
                            model, train, L,
                            lr,
                            Config.MIN_EPOCHS, Config.EPS,
                            Config.MAX_EPOCHS, rnd
                    );

                    double acc = Evaluator.accuracy(model, test);

                    results.printf(Locale.US, "%d,%d,%d,%s,%d,%d,%.10f,%.6f%n",
                            Config.H1, Config.H2, Config.H3, act3.name(), L,
                            rep.epochs, rep.finalTrainError, acc
                    );


                    System.out.printf(Locale.US,
                            "DONE: act3=%s L=%d lr=%.4f -> epochs=%d TrainE=%.6f TestAcc=%.4f%n",
                            act3.name(), L, lr, rep.epochs, rep.finalTrainError, acc
                    );


                    if (acc > bestAcc) {
                        bestAcc = acc;
                        bestDesc = "act3=" + act3.name() + " L=" + L;
                        bestModel = model;
                    }

                }

//                    Random rnd = new Random(12345L + L * 31L + act3.ordinal() * 997L);
//
//                    // Βήμα 1: αρχιτεκτονική
//                    MLP model = new MLP(Config.H1, Config.H2, Config.H3, act3, rnd);
//
//                    // Βήμα 6-7: train with mini-batches & stop
//                    Trainer.TrainReport rep = Trainer.train(
//                            model, train, L,
//                            Config.LR, Config.MIN_EPOCHS, Config.EPS,
//                            Config.MAX_EPOCHS, rnd
//                    );
//
//                    // Βήμα 7: test accuracy
//                    double acc = Evaluator.accuracy(model, test);
//
//                    results.printf(Locale.US, "%d,%d,%d,%s,%d,%d,%.10f,%.6f%n",
//                            Config.H1, Config.H2, Config.H3, act3.name(), L,
//                            rep.epochs, rep.finalTrainError, acc
//                    );
//
//                    System.out.printf(Locale.US,
//                            "DONE: act3=%s L=%d -> epochs=%d TrainE=%.6f TestAcc=%.4f%n",
//                            act3.name(), L, rep.epochs, rep.finalTrainError, acc
//                    );
//
//                    if (acc > bestAcc) {
//                        bestAcc = acc;
//                        bestDesc = "act3=" + act3.name() + " L=" + L;
//                        bestModel = model;
//                    }
//                }
            }

            System.out.println("\nBEST: " + bestDesc + " with TestAcc=" + bestAcc);

            // Προαιρετικό για report (+/-): γράφει αν κάθε test point είναι σωστό
            if (bestModel != null) {
                double sanity = Evaluator.accuracy(bestModel, test);
                System.out.printf(Locale.US, "Sanity BEST accuracy = %.6f (should equal bestAcc=%.6f)%n", sanity, bestAcc);

                saveTestMarks("ask1_best_test_marks.csv", bestModel, test);
                System.out.println("Saved ask1_best_test_marks.csv (+/- helper).");
            }
        }
    }

    private static void saveTestMarks(String filename, MLP model, List<LabeledExample> test) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("x1,x2,correct"); // correct=1 => '+' , correct=0 => '-'
            for (LabeledExample ex : test) {
                int pred = model.predictClass(ex.x);
                int correct = (pred == ex.label) ? 1 : 0;
                pw.printf(Locale.US, "%.8f,%.8f,%d%n", ex.x[0], ex.x[1], correct);
            }
        }
    }
}
