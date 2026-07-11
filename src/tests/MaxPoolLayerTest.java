import java.util.ArrayList;
import java.util.List;

public class MaxPoolLayerTest {
    public static void main(String[] args) {
        // Create a sample input
        List<List<List<Value>>> input = createSampleInput();

        // Print the input
        System.out.println("Input:");
        printInput(input);

        // Create a MaxpoolLayer instance
        int filterSize = 3;
        int padding = 1;
        int stride = 2;
        MaxpoolLayer maxpoolLayer = new MaxpoolLayer(filterSize, padding, stride);

        // Apply max pooling
        List<List<List<Value>>> output = maxpoolLayer.output(input);

        // Print the output
        System.out.println("\nOutput:");
        printInput(output);
    }

    private static List<List<List<Value>>> createSampleInput() {
        List<List<List<Value>>> input = new ArrayList<>();
        int channels = 2;
        int height = 5;
        int width = 5;

        for (int c = 0; c < channels; c++) {
            List<List<Value>> channel = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                List<Value> row = new ArrayList<>();
                for (int j = 0; j < width; j++) {
                    row.add(new Value((c * height * width) + (i * width + j + 1)));
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
                for (Value val : row) {
                    System.out.printf("%6.2f ", val.data);
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}