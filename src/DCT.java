import java.util.Arrays;

public class DCT {
    private RGB[][] rgbChannels;
    private int height;
    private int width;
    private int m;
    private int n;

    public DCT(int height, int width, int m, int n) {
        this.height = height;
        this.width = width;
        this.m = m;
        this.n = n;
        rgbChannels = new RGB[width][height];
    }

    public DCT(RGB[][] rgbChannels) {
        this.rgbChannels = new RGB[rgbChannels.length][];
        for (int i = 0; i < rgbChannels.length; i++) {
            rgbChannels[i] = Arrays.copyOf(rgbChannels[i], rgbChannels[i].length);
        }
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

    public RGB[][] getRgbChannels() {
        return rgbChannels;
    }
}
