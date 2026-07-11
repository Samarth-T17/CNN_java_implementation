import java.util.List;

@SuppressWarnings("unused")

public class Optimization {
    public static void stochasticGradientDescent(List<Value> parameters, List<Double> hyperParameters) {
        double learningRate = hyperParameters.getFirst();
        for (Value param : parameters) {
            param.data += learningRate * param.grad;
        }
    }
}