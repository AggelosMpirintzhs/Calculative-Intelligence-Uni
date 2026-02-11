package ask1;

import java.util.*;

public class Trainer {

    public static class TrainReport {
        public final int epochs;
        public final double finalTrainError;
        public TrainReport(int epochs, double finalTrainError) {
            this.epochs = epochs;
            this.finalTrainError = finalTrainError;
        }
    }

    public static TrainReport train(MLP model, List<LabeledExample> train,
                                    int batchSize, double lr, int minEpochs, double eps,
                                    int maxEpochs, Random rnd) {

        int N = train.size();
        if (N % batchSize != 0) throw new IllegalArgumentException("Batch size L must divide N.");

        double prevE = Double.POSITIVE_INFINITY;

        for (int epoch = 1; epoch <= maxEpochs; epoch++) {

            // shuffle each epoch
            Collections.shuffle(train, rnd);

            // mini-batches
            for (int start = 0; start < N; start += batchSize) {
                Gradients acc = new Gradients(Config.H1, Config.H2, Config.H3);

                for (int i = start; i < start + batchSize; i++) {
                    LabeledExample ex = train.get(i);
                    Gradients g = model.backprop(ex.x, ex.t);
                    acc.addInPlace(g);
                }

                // average gradient over batch
                acc.scaleInPlace(1.0 / batchSize);

                // gradient descent update
                model.applyUpdate(acc, lr);
            }

            // Βήμα 7
            // SUM ERROR
//            double E = 0.0;
//            for (LabeledExample ex : train) E += model.loss(ex.x, ex.t);

            // AVERAGE ERROR
            double E = 0.0;
            for (LabeledExample ex : train) E += model.loss(ex.x, ex.t);
            E /= train.size(); //


            System.out.printf(Locale.US, "epoch %4d: TrainError(E) = %.8f%n", epoch, E);

            // relative stop rule: epoch>=minEpochs AND |E - prevE| / max(|prevE|,1) < eps
            double denom = Math.max(Math.abs(prevE), 1.0);
            double relChange = Math.abs(E - prevE) / denom;

            // stop rule: epoch>=minEpochs AND |E - prevE| < eps
            if (epoch >= minEpochs && Math.abs(E - prevE) < eps) {
                return new TrainReport(epoch, E);
            }

            prevE = E;
        }

        // if reached maxEpochs
        double E = 0.0;
        for (LabeledExample ex : train) E += model.loss(ex.x, ex.t);
        //AVERAGE ERROR
        E /= train.size();
        return new TrainReport(maxEpochs, E);
    }
}
