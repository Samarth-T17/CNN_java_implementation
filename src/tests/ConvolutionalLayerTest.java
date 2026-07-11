import java.util.*;

public class ConvolutionalLayerTest {
    public static void main(String[] args) {
        // Test parameters
        int inputSize = 4;
        int numberOfChannels = 2;
        int numberOfKernels = 3;  // Increased to 3 kernels
        int kernelSize = 2;
        int padding = 1;
        int stride = 2;

        // Create a convolutional layer
        ConvolutionalLayer convLayer = new ConvolutionalLayer(inputSize, numberOfChannels, numberOfKernels, kernelSize, padding, stride);
        convLayer.stride = stride;

        List<List<List<Value>>> input = createSampleInput(inputSize, numberOfChannels);

        // Print input
        System.out.println("Input:");
        printInput(input);

        // Print kernels
        System.out.println("\nKernels:");
        printKernels(convLayer.kernels);

        // Print bias
        System.out.println("\nBias:");
        printBias(convLayer.bias);

        // Compute output
        List<List<List<Value>>> output = convLayer.output(input);

        // Print output
        System.out.println("\nOutput:");
        printOutput(output);
    }

    private static List<List<List<Value>>> createSampleInput(int size, int channels) {
        List<List<List<Value>>> input = new ArrayList<>(channels);
        for (int c = 0; c < channels; c++) {
            List<List<Value>> channel = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                List<Value> row = new ArrayList<>(size);
                for (int j = 0; j < size; j++) {
                    row.add(new Value(i * size + j + 1));
                }
                channel.add(row);
            }
            input.add(channel);
        }
        return input;
    }

    private static void printInput(List<List<List<Value>>> input) {
        for (int c = 0; c < input.size(); c++) {
            System.out.println("Channel " + c + ":");
            for (List<Value> row : input.get(c)) {
                for (Value v : row) {
                    System.out.printf("%6.2f ", v.data);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private static void printKernels(List<List<List<List<Value>>>> kernels) {
        for (int k = 0; k < kernels.size(); k++) {
            System.out.println("Kernel " + k + ":");
            for (int c = 0; c < kernels.get(k).size(); c++) {
                System.out.println("Channel " + c + ":");
                for (List<Value> row : kernels.get(k).get(c)) {
                    for (Value v : row) {
                        System.out.printf("%6.2f ", v.data);
                    }
                    System.out.println();
                }
                System.out.println();
            }
        }
    }

    private static void printBias(List<Value> bias) {
        for (int i = 0; i < bias.size(); i++) {
            System.out.printf("Kernel %d bias: %6.2f\n", i, bias.get(i).data);
        }
    }

    private static void printOutput(List<List<List<Value>>> output) {
        for (int k = 0; k < output.size(); k++) {
            System.out.println("Output Channel " + k + ":");
            for (List<Value> row : output.get(k)) {
                for (Value v : row) {
                    System.out.printf("%6.2f ", v.data);
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}