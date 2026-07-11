import java.util.List;

@FunctionalInterface
public interface LossFunction {
    Value apply(List<List<Value>> yPred, List<Value> y);
}
