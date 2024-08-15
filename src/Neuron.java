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
        for (int i = 0; i < inputSize; ++i) {
            double e = -1.0 + 2.0 * random.nextDouble(); // generates a random double between -1.0 and 1.0
            w.add(new Value(e));
        }
        b = new Value(-1.0 + 2.0 * random.nextDouble());
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
