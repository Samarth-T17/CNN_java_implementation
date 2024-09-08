public class CNNFlowerModel {
    public ConvolutionalLayer conv1;
    public ConvolutionalLayer conv2;
    public MaxpoolLayer max1;
    public ConvolutionalLayer conv3;
    public ConvolutionalLayer conv4;
    public MaxpoolLayer max2;
    public ConvolutionalLayer conv5;
    public MaxpoolLayer max3;
    public ConvolutionalLayer conv6;
    public MaxpoolLayer max4;
    CNNFlowerModel() {
        conv1 = new ConvolutionalLayer(224, 3, 64, 3, 2, 1);
        conv2 = new ConvolutionalLayer(224, 3, 64, 3, 2, 1);
    }

}
