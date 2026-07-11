package models;

import cnn.ConvolutionalLayer;
import cnn.FlattenLayer;
import cnn.MaxpoolLayer;
import core.ActivationFunctions;
import core.Layer;
import core.LossFunctions;
import core.Metrics;
import core.Optimization;
import core.Value;
import data.DataUtilImages;
import data.ImageTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CNNFlowerModel {
    public ConvolutionalLayer conv1;
    public MaxpoolLayer max1;
    public ConvolutionalLayer conv2;
    public MaxpoolLayer max2;
    public Layer dense1;
    public Layer dense2;
    private final List<Value> parameters;
    private final List<List<ImageTuple>> trainBatches;
    private final List<List<ImageTuple>> evalBatches;
    private final List<List<ImageTuple>> testBatches;

    public CNNFlowerModel(int batchSize) {
        conv1 = new ConvolutionalLayer(32, 3, 6, 3, 1, 1);
        max1 = new MaxpoolLayer(2, 0, 2);
        conv2 = new ConvolutionalLayer(16, 6, 12, 3, 1, 1);
        max2 = new MaxpoolLayer(2, 0, 2);
        dense1 = new Layer(64, 12 * 8 * 8, ActivationFunctions::relu);
        dense2 = new Layer(5, 64, ActivationFunctions::softmax);

        parameters = new ArrayList<>();
        parameters.addAll(conv1.getParams());
        parameters.addAll(conv2.getParams());
        parameters.addAll(dense1.getParams());
        parameters.addAll(dense2.getParams());

        List<ImageTuple> data = DataUtilImages.loadDataset("flower_photos", 32);
        List<List<List<ImageTuple>>> splits = DataUtilImages.trainEvalTestSplit(data, batchSize);
        trainBatches = splits.get(0);
        evalBatches = splits.get(1);
        testBatches = splits.get(2);
    }

    public List<Value> predict(List<List<List<Value>>> image) {
        List<List<List<Value>>> out = ActivationFunctions.relu3D(conv1.output(image));
        out = max1.output(out);
        out = ActivationFunctions.relu3D(conv2.output(out));
        out = max2.output(out);
        List<Value> flat = FlattenLayer.output(out);
        return dense2.output(dense1.output(flat));
    }

    public Value forward(List<ImageTuple> batch) {
        List<List<Value>> predictions = new ArrayList<>();
        List<Value> y = new ArrayList<>();
        for (ImageTuple tuple : batch) {
            predictions.add(predict(tuple.image));
            y.add(new Value(tuple.classIndex));
        }
        return LossFunctions.crossEntropyLoss(predictions, y);
    }

    public double evaluate(List<List<ImageTuple>> batches) {
        int correct = 0;
        int total = 0;
        for (List<ImageTuple> batch : batches) {
            List<List<Value>> predictions = new ArrayList<>();
            List<Value> y = new ArrayList<>();
            for (ImageTuple tuple : batch) {
                predictions.add(predict(tuple.image));
                y.add(new Value(tuple.classIndex));
            }
            correct += Metrics.accuracy(predictions, y);
            total += batch.size();
        }
        return correct * 100.0 / total;
    }

    private void clipGradients(double maxNorm) {
        double totalNorm = 0.0;
        for (Value param : parameters) {
            totalNorm += param.grad * param.grad;
        }
        totalNorm = Math.sqrt(totalNorm);

        double clipCoef = maxNorm / (totalNorm + 1e-6);
        if (clipCoef < 1.0) {
            for (Value param : parameters) {
                param.grad *= clipCoef;
            }
        }
    }

    private void zeroGrad() {
        for (Value param : parameters) {
            param.grad = 0;
        }
    }

    public void train(int epochs, double learningRate) {
        System.out.println("-------------------------------------Training started-------------------------------------\n");
        for (int epoch = 0; epoch < epochs; epoch++) {
            List<Double> hyperParameters = Collections.singletonList(learningRate * Math.pow(0.95, epoch));
            double totalLoss = 0;
            for (List<ImageTuple> batch : trainBatches) {
                Value loss = forward(batch);
                Value.backPropagate(loss);
                clipGradients(4.0);
                Optimization.stochasticGradientDescent(parameters, hyperParameters);
                zeroGrad();
                totalLoss += loss.data;
            }
            System.out.println("epoch : " + epoch + " of " + epochs);
            System.out.println("avg log-likelihood = " + (totalLoss / trainBatches.size()));
            System.out.println("Eval accuracy = " + evaluate(evalBatches) + "%");
            System.out.println("-------------------------------------");
        }
    }

    public static void main(String[] args) {
        CNNFlowerModel model = new CNNFlowerModel(5);
        model.train(18, 0.05);
        System.out.println("Test accuracy = " + model.evaluate(model.testBatches) + "%");
    }
}
