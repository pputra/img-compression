import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageDisplay {
    private JFrame frame;
    private JLabel lbDctImage;
    private JLabel lbDwtImage;
    private BufferedImage dctImage;
    private BufferedImage dwtImage;
    private final int WIDTH = 512;
    private final int HEIGHT = 512;
    private int m = 8;
    private int n = 8;
    private int numCoefficient;

    private DCT dctOutput;
    private DWT dwtOutput;

    /** Read Image RGB
     *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private void readImageRGB(int width, int height, String imgPath, BufferedImage img) {
        try {
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            dctOutput = new DCT(HEIGHT, WIDTH, m, n, numCoefficient);
            dwtOutput = new DWT(HEIGHT, WIDTH, numCoefficient);

            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    RGB rgb = new RGB(r, g, b);

                    dctOutput.getRgbChannels()[y][x] = rgb;
                    dwtOutput.getRgbChannels()[y][x] = rgb;
                    ind++;
                }
            }

            dctOutput.compress();
            dwtOutput.compress();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    RGB dctRgb = dctOutput.getRgbChannels()[y][x];
                    RGB dwtRgb = dwtOutput.getRgbChannels()[y][x];

                    int dctPix = 0xff000000 | ((dctRgb.getR()) << 16) | ((dctRgb.getG()) << 8) | (dctRgb.getB());
                    int dwtPix = 0xff000000 | ((dwtRgb.getR()) << 16) | ((dwtRgb.getG()) << 8) | (dwtRgb.getB());

                    dctImage.setRGB(x, y, dctPix);
                    dwtImage.setRGB(x, y, dwtPix);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showIms(String[] args) {
        // Read a parameter from command line
        numCoefficient = Integer.parseInt(args[1]);
//        numCoefficient = 65536;
//        numCoefficient = 16384;
//        numCoefficient = 4096;
//        numCoefficient = 1024;
//        numCoefficient = 256;
//        numCoefficient = 64;
//        numCoefficient = 16;
//        numCoefficient = 4;
//        numCoefficient = 1;

        // Read in the specified image
        dctImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        dwtImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        readImageRGB(WIDTH, HEIGHT, args[0], dctImage);

        // Use label to display the image
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        lbDctImage = new JLabel(new ImageIcon(dctImage));
        lbDwtImage = new JLabel(new ImageIcon(dwtImage));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbDctImage, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbDwtImage, c);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ImageDisplay ren = new ImageDisplay();
        ren.showIms(args);
    }
}
