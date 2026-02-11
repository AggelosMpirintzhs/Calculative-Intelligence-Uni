package ask1;

import java.util.List;

public class Evaluator {

    public static double accuracy(MLP model, List<LabeledExample> test) {
        int correct = 0;
        for (LabeledExample ex : test) {
            int pred = model.predictClass(ex.x);
            if (pred == ex.label) correct++;
        }
        return correct / (double) test.size();
    }
}
