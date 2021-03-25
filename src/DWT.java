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
        RGB[][] decomposedColumnChannels = getColumnDecompositionChannels(rgbChannels, height, width);

        RGB[][] decomposedRowChannels = getRowDecompositionChannels(decomposedColumnChannels, height, width);

        RGB[][] constructedRowChannel = getRowConstructionChannels(decomposedRowChannels, height, width);

        RGB[][] constructedColumnChannel = getColumnConstructionChannels(constructedRowChannel, height, width);

        rgbChannels = constructedColumnChannel;
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
}
