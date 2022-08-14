package com.example.plantdiseaseimage.myutils;

import android.graphics.Bitmap;

import java.util.Arrays;
import java.util.List;

import Jama.Matrix;
import de.lmu.ifi.dbs.utilities.Arrays2;

public class GLCM {
static int totalPixels=0;
/**
 * The number of gray values for the textures
 */
public final int NUM_GRAY_VALUES = 32;
/**
 * p_(x+y) statistics
 */
public final double[] p_x_plus_y = new double[2 * NUM_GRAY_VALUES - 1];
/**
 * p_(x-y) statistics
 */
public final double[] p_x_minus_y = new double[NUM_GRAY_VALUES];
/**
 * row mean value
 */
public double mu_x = 0;
/**
 * column mean value
 */
public double mu_y = 0;
/**
 * row variance
 */
public double var_x = 0;
/**
 * column variance
 */
public double var_y = 0;
/**
 * HXY1 statistics
 */
public double hx = 0;
/**
 * HXY2 statistics
 */
public double hy = 0;
/**
 * HXY1 statistics
 */
public double hxy1 = 0;
/**
 * HXY2 statistics
 */
public double hxy2 = 0;
/**
 * p_x statistics
 */
public final double[] p_x = new double[NUM_GRAY_VALUES];
/**
 * p_y statistics
 */
public final double[] p_y = new double[NUM_GRAY_VALUES];
// -
public List<double[]> data;
public int haralickDist;
public double[] features = null;
public static byte[] imageArray;
public void addData(double[] data) {

    this.data.add(data);
}
public List<double[]> getFeatures() {
    return data;
}
public void process(Bitmap b) {

    features = new double[14];

    Coocurrence coocurrence = new Coocurrence(b, NUM_GRAY_VALUES, this.haralickDist);
    coocurrence.calculate();
    double[][] cooccurrenceMatrix = coocurrence.getCooccurrenceMatrix();
    double meanGrayValue = coocurrence.getMeanGrayValue();


    normalize(cooccurrenceMatrix, coocurrence.getCooccurenceSums());

    calculateStatistics(cooccurrenceMatrix);
    double[][] p = cooccurrenceMatrix;
    double[][] Q = new double[NUM_GRAY_VALUES][NUM_GRAY_VALUES];
    for (int i = 0; i < NUM_GRAY_VALUES; i++) {
        double sum_j_p_x_minus_y = 0;
        for (int j = 0; j < NUM_GRAY_VALUES; j++) {
            double p_ij = p[i][j];

            sum_j_p_x_minus_y += j * p_x_minus_y[j];

            features[0] += p_ij * p_ij;
            features[2] += i * j * p_ij - mu_x * mu_y;
            features[3] += (i - meanGrayValue) * (i - meanGrayValue) * p_ij;
            features[4] += p_ij / (1 + (i - j) * (i - j));
            features[8] += p_ij * log(p_ij);

            // feature 13
            if (p_ij != 0 && p_x[i] != 0) { // would result in 0
                for (int k = 0; k < NUM_GRAY_VALUES; k++) {
                    if (p_y[k] != 0 && p[j][k] != 0) { // would result in NaN
                        Q[i][j] += (p_ij * p[j][k]) / (p_x[i] * p_y[k]);
                    }
                }
            }
        }

        features[1] += i * i * p_x_minus_y[i];
        features[9] += (i - sum_j_p_x_minus_y) * (i - sum_j_p_x_minus_y) * p_x_minus_y[i];
        features[10] += p_x_minus_y[i] * log(p_x_minus_y[i]);
    }

    // feature 13: Max Correlation Coefficient
    double[] realEigenvaluesOfQ = new Matrix(Q).eig().getRealEigenvalues();
    Arrays2.abs(realEigenvaluesOfQ);
    Arrays.sort(realEigenvaluesOfQ);
    features[13] = Math.sqrt(realEigenvaluesOfQ[realEigenvaluesOfQ.length - 2]);

    features[2] /= Math.sqrt(var_x * var_y);
    features[8] *= -1;
    features[10] *= -1;
    double maxhxhy = Math.max(hx, hy);
    if (Math.signum(maxhxhy) == 0) {
        features[11] = 0;
    } else {
        features[11] = (features[8] - hxy1) / maxhxhy;
    }
    features[12] = Math.sqrt(1 - Math.exp(-2 * (hxy2 - features[8])));

    for (int i = 0; i < 2 * NUM_GRAY_VALUES - 1; i++) {
        features[5] += i * p_x_plus_y[i];
        features[7] += p_x_plus_y[i] * log(p_x_plus_y[i]);

        double sum_j_p_x_plus_y = 0;
        for (int j = 0; j < 2 * NUM_GRAY_VALUES - 1; j++) {
            sum_j_p_x_plus_y += j * p_x_plus_y[j];
        }
        features[6] += (i - sum_j_p_x_plus_y) * (i - sum_j_p_x_plus_y) * p_x_plus_y[i];
    }

    features[7] *= -1;
}

/**
 * Calculates the statistical properties.
 */
public void calculateStatistics(double[][] cooccurrenceMatrix) {
    // p_x, p_y, p_x+y, p_x-y
    for (int i = 0; i < NUM_GRAY_VALUES; i++) {
        for (int j = 0; j < NUM_GRAY_VALUES; j++) {
            double p_ij = cooccurrenceMatrix[i][j];

            p_x[i] += p_ij;
            p_y[j] += p_ij;

            p_x_plus_y[i + j] += p_ij;
            p_x_minus_y[Math.abs(i - j)] += p_ij;
        }
    }

    // mean and variance values
    double[] meanVar;
    meanVar = meanVar(p_x);
    mu_x = meanVar[0];
    var_x = meanVar[1];
    meanVar = meanVar(p_y);
    mu_y = meanVar[0];
    var_y = meanVar[1];

    for (int i = 0; i < NUM_GRAY_VALUES; i++) {
        // hx and hy
        hx += p_x[i] * log(p_x[i]);
        hy += p_y[i] * log(p_y[i]);

        // hxy1 and hxy2
        for (int j = 0; j < NUM_GRAY_VALUES; j++) {
            double p_ij = cooccurrenceMatrix[i][j];
            hxy1 += p_ij * log(p_x[i] * p_y[j]);
            hxy2 += p_x[i] * p_y[j] * log(p_x[i] * p_y[j]);
        }
    }
    hx *= -1;
    hy *= -1;
    hxy1 *= -1;
    hxy2 *= -1;
}

/**
 * Compute mean and variance of the given array
 *
 * @param a inut values
 * @return array{mean, variance}
 */
public double[] meanVar(double[] a) {
    // VAR(X) = E(X^2) - E(X)^2
    // two-pass is numerically stable.
    double ex = 0;
    for (int i = 0; i < NUM_GRAY_VALUES; i++) {
        ex += a[i];
    }
    ex /= a.length;
    double var = 0;
    for (int i = 0; i < NUM_GRAY_VALUES; i++) {
        var += (a[i] - ex) * (a[i] - ex);
    }
    var /= (a.length - 1);

    return new double[]{ex, var};
}

/**
 * Returns the bound logarithm of the specified value.
 *
 * If Math.log would be Double.NEGATIVE_INFINITY, 0 is returned
 *
 * @param value the value for which the logarithm should be returned
 * @return the logarithm of the specified value
 */
public double log(double value) {
    double log = Math.log(value);
    if (log == Double.NEGATIVE_INFINITY) {
        log = 0;
    }
    return log;
}

/**
 * Normalizes the array by the given sum. by dividing each 2nd dimension
 * array componentwise by the sum.
 *
 * @param A
 * @param sum
 */
public void normalize(double[][] A, double sum) {
    for (double[] A1 : A) {
        Arrays2.div(A1, sum);
    }
}

//<editor-fold defaultstate="collapsed" desc="getter/Setter">
/**
 * Getter for haralick distributions
 *
 * @return haralick distributions
 */
public int getHaralickDist() {
    return haralickDist;
}

/**
 * Setter for haralick distributions
 *
 * @param haralickDist int for haralick distributions (must be >= 1)
 */
public void setHaralickDist(int haralickDist) {
    if (haralickDist <= 0) {
        throw new IllegalArgumentException("the distance for haralick must be >= 1 but was " + haralickDist);
    }
    this.haralickDist = haralickDist;
}
//</editor-fold>
static class Coocurrence {

