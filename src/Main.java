import java.util.List;
import java.util.function.Function;
import java.util.*;

class MLP extends NeuralNetwork {
    public MLP(int batchSize) {
        this.batchSize = batchSize;
        this.denseNN = new ArrayList<>();
        this.denseNN.add(new Layer(100, 4, ActivationFunctions::relu));
        this.denseNN.add(new Layer(180, 100, ActivationFunctions::relu));
        this.denseNN.add(new Layer(50, 180, ActivationFunctions::relu));
        this.denseNN.add(new Layer(3, 50, ActivationFunctions::softmax));
        storeParams();

        List<List<Value>> data = DataUtils.readCSV("cpp_dataset_species");
        List<DataUtils.SplitData> splitData = DataUtils.trainEvalTestSplit(data, batchSize);

        this.trainDataX = splitData.get(0).x;
        this.trainDataY = splitData.get(0).y;
        this.evalDataX = splitData.get(1).x;
        this.evalDataY = splitData.get(1).y;
        this.testDataX = splitData.get(2).x;
        this.testDataY = splitData.get(2).y;
    }

    @Override
    public Value forward(List<List<Value>> x, List<Value> y, LossFunction lossFunction) {
        List<List<Value>> predictions = predictBatch(x, true);
        return lossFunction.apply(predictions, y);
    }

    @Override
    public List<Value> predict(List<Value> x) {
        List<Value> out1 = denseNN.get(0).output(x);
        List<Value> out2 = denseNN.get(1).output(out1);
        List<Value> out3 = denseNN.get(2).output(out2);
        return denseNN.get(3).output(out3);
    }

    private void clipGradients(List<Value> parameters, double maxNorm) {
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

    private void temp(int epochs, int i, List<Double> hyperParameters) {
        System.out.println("epoch : " + i + " of " + epochs);
        int correctPredictions = 0;
        int totalPredictions = 0;

        for (int j = 0; j < evalDataX.size(); j++) {
            List<List<Value>> predictions = predictBatch(evalDataX.get(j), false);
            correctPredictions += Metrics.accuracy(predictions, evalDataY.get(j));
            totalPredictions += evalDataY.get(j).size();
        }

        for (int j = 0; j < trainDataX.size(); j++) {
            Value finalNode = forward(trainDataX.get(j), trainDataY.get(j), LossFunctions::crossEntropyLoss);
            List<Value> allNodes = Value.backPropagate(finalNode);
            clipGradients(parameters, 4.0);
            Optimization.stochasticGradientDescent(parameters, hyperParameters);
            zeroGrad();
        }

        System.out.println("correct predictions = " + correctPredictions + " total = " + totalPredictions);
        System.out.println("Eval accuracy = " + (correctPredictions * 100.0 / totalPredictions) + "%");
        System.out.println("-------------------------------------");
    }

    public void train(int epochs) {
        System.out.println("-------------------------------------Training started-------------------------------------\n");
        List<Double> hyperParameters = Collections.singletonList(0.1);
        for (int i = 0; i < epochs; i++) {
            temp(epochs, i, hyperParameters);
        }
    }

    public static void main(String[] args) {
        MLP model = new MLP(10);
        model.train(100);
    }
}