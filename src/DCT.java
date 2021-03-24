public class DCT {
    private final RGB[][] rgbChannels;
    private final int height;
    private final int width;
    private final int m;
    private final int n;
    private final int numCoefficient;

    public DCT(int height, int width, int m, int n, int numCoefficient) {
        this.height = height;
        this.width = width;
        this.m = m;
        this.n = n;
        this.numCoefficient = (int) Math.ceil(numCoefficient / 4096.0);
        rgbChannels = new RGB[height][width];
    }

    public void compress() {
        for (int offsetRow = 0; offsetRow < height; offsetRow += m) {
            for (int offsetCol = 0; offsetCol < width; offsetCol += n) {
                RGB[][] currDCTChannels = transformDCT(getMbyNBlockChannels(offsetRow, offsetCol));

                updateRGBChannelsBlock(currDCTChannels, offsetRow, offsetCol);

                quantize(offsetRow, offsetCol);

                RGB[][] currIDCTChannels = transformIDCT(getMbyNBlockChannels(offsetRow, offsetCol));

                updateRGBChannelsBlock(currIDCTChannels, offsetRow, offsetCol);
            }
        }

        print2dArr(rgbChannels);
    }

    public RGB[][] transformDCT(RGB[][] in) {
        int u, v, x, y;

        double cu, cv, rSum, gSum, bSum;

        RGB[][] dct = getChannelsBuffer(m, n);

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

                rSum = 0;
                gSum = 0;
                bSum = 0;

                for (x = 0; x < m; x++) {
                    for (y = 0; y < n; y++) {
                        double cosProducts = Math.cos((2.0 * x + 1.0) * u * Math.PI/16.0) *
                                Math.cos((2.0 * y + 1.0) * v * Math.PI/16.0);

                        rSum += in[x][y].getR() * cosProducts;

                        gSum += in[x][y].getG() * cosProducts;

                        bSum += in[x][y].getB() * cosProducts;

                    }
                }
                dct[u][v].setR((int) Math.round(0.25 * cu * cv * rSum));
                dct[u][v].setG((int) Math.round(0.25 * cu * cv * gSum));
                dct[u][v].setB((int) Math.round(0.25 * cu * cv * bSum));
            }
        }

        return dct;
    }

    private RGB[][] transformIDCT(RGB[][] in) {
        int u, v, x, y;

        double cu, cv, rSum, gSum, bSum;

        RGB[][] dct = getChannelsBuffer(m, n);

        for (u = 0; u < m; u++) {
            for (v = 0; v < n; v++) {
                rSum = 0;
                gSum = 0;
                bSum = 0;

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

                        double cosProducts =  cu * cv *
                                Math.cos((2.0 * u + 1.0) * x * Math.PI/16.0) *
                                Math.cos((2.0 * v + 1.0) * y * Math.PI/16.0);

                        rSum += in[x][y].getR() * cosProducts;
                        gSum += in[x][y].getG() * cosProducts;
                        bSum += in[x][y].getB() * cosProducts;
                    }
                }
                dct[u][v].setR((int) Math.round(0.25 * rSum));
                dct[u][v].setG((int) Math.round(0.25 * gSum));
                dct[u][v].setB((int) Math.round(0.25 * bSum));
            }
        }

        return dct;
    }

    private void quantize(int offsetRow, int offsetCol) {
        int maxCount = m * n;
        int count = 0;
        int row = 0;
        int col = 0;
        boolean isDiagonalLeft = true;
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

            if (count >= numCoefficient) {
                rgbChannels[row + offsetRow][col + offsetCol].setR(0);
            }

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

    private RGB[][] getMbyNBlockChannels(int offsetRow, int offsetCol) {
        RGB[][] copiedChannel = new RGB[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                copiedChannel[i][j] = new RGB(rgbChannels[i + offsetRow][j + offsetCol]);
            }
        }

        return copiedChannel;
    }

    private void updateRGBChannelsBlock(RGB[][] newRGBChannels, int offsetRow, int offsetCol) {
        for (int i = 0; i < m; i++) {
            System.arraycopy(newRGBChannels[i], 0, rgbChannels[i + offsetRow], offsetCol, n);
        }
    }

    private RGB[][] getChannelsBuffer(int height, int width) {
        RGB[][] buffer = new RGB[height][width];

        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < buffer[i].length; j++) {
                buffer[i][j] = new RGB(0, 0, 0);
            }
        }

        return buffer;
    }

    public RGB[][] getRgbChannels() {
        return rgbChannels;
    }

    private static void print2dArr(RGB[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j].getR() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
