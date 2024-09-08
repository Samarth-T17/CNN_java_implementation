import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")

public class ConvolutionalLayer {
    public List<List<List<List<Value>>>> kernels;
    public List<Value> bias;
    public int padding;
    public int stride;
    private static final Random random = new Random();
    ConvolutionalLayer(int inputSize, int numberOfChannels, int numberOfKernels, int kernelSize, int padding, int stride) {
        this.padding = padding;
        this.stride = stride;
        int fanIn = (inputSize + 2 * padding) * (inputSize + 2 * padding) * numberOfChannels;
        int fanOut = numberOfKernels * (inputSize + 2 * padding) * (inputSize + 2 * padding);
        double stddev = Math.sqrt(2.0 / (fanIn + fanOut));

        kernels = new ArrayList<>(numberOfKernels);

        for (int n = 0; n < numberOfKernels; n++) {
            List<List<List<Value>>> kernel = new ArrayList<>(numberOfChannels);
            for (int c = 0; c < numberOfChannels; c++) {
                List<List<Value>> channel = new ArrayList<>(kernelSize);
                for (int h = 0; h < kernelSize; h++) {
                    List<Value> row = new ArrayList<>(kernelSize);
                    for (int w = 0; w < kernelSize; w++) {
                        row.add(new Value(2));
                        //random.nextGaussian() * stddev
                    }
                    channel.add(row);
                }
                kernel.add(channel);
            }
            kernels.add(kernel);
        }
        bias = new ArrayList<>(numberOfKernels);
        for(int n = 0; n < numberOfKernels; n++) {
            bias.add(new Value(random.nextGaussian() * stddev));
        }
    }


    public List<List<List<Value>>> output(List<List<List<Value>>> input) {
        int outDim = getOutDim(input.getFirst().size());
        input = PaddingUtil.addPadding(input, padding);
        List<List<List<Value>>> output = new ArrayList<>(kernels.size());

        for (int i = 0; i < kernels.size(); i++) {
            List<List<Value>> channel = new ArrayList<>(outDim);
            for (int j = 0; j < outDim; j++) {
                channel.add(new ArrayList<>(outDim));
            }
            output.add(channel);
        }

        for(int i = 0; i < kernels.size(); i++) {
            // output's ith channel is initialised
            List<List<List<Value>>> kernel = kernels.get(i);
            int outputI = 0;
            for(int inputI = 0; inputI + kernel.getFirst().size() <= input.getFirst().size(); inputI += stride) {
                for(int inputJ = 0; inputJ  + kernel.getFirst().size() <= input.getFirst().getFirst().size(); inputJ += stride) {
                    List<Value> products = new ArrayList<>();
                    give_products(input, kernel, inputI, inputJ, products);
                    products.add(bias.get(i));
                    output.get(i).get(outputI).add(Value.add1d(products));
                }
                outputI++;
            }
        }
        return output;
    }

    void give_products(List<List<List<Value>>> input, List<List<List<Value>>> kernel, int inputI, int inputJ, List<Value> products) {
        int startsJ = inputJ;
        for(int i = 0; i < kernel.getFirst().size(); i++, inputI++) {
            inputJ = startsJ;
            for(int j = 0; j < kernel.getFirst().get(i).size(); j++, inputJ++) {
                for(int k = 0; k < kernel.size(); k++) {
                    products.add(Value.mul(input.get(k).get(inputI).get(inputJ), kernel.get(k).get(i).get(j)));
                }
            }
        }
    }

    int getOutDim(int inSize) {
        return ((inSize + 2 * padding - kernels.getFirst().getFirst().size()) / stride) + 1;
    }
}
