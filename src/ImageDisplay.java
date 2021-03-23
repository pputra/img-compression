import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageDisplay {
    private JFrame frame;
    private JLabel lbOriginalImage;
    private JLabel lbProcessedImage;
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private final int WIDTH = 512;
    private final int HEIGHT = 512;

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
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);

                    img.setRGB(x,y,pix);

                    processedImage.setRGB(x, y, pix);

                    ind++;
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

        // Read in the specified image
        originalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        processedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        readImageRGB(WIDTH, HEIGHT, args[0], originalImage);

        // Use label to display the image
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        lbOriginalImage = new JLabel(new ImageIcon(originalImage));
        lbProcessedImage = new JLabel(new ImageIcon(processedImage));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbOriginalImage, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbProcessedImage, c);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ImageDisplay ren = new ImageDisplay();
        ren.showIms(args);
    }
}