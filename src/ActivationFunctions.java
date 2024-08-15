import java.util.*;
//import java.util.function.*;
@SuppressWarnings("unused")

public class ActivationFunctions {
    public static List<Value> tanh(List<Value> e) {
        List<Value> out = new ArrayList<>();
        for (Value x : e) {
            List<Value> children = new ArrayList<>();
            children.add(x);
            double temp = Math.exp(2 * x.data);
            final double val = (temp - 1) / (temp + 1);
            Value y = new Value(val, children);
            y.backwardOp = () -> x.grad += (1 - val * val) * y.grad;
            out.add(y);
        }
        return out;
    }

    public static List<Value> relu(List<Value> e) {
        List<Value> out = new ArrayList<>();
        for (Value x : e) {
            List<Value> children = new ArrayList<>();
            children.add(x);
            double val = x.data > 0 ? x.data : 0;
            Value y = new Value(val, children);
            y.backwardOp = () -> x.grad += val != 0 ? y.grad : 0;
            out.add(y);
        }
        return out;
    }

    public static List<Value> sigmoid(List<Value> e) {
        List<Value> out = new ArrayList<>();
        for (Value x : e) {
            List<Value> children = new ArrayList<>();
            children.add(x);
            double val = 1 / (1 + Math.exp(-1 * x.data));
            Value y = new Value(val, children);
            y.backwardOp = () -> x.grad += val * (1 - val) * y.grad;
            out.add(y);
        }
        return out;
    }

    public static List<Value> softmax(List<Value> e) {
        List<Value> exp = new ArrayList<>();
        for (Value x : e) {
            exp.add(Value.ePow(x));
        }
        Value sum = Value.add1d(exp);
        List<Value> out = new ArrayList<>();
        for (Value x : exp) {
            out.add(Value.div(x, sum));
        }
        return out;
    }
}