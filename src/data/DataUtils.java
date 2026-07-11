import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")

public class DataUtils {
    public static class SplitData {
        public List<List<List<Value>>> x;
        public List<List<Value>> y;

        public SplitData(List<List<List<Value>>> x, List<List<Value>> y) {
            this.x = x;
            this.y = y;
        }
    }

    public static SplitData splitXy(List<List<List<Value>>> data) {
        List<List<List<Value>>> x = new ArrayList<>();
        List<List<Value>> y = new ArrayList<>();

        for (List<List<Value>> batch : data) {
            List<Value> batchY = new ArrayList<>();
            List<List<Value>> batchX = new ArrayList<>();

            for (List<Value> tuple : batch) {
                batchY.add(tuple.getLast());
                batchX.add(new ArrayList<>(tuple.subList(0, tuple.size() - 1)));
            }

            x.add(batchX);
            y.add(batchY);
        }

        return new SplitData(x, y);
    }

    public static List<List<List<Value>>> divideIntoBatches(List<List<Value>> data, int batchSize) {
        List<List<List<Value>>> segmentedData = new ArrayList<>();
        int dataSize = data.size();

        for (int i = 0; i < dataSize; ) {
            List<List<Value>> newBatch = new ArrayList<>();
            for (int j = 0; j < batchSize && i < dataSize; j++, i++) {
                newBatch.add(data.get(i));
            }
            segmentedData.add(newBatch);
        }
        return segmentedData;
    }

    public static List<SplitData> trainEvalTestSplit(List<List<Value>> data, int batchSize) {
        List<List<Value>> shuffledData = new ArrayList<>(data);
        Collections.shuffle(shuffledData, new Random());

        int totalSize = shuffledData.size();
        int firstSplit = (int) (totalSize * 0.7);
        int secondSplit = firstSplit + (int) (totalSize * 0.3);

        List<List<Value>> trainSet = shuffledData.subList(0, firstSplit);
        List<List<Value>> evalSet = shuffledData.subList(firstSplit, secondSplit);
        List<List<Value>> testSet = shuffledData.subList(secondSplit, totalSize);

        List<SplitData> segmentedData = new ArrayList<>();
        segmentedData.add(splitXy(divideIntoBatches(trainSet, batchSize)));
        segmentedData.add(splitXy(divideIntoBatches(evalSet, batchSize)));
        segmentedData.add(splitXy(divideIntoBatches(testSet, batchSize)));

        return segmentedData;
    }

    public static List<List<Value>> readCSV(String filename) {
        List<List<Value>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<Value> row = new ArrayList<>();
                String[] values = line.split(",");
                for (String value : values) {
                    row.add(new Value(Double.parseDouble(value)));
                }
                data.add(row);
            }
        } catch (IOException e) {
            System.err.println("Could not open the file " + filename);
        }
        return data;
    }
}