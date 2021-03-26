public class DWT {
    private RGB[][] rgbChannels;
    private final int height;
    private final int width;
    private final int numCoefficient;

    public DWT(int height, int width, int numCoefficient) {
        this.height = height;
        this.width = width;
        this.numCoefficient = numCoefficient;
        rgbChannels = new RGB[height][width];
    }

    public RGB[][] getRgbChannels() {
        return rgbChannels;
    }

    public void compress() {
        int numSteps = getNumDecompositionSteps();

        RGB[][] decomposedColumnChannels;

        RGB[][] decomposedRowChannels;

        int currHeight = height;

        int currWidth = width;

        RGB[][] initialChannels = rgbChannels;

        for (int i = 0; i < numSteps; i++) {
            decomposedColumnChannels = getColumnDecompositionChannels(initialChannels, currHeight, currWidth);

            decomposedRowChannels = getRowDecompositionChannels(decomposedColumnChannels, currHeight, currWidth);

            if (i > 0) {
                for (int row = 0; row < decomposedRowChannels.length; row++) {
                    for (int col = 0; col < decomposedRowChannels[row].length ; col++) {
                        rgbChannels[row+height-currHeight][col] = decomposedRowChannels[row][col];
                    }
                }
            } else {
                rgbChannels = decomposedRowChannels;
            }

            currHeight /= 2;

            currWidth /= 2;

            initialChannels = getSubChannels(rgbChannels, height - currHeight, currHeight, currWidth);
        }

        quantize();

        RGB[][] constructedRowChannels;

        RGB[][] constructedColumnChannels;

        initialChannels = rgbChannels;

        for (int i = 0; i < numSteps; i++) {
            currHeight *= 2;

            currWidth *= 2;

            RGB[][] subChannels = getSubChannels(initialChannels, height - currHeight, currHeight, currWidth);

            constructedRowChannels = getRowConstructionChannels(subChannels, currHeight, currWidth);

            constructedColumnChannels = getColumnConstructionChannels(constructedRowChannels, currHeight, currWidth);

            if (i != numSteps - 1) {
                for (int row = 0; row < constructedColumnChannels.length; row++) {
                    for (int col = 0; col < constructedColumnChannels[row].length ; col++) {
                        rgbChannels[row+height-currHeight][col] = constructedColumnChannels[row][col];
                    }
                }
            } else {
                rgbChannels = constructedColumnChannels;
            }
        }
    }

    private RGB[][] getColumnDecompositionChannels(RGB[][] inputChannels, int height, int width) {
        int midWidth = width / 2;

        RGB[][] decompositionBuffer = new RGB[height][width];

        for (int row = 0; row < height; row++) {
            int bufferIndex = 0;

            for (int col = 0; col < width; col += 2) {
                RGB rgb1 = inputChannels[row][col];
                RGB rgb2 = inputChannels[row][col+1];
                decompositionBuffer[row][bufferIndex] = getSumCoefficient(rgb1, rgb2, true);
                bufferIndex++;
            }
        }

        for (int row = 0; row < height; row++) {
            int bufferIndex = midWidth;

            for (int col = 0; col < width; col += 2) {
                RGB rgb1 = inputChannels[row][col];
                RGB rgb2 = inputChannels[row][col+1];
                decompositionBuffer[row][bufferIndex] = getDiffCoefficient(rgb1, rgb2, true);
                bufferIndex++;
            }
        }

        return decompositionBuffer;
    }

    private RGB[][] getRowDecompositionChannels(RGB[][] inputChannels, int height, int width) {
        int midHeight = height / 2;

        RGB[][] decompositionBuffer = new RGB[height][width];

        for (int col = 0; col < width; col++) {
            int bufferIndex = 0;

            for (int row = 0; row < height; row += 2) {
                RGB rgb1 = inputChannels[row][col];
                RGB rgb2 = inputChannels[row + 1][col];
                decompositionBuffer[bufferIndex][col] = getDiffCoefficient(rgb1, rgb2, true);
                bufferIndex++;
            }
        }

        for (int col = 0; col < width; col++) {
            int bufferIndex = midHeight;

            for (int row = 0; row < height; row += 2) {
                RGB rgb1 = inputChannels[row][col];
                RGB rgb2 = inputChannels[row + 1][col];
                decompositionBuffer[bufferIndex][col] = getSumCoefficient(rgb1, rgb2, true);

                bufferIndex++;
            }
        }

        return decompositionBuffer;
    }

    private RGB[][] getColumnConstructionChannels(RGB[][] decomposedChannels, int height, int width) {
        int midWidth = width / 2;

        RGB[][] constructionBuffer = new RGB[height][width];

        for (int row = 0; row < height; row++) {
            int bufferIndex = 0;

            for (int col = 0; col < midWidth; col++) {
                RGB rgb1 = decomposedChannels[row][col];
                RGB rgb2 = decomposedChannels[row][col+midWidth];
                constructionBuffer[row][bufferIndex] = getSumCoefficient(rgb1, rgb2, false);
                bufferIndex++;
                constructionBuffer[row][bufferIndex] = getDiffCoefficient(rgb1, rgb2, false);
                bufferIndex++;
            }
        }

        return constructionBuffer;
    }

    private RGB[][] getRowConstructionChannels(RGB[][] decomposedChannels, int height, int width) {
        int midHeight = height / 2;

        RGB[][] constructionBuffer = new RGB[height][width];

        for (int col = 0; col < width; col++) {
            int bufferIndex = 0;

            for (int row = 0; row < midHeight; row++) {
                RGB rgb1 = decomposedChannels[row+midHeight][col];
                RGB rgb2 = decomposedChannels[row][col];
                constructionBuffer[bufferIndex][col] = getSumCoefficient(rgb1, rgb2, false);
                bufferIndex++;
                constructionBuffer[bufferIndex][col] = getDiffCoefficient(rgb1, rgb2, false);
                bufferIndex++;
            }
        }

        return constructionBuffer;
    }

    private RGB getSumCoefficient(RGB rgb1, RGB rgb2, boolean isAverage) {
        double denominator = isAverage ? 2.0 : 1.0;

        int r = (int) Math.round((rgb1.getR() + rgb2.getR()) / denominator);
        int g = (int) Math.round((rgb1.getG() + rgb2.getG()) / denominator);
        int b = (int) Math.round((rgb1.getB() + rgb2.getB()) / denominator);

        return new RGB(r, g, b);
    }

    private RGB getDiffCoefficient(RGB rgb1, RGB rgb2, boolean isAverage) {
        double denominator = isAverage ? 2.0 : 1.0;

        int r = (int) Math.round((rgb1.getR() - rgb2.getR()) / denominator);
        int g = (int) Math.round((rgb1.getG() - rgb2.getG()) / denominator);
        int b = (int) Math.round((rgb1.getB() - rgb2.getB()) / denominator);

        return new RGB(r, g, b);
    }

    private void quantize() {
        int size = (int) Math.sqrt(numCoefficient);

        int startRow = height - size;

        if (startRow == 0) return;

        RGB[][] quantizedChannels = new RGB[height][width];

        for (int i = 0; i < quantizedChannels.length; i++) {
            for (int j = 0; j < quantizedChannels[i].length; j++) {
                quantizedChannels[i][j] = new RGB(0, 0, 0);
            }
        }

        for (int row = startRow; row < height; row++) {
            for (int col = 0; col < size; col++) {
                quantizedChannels[row][col] = rgbChannels[row][col];
            }
        }

        rgbChannels = quantizedChannels;
    }

    private int getNumDecompositionSteps() {
        int numStep = 0;

        int currCoefficient = height * width;

        while (currCoefficient != numCoefficient) {
            currCoefficient /= 4;

            numStep++;
        }

        return numStep;
    }

    private RGB[][] getSubChannels(RGB[][] channels, int startRow, int height, int width) {
        RGB[][] subChannels = new RGB[height][width];

        for (int row = 0; row < subChannels.length; row++) {
            for (int col = 0; col < subChannels[row].length; col++) {
                subChannels[row][col] = channels[row + startRow][col];
            }
        }

        return subChannels;
    }
}
