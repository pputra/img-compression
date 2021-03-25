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

    public void compress() {
        int midWidth = width / 2;
        RGB[][] decompositionBuffer = new RGB[height][width];

        for (int i = 0; i < height; i++) {
            int bufferIndex = 0;
            for (int j = 0; j < width; j += 2) {
                RGB rgb1 = rgbChannels[i][j];
                RGB rgb2 = rgbChannels[i][j+1];

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
                RGB rgb1 = rgbChannels[i][j];
                RGB rgb2 = rgbChannels[i][j+1];

                int rAvg = (int) Math.round((rgb1.getR() - rgb2.getR()) / 2.0);
                int gAvg = (int) Math.round((rgb1.getG() - rgb2.getG()) / 2.0);
                int bAvg = (int) Math.round((rgb1.getB() - rgb2.getB()) / 2.0);

                decompositionBuffer[i][bufferIndex] = new RGB(rAvg, gAvg, bAvg);
                bufferIndex++;
            }
        }

        rgbChannels = decompositionBuffer;
    }

    public RGB[][] getRgbChannels() {
        return rgbChannels;
    }
}
