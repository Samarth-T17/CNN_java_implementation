import java.util.ArrayList;
import java.util.List;

public class MaxpoolLayer {
    public int filterSize;
    public int padding;
    public int stride;
    MaxpoolLayer(int filterSize, int padding, int stride) {
        this.filterSize = filterSize;
        this.padding = padding;
        this.stride = stride;
    }
    public List<List<List<Value>>> output(List<List<List<Value>>> input) {
        input = PaddingUtil.addPadding(input, padding);
        int outDim = ((input.getFirst().size() - filterSize) / stride) + 1;
        List<List<List<Value>>> output = new ArrayList<>(input.size());

        for (int i = 0; i < input.size(); i++) {
            List<List<Value>> channel = new ArrayList<>(outDim);
            for (int j = 0; j < outDim; j++) {
                channel.add(new ArrayList<>(outDim));
            }
            output.add(channel);
        }

        for(int channelI = 0; channelI < input.size(); channelI++) {
            int outputI = 0;
            for(int inputI = 0; inputI + filterSize <= input.getFirst().size(); inputI += stride) {
                for(int inputJ = 0; inputJ  + filterSize <= input.getFirst().getFirst().size(); inputJ += stride) {
                    int startsJ = inputJ;
                    int startsI = inputI;
                    Value max = new Value(Integer.MIN_VALUE);
                    for(int i = 0; i < filterSize; i++, inputI++) {
                        for(int j = 0; j < filterSize; j++, inputJ++) {
                            if(max.data < input.get(channelI).get(inputI).get(inputJ).data) {
                                max = input.get(channelI).get(inputI).get(inputJ);
                            }
                        }
                        inputJ = startsJ;
                    }
                    inputI = startsI;
                    output.get(channelI).get(outputI).add(Value.noOpp(max));
                }
                outputI++;
            }
        }


        return output;
    }


}
