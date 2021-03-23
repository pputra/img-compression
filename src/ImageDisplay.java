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
    private int m = 8;
    private int n = 8;

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

    public double[][] transformDCT(double[][] in) {
        int u, v, x, y;

        double cu, cv, sum;

        double[][] dct = new double[m][n];

        for (u = 0; u < m; u++) {
            for (v = 0; v < n; v++) {
                if (u == 0) {
                    cu = 1.0 / Math.sqrt(2);
                } else {
                    cu = 1.0;
                }

                if (v == 0) {
                    cv = 1.0 / Math.sqrt(2);
                } else {
                    cv = 1.0;
                }

                sum = 0;

                for (x = 0; x < m; x++) {
                    for (y = 0; y < n; y++) {
                        sum += in[x][y] *
                                Math.cos((2.0 * x + 1.0) * u * Math.PI/16.0) *
                                Math.cos((2.0 * y + 1.0) * v * Math.PI/16.0);
                    }
                }
                dct[u][v] = Math.round(0.25 * cu * cv * sum);
            }
        }

        return dct;
    }

    public double[][] transformIDCT(double[][] in) {
        int u, v, x, y;

        double cu, cv, sum;

        double[][] dct = new double[m][n];

        for (u = 0; u < m; u++) {
            for (v = 0; v < n; v++) {
                sum = 0;

                for (x = 0; x < m; x++) {
                    for (y = 0; y < n; y++) {
                        if (x == 0) {
                            cu = 1.0 / Math.sqrt(2);
                        } else {
                            cu = 1.0;
                        }

                        if (y == 0) {
                            cv = 1.0 / Math.sqrt(2);
                        } else {
                            cv = 1.0;
                        }

                        sum += in[x][y] * cu * cv *
                                Math.cos((2.0 * u + 1.0) * x * Math.PI/16.0) *
                                Math.cos((2.0 * v + 1.0) * y * Math.PI/16.0);
                    }
                }
                dct[u][v] = Math.round(0.25 * sum);
            }
        }

        return dct;
    }

    private void zigZagTraversal(double[][] in) {
        int maxCount = m * n;
        int count = 0;
        int row = 0;
        int col = 0;
        boolean isDiagonalLeft = true;
        System.out.println(in[row][col]);
        count++;
        col++;

        while (count < maxCount) {
            if (row >= n || row < 0 || col >= m || col < 0) {
                if (isDiagonalLeft) {
                    if (row >= n) {
                        col++;
                    } else {
                        row++;
                    }
                    row--;
                    col++;
                } else {
                    if (col >= m) {
                        row++;
                    } else {
                        col++;
                    }
                    row++;
                    col--;
                }
                isDiagonalLeft = !isDiagonalLeft;
            }

            System.out.println(in[row][col]);
            if (isDiagonalLeft) {
                row++;
                col--;
            } else {
                row--;
                col++;
            }
            count++;
        }
    }

    public static void main(String[] args) {
        ImageDisplay ren = new ImageDisplay();
//        ren.showIms(args);

        double[][] in ={{0, 1, 2, 3, 4, 5, 6, 7},
                        {8, 9, 10, 11, 12, 13, 14, 15},
                        {16, 17, 18, 19, 20, 21, 22, 23},
                        {24, 25, 26, 27, 28, 29, 30, 31},
                        {32, 33, 34, 35, 36, 37, 38, 39},
                        {40, 41, 42, 43, 44, 45, 46, 47},
                        {48, 49, 50, 51, 52, 53, 54, 55},
                        {56, 57, 58, 59, 60, 61, 62, 63}};

//        double[][] dct = ren.transformDCT(in);
//        print2dArr(dct);
//
//        double[][] idct = ren.transformIDCT(dct);
//        print2dArr(idct);
        ren.zigZagTraversal(in);
    }

    private static void print2dArr(double[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
