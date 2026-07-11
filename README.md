# Neural Networks from Scratch in Java

A scalar autograd engine with MLP and CNN implementations built on top of it. No external dependencies — the only libraries used are `java.util` and `javax.imageio`.

## How it works

Everything is built on `core/Value.java`, a scalar autograd node (in the spirit of micrograd). Each `Value` holds a `data` scalar, a `grad`, its child nodes, and a backward closure. `Value.backPropagate` topologically sorts the computation graph and runs the backward ops in reverse, accumulating gradients. Every layer type — dense, convolutional, maxpool — is composed of these scalar operations, so gradients flow through the entire network automatically.

## Project structure

```
src/
├── core/      autograd engine + dense NN building blocks
│              Value, Neuron, Layer, NeuralNetwork, ActivationFunctions,
│              LossFunction, LossFunctions, Optimization, Metrics
├── cnn/       ConvolutionalLayer, MaxpoolLayer, FlattenLayer, PaddingUtil
├── data/      DataUtils (CSV), DataUtilImages (image loading), ImageTuple
├── models/    MLP, CNNFlowerModel
└── tests/     ConvolutionalLayerTest, MaxPoolLayerTest
```

## Build

```bash
cd src
javac -d ../out */*.java
cd ..
```

Run all commands below from the project root so the models find their datasets.

## MLP

A 4-layer dense network (4 → 100 → 180 → 50 → 3, relu + softmax) trained on the species CSV dataset (`cpp_dataset_species`, included in the repo): 150 samples, 4 features, 3 classes.

```bash
java -cp out models.MLP
```

Trains with SGD, gradient clipping, and cross-entropy loss. Reaches ~90%+ eval accuracy within a few epochs.

## CNN

A small convolutional network for 5-class flower classification on 32×32 RGB images:

```
input 3×32×32
conv 6 kernels 3×3, pad 1  → 6×32×32, relu
maxpool 2×2 stride 2       → 6×16×16
conv 12 kernels 3×3, pad 1 → 12×16×16, relu
maxpool 2×2 stride 2       → 12×8×8
flatten                    → 768
dense 768 → 64, relu
dense 64 → 5, softmax
```

Kernels and dense weights use He initialization. Training uses SGD with per-epoch learning-rate decay (×0.95) and gradient-norm clipping.

### Dataset

The [TensorFlow flower_photos dataset](https://www.tensorflow.org/datasets/catalog/tf_flowers) (daisy, dandelion, roses, sunflowers, tulips). It is gitignored; download and trim it with:

```bash
curl -sSL -o flower_photos.tgz https://storage.googleapis.com/download.tensorflow.org/example_images/flower_photos.tgz
tar -xzf flower_photos.tgz && rm flower_photos.tgz
for d in flower_photos/*/; do ls "$d" | tail -n +201 | while read f; do rm "$d$f"; done; done
rm -f flower_photos/LICENSE.txt
```

`DataUtilImages` loads whatever is in the class folders (folder name order = class index), resizes each image to 32×32 with bilinear interpolation, normalizes pixels to [0, 1], and splits 70/15/15 into train/eval/test batches. Unreadable images are skipped with a warning.

### Run

```bash
java -Xmx6g -cp out models.CNNFlowerModel
```

### Results

With 200 images per class (1000 total), 18 epochs, lr 0.05, batch size 5:

| metric | value |
|---|---|
| peak eval accuracy | 55.7% (epoch 4) |
| final test accuracy | 43.7% |
| chance baseline | 20% |

The model overfits after ~epoch 5 (final train log-likelihood −0.05); early stopping at the eval peak would improve test accuracy. Training takes a few hours because the scalar autograd allocates one object per operation — roughly 400k graph nodes per image forward pass.

## Layer sanity checks

```bash
java -cp out tests.ConvolutionalLayerTest
java -cp out tests.MaxPoolLayerTest
```

Print the input, kernels, and output of a single forward pass for manual inspection.
