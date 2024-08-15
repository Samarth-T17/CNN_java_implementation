import java.util.*;
//import java.util.function.Function;
@SuppressWarnings("unused")
public class Value {
    public double data;
    public double grad;
    public List<Value> children;
    public Runnable backwardOp;

    public Value(double data) {
        this.data = data;
        this.grad = 0;
        this.children = null;
        this.backwardOp = null;
    }

    public Value(double data, List<Value> children) {
        this.data = data;
        this.grad = 0;
        this.children = children;
        this.backwardOp = null;
    }

    public static Value add(Value lhs, Value rhs) {
        List<Value> children = new ArrayList<>();
        children.add(lhs);
        children.add(rhs);
        Value out = new Value(lhs.data + rhs.data, children);
        out.backwardOp = () -> {
            lhs.grad += out.grad;
            rhs.grad += out.grad;
        };
        return out;
    }

    public static Value add1d(List<Value> elements) {
        double sum = 0;
        for (Value element : elements) {
            sum += element.data;
        }
        Value out = new Value(sum, elements);
        out.backwardOp = () -> {
            for (Value element : out.children) {
                element.grad += out.grad;
            }
        };
        return out;
    }

    public static Value mul(Value lhs, Value rhs) {
        List<Value> children = new ArrayList<>();
        children.add(lhs);
        children.add(rhs);
        Value out = new Value(lhs.data * rhs.data, children);
        out.backwardOp = () -> {
            lhs.grad += rhs.data * out.grad;
            rhs.grad += lhs.data * out.grad;
        };
        return out;
    }

    public static Value ePow(Value e) {
        List<Value> children = new ArrayList<>();
        children.add(e);
        Value out = new Value(Math.exp(e.data), children);
        out.backwardOp = () -> e.grad += out.data * out.grad;
        return out;
    }

    public static Value div(Value lhs, Value rhs) {
        Value denominator = pow(rhs, -1);
        return mul(lhs, denominator);
    }

    public static Value pow(Value e, double p) {
        List<Value> children = new ArrayList<>();
        children.add(e);
        double x = Math.pow(e.data, p);
        Value out = new Value(x, children);
        out.backwardOp = () -> e.grad += p * out.grad * x / e.data;
        return out;
    }

    public static Value log(Value e) {
        List<Value> children = new ArrayList<>();
        children.add(e);
        double x = e.data;
        Value out = new Value(Math.log(e.data), children);
        out.backwardOp = () -> e.grad += (1 / x) * out.grad;
        return out;
    }

    public static List<Value> backPropagate(Value finalNode) {
        Set<Value> visited = new HashSet<>();
        List<Value> order = new ArrayList<>();
        dfs(visited, order, finalNode);
        finalNode.grad = 1;
        for (int i = order.size() - 1; i >= 0; i--) {
            Value node = order.get(i);
            if (node.backwardOp != null) {
                node.backwardOp.run();
            }
        }
        return order;
    }

    public static void dfs(Set<Value> visited, List<Value> order, Value cur) {
        if (!visited.contains(cur)) {
            visited.add(cur);
            if (cur.children != null) {
                for (Value child : cur.children) {
                    dfs(visited, order, child);
                }
            }
            order.add(cur);
        }
    }
}