    /**
     * The number of gray values for the textures
     */
    public final int NUM_GRAY_VALUES;
    /**
     * The number of gray levels in an image
     */
    int GRAY_RANGES = 256;
    /**
     * The scale for the gray values for conversion rgb to gray values.
     */
    double GRAY_SCALE;
    /**
     * gray histogram of the image.
     */
    double[] grayHistogram;
    /**
     * Quantized gray values of each pixel of the image.
     *
     * Use int instead of byte as there is no unsigned byte in Java.
     * Otherwise you'll have a hard time using white = 255. Alternative:
     * replace with ImageJ ByteProcessor.
     */
    public final int[] grayValue;
    /**
     * mean gray value
     */
    public double meanGrayValue = 0;
    /**
     * The cooccurrence matrix
     */
    public final double[][] cooccurrenceMatrices;
    /**
     * The value for one increment in the gray/color histograms.
     */
    public final int HARALICK_DIST;
    public final Bitmap image;

    public Coocurrence(Bitmap b, int numGrayValues, int haralickDist) {
        this.NUM_GRAY_VALUES = numGrayValues;
        this.HARALICK_DIST = haralickDist;
        this.cooccurrenceMatrices = new double[NUM_GRAY_VALUES][NUM_GRAY_VALUES];
        this.image = b;
        totalPixels=b.getHeight()*b.getWidth();
        this.grayValue = new int[totalPixels];
    }

