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
//        RGB[][] decomposedChannels = getColumnDecompositionChannels(rgbChannels, height, width);
//
//        rgbChannels = getColumnConstructionChannels(decomposedChannels, height, width);

        rgbChannels = getRowDecompositionChannels(rgbChannels, height, width);
    }

    private RGB[][] getColumnDecompositionChannels(RGB[][] inputChannels, int height, int width) {
        int midWidth = width / 2;

        RGB[][] decompositionBuffer = new RGB[height][width];

        for (int i = 0; i < height; i++) {
            int bufferIndex = 0;

            for (int j = 0; j < width; j += 2) {
                RGB rgb1 = inputChannels[i][j];
                RGB rgb2 = inputChannels[i][j+1];

                int rAvg = (int) Math.round((rgb1.getR() + rgb2.getR()) / 2.0);
                int gAvg = (int) Math.round((rgb1.getG() + rgb2.getG()) / 2.0);
                int bAvg = (int) Math.round((rgb1.getB() + rgb2.getB()) / 2.0);

                decompositionBuffer[i][bufferIndex] = new RGB(rAvg, gAvg, bAvg);
                bufferIndex++;
            }
        }

        for (int i = 0; i < height; i++) {
            int bufferIndex = midWidth;

            for (int j = 0; j < width; j += 2) {
                RGB rgb1 = inputChannels[i][j];
                RGB rgb2 = inputChannels[i][j+1];

                int rAvg = (int) Math.round((rgb1.getR() - rgb2.getR()) / 2.0);
                int gAvg = (int) Math.round((rgb1.getG() - rgb2.getG()) / 2.0);
                int bAvg = (int) Math.round((rgb1.getB() - rgb2.getB()) / 2.0);

                decompositionBuffer[i][bufferIndex] = new RGB(rAvg, gAvg, bAvg);
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

                int rAvg = (int) Math.round((rgb1.getR() - rgb2.getR()) / 2.0);
                int gAvg = (int) Math.round((rgb1.getG() - rgb2.getG()) / 2.0);
                int bAvg = (int) Math.round((rgb1.getB() - rgb2.getB()) / 2.0);

                decompositionBuffer[bufferIndex][col] = new RGB(rAvg, gAvg, bAvg);

                bufferIndex++;
            }
        }

        for (int col = 0; col < width; col++) {
            int bufferIndex = midHeight;

            for (int row = 0; row < height; row += 2) {
                RGB rgb1 = inputChannels[row][col];
                RGB rgb2 = inputChannels[row + 1][col];

                int rAvg = (int) Math.round((rgb1.getR() + rgb2.getR()) / 2.0);
                int gAvg = (int) Math.round((rgb1.getG() + rgb2.getG()) / 2.0);
                int bAvg = (int) Math.round((rgb1.getB() + rgb2.getB()) / 2.0);

                decompositionBuffer[bufferIndex][col] = new RGB(rAvg, gAvg, bAvg);

                bufferIndex++;
            }
        }

        return decompositionBuffer;
    }

    private RGB[][] getColumnConstructionChannels(RGB[][] decomposedChannels, int height, int width) {
        int midWidth = width / 2;

        RGB[][] constructionBuffer = new RGB[height][width];

        for (int i = 0; i < height; i++) {
            int bufferIndex = 0;

            for (int j = 0; j < midWidth; j++) {
                RGB rgb1 = decomposedChannels[i][j];
                RGB rgb2 = decomposedChannels[i][j+midWidth];

                int constructedR = rgb1.getR() + rgb2.getR();
                int constructedG = rgb1.getG() + rgb2.getG();
                int constructedB = rgb1.getB() + rgb2.getB();

                constructionBuffer[i][bufferIndex] = new RGB(constructedR, constructedG, constructedB);

                constructedR = rgb1.getR() - rgb2.getR();
                constructedG = rgb1.getG() - rgb2.getG();
                constructedB = rgb1.getB() - rgb2.getB();

                bufferIndex++;

                constructionBuffer[i][bufferIndex] = new RGB(constructedR, constructedG, constructedB);

                bufferIndex++;
            }
        }

        return constructionBuffer;
    }
}
