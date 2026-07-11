import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")

public class Layer {
    private final List<Neuron> neurons;
    private final Function<List<Value>, List<Value>> activation;

    public Layer(int nNeurons, int nParams, Function<List<Value>, List<Value>> function) {
        neurons = new ArrayList<>();
        activation = function;
        for (int i = 0; i < nNeurons; i++) {
            neurons.add(new Neuron(nParams));
        }
    }

    public Layer(int nNeurons, int nParams) {
        this(nNeurons, nParams, null); // Default constructor without activation function
    }

    public List<Value> output(List<Value> x) {
        List<Value> weightedSum = new ArrayList<>();
        for (Neuron neuron : neurons) {
            weightedSum.add(neuron.output(x));
        }
        if (activation == null) {
            return weightedSum;
        }
        return activation.apply(weightedSum);
    }

    public List<Value> getParams() {
        List<Value> out = new ArrayList<>();
        for (Neuron neuron : neurons) {
            List<Value> params = neuron.getParams();
            out.addAll(params);
        }
        return out;
    }
}

