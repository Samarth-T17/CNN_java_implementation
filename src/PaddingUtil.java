import java.util.ArrayList;
import java.util.List;

public class PaddingUtil {
    static public List<List<List<Value>>> addPadding(List<List<List<Value>>> input, int padding) {
        int channels = input.size();
        int size = input.getFirst().size();
        System.out.println("Padding : " + padding);
        List<List<List<Value>>> paddedOutput = new ArrayList<>(channels);
        for (int c = 0; c < channels; c++) {
            List<List<Value>> paddedChannel = new ArrayList<>(size + 2 * padding);
            for (int i = 0; i < padding; i++) {
                paddedChannel.add(createPaddedRow(size + 2 * padding));
            }
            for (List<Value> row : input.get(c)) {
                List<Value> paddedRow = new ArrayList<>(size + 2 * padding);
                for (int i = 0; i < padding; i++) {
                    paddedRow.add(new Value(0));
                }
                paddedRow.addAll(row);
                for (int i = 0; i < padding; i++) {
                    paddedRow.add(new Value(0));
                }
                paddedChannel.add(paddedRow);
            }
            for (int i = 0; i < padding; i++) {
                paddedChannel.add(createPaddedRow(size + 2 * padding));
            }
            paddedOutput.add(paddedChannel);
        }
        return paddedOutput;
    }

    static private List<Value> createPaddedRow(int size) {
        List<Value> paddedRow = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            paddedRow.add(new Value(0));
        }
        return paddedRow;
    }
}
