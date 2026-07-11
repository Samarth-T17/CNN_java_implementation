package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")

public class Neuron {
    private final List<Value> w;
    private final Value b;

    public Neuron(int inputSize) {
        w = new ArrayList<>();
        Random random = new Random();
        double stddev = Math.sqrt(2.0 / inputSize);
        for (int i = 0; i < inputSize; ++i) {
            w.add(new Value(random.nextGaussian() * stddev));
        }
        b = new Value(0);
    }

    public Value output(List<Value> x) {
        List<Value> products = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            products.add(Value.mul(x.get(i), w.get(i)));
        }
        products.add(b);
        return Value.add1d(products);
    }

    public List<Value> getParams() {
        List<Value> out = new ArrayList<>(w);
        out.add(b);
        return out;
    }
}
