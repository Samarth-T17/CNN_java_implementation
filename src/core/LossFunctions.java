import java.util.ArrayList;
import java.util.List;

public class LossFunctions {
    public static Value crossEntropyLoss(List<List<Value>> yPred, List<Value> y) {
        List<Value> probabilities = new ArrayList<>();
        for (int i = 0; i < yPred.size(); i++) {
            probabilities.add(yPred.get(i).get((int) y.get(i).data));
        }
        List<Value> loss = new ArrayList<>();
        for (Value probability : probabilities) {
            loss.add(Value.log(probability));
        }
        
        Value totalLoss = Value.add1d(loss);

        // System.out.println("Average Loss: " + averageLoss.getData());
        return Value.div(totalLoss, new Value(y.size()));
    }
}