    void calculate() {
        this.GRAY_SCALE = (double) GRAY_RANGES / (double) NUM_GRAY_VALUES;
        this.grayHistogram = new double[GRAY_RANGES];

        calculateGreyValues();

        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final int d = HARALICK_DIST;
        final int yOffset = d * imageWidth;
        int i, j, pos;

        // image is not empty per default
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                pos = imageWidth * y + x;

                // horizontal neighbor: 0 degrees
                i = x - d;
                if (i >= 0) {
                    increment(grayValue[pos], grayValue[pos - d]);
                }

                // vertical neighbor: 90 degree
                j = y - d;
                if (j >= 0) {
                    increment(grayValue[pos], grayValue[pos - yOffset]);
                }

                // 45 degree diagonal neigbor
                i = x + d;
                j = y - d;
                if (i < imageWidth && j >= 0) {
                    increment(grayValue[pos], grayValue[pos + d - yOffset]);
                }

                // 135 vertical neighbor
                i = x - d;
                j = y - d;
                if (i >= 0 && j >= 0) {
                    increment(grayValue[pos], grayValue[pos - d - yOffset]);
                }
            }
        }
    }

    public void calculateGreyValues() {
        final int size = grayValue.length;
        double graySum = 0;
        for (int pos = 0; pos < size; pos++) {
            int gray = imageArray[pos]&0xff;
            graySum += gray;
            grayValue[pos] = (int) (gray / GRAY_SCALE);  // quantized for texture analysis
            assert grayValue[pos] >= 0 : grayValue[pos] + " > 0 violated";
            grayHistogram[gray]++;
        }
        Arrays2.div(grayHistogram, size);
        meanGrayValue = Math.floor(graySum / size / GRAY_SCALE)*GRAY_SCALE;
    }

    /**
     * Incremets the coocurrence matrix at the specified positions (g1,g2)
     * and (g2,g1) if g1 and g2 are in range.
     *
     * @param g1 the gray value of the first pixel
     * @param g2 the gray value of the second pixel
     */
    public void increment(int g1, int g2) {
        cooccurrenceMatrices[g1][g2]++;
        cooccurrenceMatrices[g2][g1]++;
    }

    public double getMeanGrayValue() {
        return this.meanGrayValue;
    }

    public double[][] getCooccurrenceMatrix() {
        return this.cooccurrenceMatrices;
    }

    public double getCooccurenceSums() {
        // divide by R=8 neighbours
        // see p.613, §2 of Haralick paper
        return totalPixels * 8;
    }
}
}