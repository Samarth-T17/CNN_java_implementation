import java.util.*;
//import java.util.function.Function;

@SuppressWarnings("unused")

public abstract class NeuralNetwork {
    protected List<Layer> denseNN;
    protected List<Value> parameters;
    protected int batchSize;
    protected List<List<List<Value>>> trainDataX;
    protected List<List<Value>> trainDataY;
    protected List<List<List<Value>>> evalDataX;
    protected List<List<Value>> evalDataY;
    protected List<List<List<Value>>> testDataX;
    protected List<List<Value>> testDataY;

    public NeuralNetwork() {
        this.denseNN = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    public void storeParams() {
        parameters.clear();
        for (Layer layer : denseNN) {
            List<Value> layerParams = layer.getParams();
            parameters.addAll(layerParams);
        }
    }

    public abstract Value forward(List<List<Value>> x, List<Value> y, LossFunction lossFunction);

    public abstract List<Value> predict(List<Value> x);

    public List<List<Value>> predictBatch(List<List<Value>> x, boolean inTraining) {
        List<List<Value>> out = new ArrayList<>();
        for (List<Value> tuple : x) {
            List<Value> predictions = predict(tuple);
            out.add(predictions);
        }
        return out;
    }

    public void zeroGrad() {
        for (Value param : parameters) {
            param.grad = 0;
        }
    }

    public List<Value> getParams() {
        return parameters;
    }

    public static void backPropagate(Value lastNode) {
        Value.backPropagate(lastNode);
    }

    // Optional: Implement a method for testing backpropagation if needed.
}

