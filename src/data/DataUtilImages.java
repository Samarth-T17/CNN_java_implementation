package data;

import core.Value;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DataUtilImages {

    public static List<String> classNames(String rootDir) {
        File[] dirs = new File(rootDir).listFiles(File::isDirectory);
        if (dirs == null) {
            throw new IllegalArgumentException("Not a directory: " + rootDir);
        }
        Arrays.sort(dirs);
        List<String> names = new ArrayList<>();
        for (File dir : dirs) {
            names.add(dir.getName());
        }
        return names;
    }

    public static List<ImageTuple> loadDataset(String rootDir, int targetSize) {
        List<String> classes = classNames(rootDir);
        List<ImageTuple> data = new ArrayList<>();
        for (int classIndex = 0; classIndex < classes.size(); classIndex++) {
            File classDir = new File(rootDir, classes.get(classIndex));
            File[] files = classDir.listFiles((d, name) -> name.toLowerCase().endsWith(".jpg")
                    || name.toLowerCase().endsWith(".jpeg")
                    || name.toLowerCase().endsWith(".png"));
            if (files == null) continue;
            Arrays.sort(files);
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img == null) {
                        System.err.println("Skipping unreadable image: " + file);
                        continue;
                    }
                    data.add(new ImageTuple(toChannels(resize(img, targetSize)), classIndex));
                } catch (IOException e) {
                    System.err.println("Skipping " + file + " : " + e.getMessage());
                }
            }
        }
        return data;
    }

    private static BufferedImage resize(BufferedImage img, int targetSize) {
        BufferedImage out = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, targetSize, targetSize, null);
        g.dispose();
        return out;
    }

    private static List<List<List<Value>>> toChannels(BufferedImage img) {
        int size = img.getWidth();
        List<List<List<Value>>> channels = new ArrayList<>(3);
        for (int c = 0; c < 3; c++) {
            List<List<Value>> channel = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                channel.add(new ArrayList<>(size));
            }
            channels.add(channel);
        }
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int rgb = img.getRGB(x, y);
                channels.get(0).get(y).add(new Value(((rgb >> 16) & 0xFF) / 255.0));
                channels.get(1).get(y).add(new Value(((rgb >> 8) & 0xFF) / 255.0));
                channels.get(2).get(y).add(new Value((rgb & 0xFF) / 255.0));
            }
        }
        return channels;
    }

    public static List<List<ImageTuple>> divideIntoBatches(List<ImageTuple> data, int batchSize) {
        List<List<ImageTuple>> batches = new ArrayList<>();
        for (int i = 0; i < data.size(); i += batchSize) {
            batches.add(new ArrayList<>(data.subList(i, Math.min(i + batchSize, data.size()))));
        }
        return batches;
    }

    public static List<List<List<ImageTuple>>> trainEvalTestSplit(List<ImageTuple> data, int batchSize) {
        List<ImageTuple> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled, new Random());

        int firstSplit = (int) (shuffled.size() * 0.7);
        int secondSplit = firstSplit + (int) (shuffled.size() * 0.15);

        List<List<List<ImageTuple>>> splits = new ArrayList<>();
        splits.add(divideIntoBatches(shuffled.subList(0, firstSplit), batchSize));
        splits.add(divideIntoBatches(shuffled.subList(firstSplit, secondSplit), batchSize));
        splits.add(divideIntoBatches(shuffled.subList(secondSplit, shuffled.size()), batchSize));
        return splits;
    }
}
