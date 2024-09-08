import java.util.List;

public class ImageTuple {
    public List<List<List<Value>>> image;
    public int classIndex;
    public ImageTuple(List<List<List<Value>>> image, int classIndex) {
        this.image = image;
        this.classIndex = classIndex;
    }
}
