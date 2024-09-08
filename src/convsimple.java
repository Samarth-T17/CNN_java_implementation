import java.util.ArrayList;
import java.util.List;

public class convsimple {
    List<List<Value>> kernel;
    void output(List<List<Value>> input, int stride, int padding) {
        List<List<Value>> output = new ArrayList<>();
        for(int i = 0; i < input.size(); i++) {
            output.add(new ArrayList<>());
        }
        int outputI = 0;
        int outDim = getOutDim(input.size(), kernel.size(), padding, stride);
        for(int i = 0; i < outDim; i++) {
            output.add(new ArrayList<>());
        }
        for(int inputI = 0; inputI < outDim; inputI += stride) {
            List<Value> products = new ArrayList<>();
            for(int inputJ = 0; inputJ < outDim; inputJ += stride) {
                give_products(input, kernel, inputI, inputJ, products);
                output.get(outputI).add(Value.add1d(products));
            }
            outputI++;
        }
    }
    void give_products(List<List<Value>> input, List<List<Value>> kernel, int inputI, int inputJ, List<Value> products) {
        int startsJ = inputJ;
        for(int i = 0; i < kernel.size(); i++, inputI++) {
            for(int j = 0; j < kernel.get(i).size(); j++, inputJ++) {
                products.add(Value.mul(input.get(inputI).get(inputJ), kernel.get(i).get(j)));
            }
            inputJ = startsJ;
        }
    }
    int getOutDim(int inSize, int kernelSize, int padding, int stride) {
        return ((inSize + 2 * padding - kernelSize) / stride) + 1;
    }

}
