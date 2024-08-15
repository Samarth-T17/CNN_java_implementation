import java.util.List;

@SuppressWarnings("unused")

public class Metrics {
    public static int accuracy(List<List<Value>> yPred, List<Value> y) {
        int correctPredictions = 0;
        for (int i = 0; i < y.size(); i++) {
            List<Value> probabilities = yPred.get(i);
            double maxProbability = -1;
            int yIndex = -1;
            double isSum1 = 0;
            for (int j = 0; j < probabilities.size(); j++) {
                isSum1 += probabilities.get(j).data;
                if (maxProbability < probabilities.get(j).data) {
                    maxProbability = probabilities.get(j).data;
                    yIndex = j;
                }
            }
            // System.out.println("Sum: " + isSum1);
            // System.out.println("Model Prediction: " + yIndex + ", Correct Prediction: " + y.get(i).data);
            if (yIndex == (int) y.get(i).data) {
                correctPredictions++;
            }
        }
        return correctPredictions;
    }
}
