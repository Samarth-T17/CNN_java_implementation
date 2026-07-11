package cnn;

import core.Value;

import java.util.*;

public class FlattenLayer {
    public static List<Value> output(List<List<List<Value>>> input) {
        List<Value> output = new ArrayList<>();
        for (List<List<Value>> row : input) {
            for (List<Value> column : row) {
                for(Value value : column) {
                    output.add(Value.noOpp(value));
                }
            }
        }
        return output;
    }
}
