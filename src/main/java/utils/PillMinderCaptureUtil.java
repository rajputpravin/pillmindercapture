package utils;

import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class PillMinderCaptureUtil {

    private static final String ABSOLUTE_PATH = "./src/main/resources/";
    private static final String CELLS_PATH = "filledCells/";

    // region Constructor
    private PillMinderCaptureUtil() {
        // Empty by default
    }

    /**
     * Used to process forwarded {@link Mat} image and return the result.
     *
     * @param mat Image to process.
     * @return Returns processed image.
     */
    public static Mat processImage(final Mat mat) {
        final Mat processed = new Mat(mat.height(), mat.width(), mat.type());
        // Blur an image using a Gaussian filter
        Imgproc.GaussianBlur(mat, processed, new Size(7, 7), 1);

        // Switch from RGB to GRAY
        Imgproc.cvtColor(processed, processed, Imgproc.COLOR_RGB2GRAY);

        // Find edges in an image using the Canny algorithm
        Imgproc.Canny(processed, processed, 200, 25);

        // Dilate an image by using a specific structuring element
        // https://en.wikipedia.org/wiki/Dilation_(morphology)
        Imgproc.dilate(processed, processed, new Mat(), new Point(-1, -1), 1);

        return processed;
    }

    /**
     * Used to mark outer rectangle and its corners.
     *
     * @param processedImage Image used for calculation of contours and corners.
     * @param originalImage  Image on which marking is done.
     */
    public static void markOuterContour(final Mat processedImage, final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(processedImage, allContours, new Mat(processedImage.size(), processedImage.type()),
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        // Filter out noise and display contour area value
        final List<MatOfPoint> filteredContours = allContours.stream().filter(contour -> {
            final double value = Imgproc.contourArea(contour);
            final Rect rect = Imgproc.boundingRect(contour);

            final boolean isNotNoise = value > 10000;

            if (isNotNoise) {
                Imgproc.putText(originalImage, "Area: " + (int) value,
                        new Point(rect.x + rect.width, rect.y + rect.height), 2, 0.5, new Scalar(124, 252, 0), 1);

                MatOfPoint2f dst = new MatOfPoint2f();
                contour.convertTo(dst, CvType.CV_32F);
                Imgproc.approxPolyDP(dst, dst, 0.02 * Imgproc.arcLength(dst, true), true);
                Imgproc.putText(originalImage, "Points: " + dst.toArray().length,
                        new Point(rect.x + rect.width, rect.y + rect.height + 15), 2, 0.5, new Scalar(124, 252, 0), 1);
            }

            return isNotNoise;
        }).collect(Collectors.toList());

        // Mark contours
        Imgproc.drawContours(originalImage, filteredContours, -1, // Negative value indicates that we want to draw all
                // of contours
                new Scalar(124, 252, 0), // Green color
                1);

        /*
         * for(MatOfPoint filteredContour : filteredContours) {
         * System.out.println(filteredContour.hashCode() + " : " +
         * filteredContour.empty()); }
         */
    }
    // endregion

    // region UI
    public static void createJFrame(final JPanel... panels) {
        final JFrame window = new JFrame("Shape Detection");
        window.setSize(new Dimension(panels.length * 640, 480));
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(new GridLayout(1, panels.length));

        for (final JPanel panel : panels) {
            window.add(panel);
        }

        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Draw forwarded mat image to forwarded panel.
     *
     * @param mat   Image to draw.
     * @param panel Panel on which to draw image.
     */
    public static void drawImage(final Mat mat, final JPanel panel) {
        // Get buffered image from mat frame
        final BufferedImage image = PillMinderCaptureUtil.convertMatToBufferedImage(mat);

        // Draw image to panel
        final Graphics graphics = panel.getGraphics();
        graphics.drawImage(image, 0, 0, panel);
    }
    // endregion

    // region Helpers

    /**
     * Converts forwarded {@link Mat} to {@link BufferedImage}.
     *
     * @param mat Mat to convert.
     * @return Returns converted BufferedImage.
     */
    private static BufferedImage convertMatToBufferedImage(final Mat mat) {
        // Create buffered image
        final BufferedImage bufferedImage = new BufferedImage(mat.width(), mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR);

        // Write data to image
        final WritableRaster raster = bufferedImage.getRaster();
        final DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        mat.get(0, 0, dataBuffer.getData());

        return bufferedImage;
    }
    // endregion

    public static void removeLines(final Mat processedImage) {
        final Mat lines = new Mat();

        // Detect lines
        Imgproc.HoughLinesP(processedImage, lines, 1, Math.PI / 180, 150, 300, 20);

        // Remove found lines. Removing in our case means just drawing over them with
        // black color (our background is
        // also black).
        for (int r = 0; r < lines.rows(); r++) {
            double[] l = lines.get(r, 0);
            Imgproc.line(processedImage, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 2,
                    Imgproc.FILLED, 0);
        }

        lines.release();
    }

    public static int[][] getPillBoxMatrix(final Mat processedImage) {
        final int[][] matrix = new int[2][7];
        final int cellWidth = processedImage.width() / 7;
        final int cellHeight = processedImage.height() / 2;
        final Size cellSize = new Size(cellWidth, cellHeight);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 7; col++) {
                final double tempXPosition = col * cellWidth;
                final double tempYPosition = row * cellHeight;

                final Mat cell = new Mat(processedImage, new Rect(new Point(tempXPosition, tempYPosition), cellSize) // Which
                        // part
                        // to
                        // cut
                        // out
                ).clone();

                // Find non zero pixel count
                final int count = Core.countNonZero(cell);
                if (count < 800) {
                    matrix[row][col] = 0;
                } else {
                    saveImage(cell, CELLS_PATH + getPillBoxCellName(row, col) + ".jpg");
                    matrix[row][col] = 1;
                }
                System.out.println("Row : " + row + "Col : " + col + "Count : " + matrix[row][col]);
            }
        }

        return matrix;
    }

    private static String getPillBoxCellName(int row, int col) {
        StringBuilder cellName = new StringBuilder();
        switch (col) {
            case 0:
                cellName.append("Sunday");
                break;
            case 1:
                cellName.append("Monday");
                break;
            case 2:
                cellName.append("Tuesday");
                break;
            case 3:
                cellName.append("Wednesday");
                break;
            case 4:
                cellName.append("Thursday");
                break;
            case 5:
                cellName.append("Friday");
                break;
            case 6:
                cellName.append("Saturday");
                break;
            default:
                cellName.append(System.nanoTime());
                break;
        }

        switch (row) {
            case 0:
                cellName.append("_AM");
                break;
            case 1:
                cellName.append("_PM");
                break;
        }
        return cellName.toString();
    }

    public static Mat preProcessImage(final Mat image) {
        final Mat processed = new Mat(image.height(), image.width(), CvType.CV_8UC1);

        // RGB to GRAY
        Imgproc.cvtColor(image, processed, Imgproc.COLOR_RGB2GRAY);
        saveImage(processed, "pillbox_RGB_TO_GRAY.jpg");

        // Invert
        Core.bitwise_not(processed, processed);
        saveImage(processed, "pillbox_GRAY_TO_BLACK_Inverted.jpg");

        return processed;
    }

    public static Mat loadImage(final String imagePath) {
        return Imgcodecs.imread(ABSOLUTE_PATH + imagePath);
    }

    public static void markOuterRectangleAndCorners(final Mat processedImage, final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(processedImage, allContours,
                new Mat(processedImage.height(), processedImage.width(), processedImage.type()), Imgproc.RETR_EXTERNAL, // We
                // are
                // looking
                // for
                // external
                // contours
                Imgproc.CHAIN_APPROX_SIMPLE);

        // Find index of biggest contour
        final int biggestContourIndex = getBiggestPolygonIndex(allContours);

        // Mark outer contour (biggest one)
        markPolyCurve(allContours, biggestContourIndex, originalImage);

        // Find corner points and mark them
        final Point[] points = getPoints(allContours.get(biggestContourIndex));
        for (final Point point : points) {
            Imgproc.drawMarker(originalImage, point, new Scalar(255, 0, 0), 0, 30, 2);
        }
    }

    /**
     * Used to find index of biggest polygonal curve.
     *
     * @param contours Contours for which index of biggest polygonal curve is calculated.
     * @return Returns an integer representing index of biggest polygonal curve.
     */
    private static int getBiggestPolygonIndex(final List<MatOfPoint> contours) {
        double maxValue = 0;
        int maxValueIndex = 0;
        for (int i = 0; i < contours.size(); i++) {
            final double contourArea = Imgproc.contourArea(contours.get(i));
            // If current value (contourArea) is bigger then maxValue then it becomes
            // maxValue
            if (maxValue < contourArea) {
                maxValue = contourArea;
                maxValueIndex = i;
            }
        }

        return maxValueIndex;
    }

    /**
     * Mark polygonal curve with green colour.
     *
     * @param contours All contours.
     * @param index    Index of biggest contour, if it is negative all contours will be
     *                 drawn.
     * @param image    Image on which contours are drawn.
     */
    private static void markPolyCurve(final List<MatOfPoint> contours, final int index, final Mat image) {
        Imgproc.drawContours(image, contours, index, new Scalar(124, 252, 0), // Green color
                3);
    }

    /**
     * Used to get corner points of provided polygonal curve.
     *
     * @param poly Polygonal curve for which corner points are found.
     * @return Returns an array of found corner points.
     */
    private static Point[] getPoints(final MatOfPoint poly) {
        MatOfPoint2f approxPolygon = approxPolygon(poly);
        Point[] sortedPoints = new Point[4];

        if (!approxPolygon.size().equals(new Size(1, 4))) {
            return sortedPoints;
        }

        // Calculate the center of mass of our contour image using moments
        final Moments moment = Imgproc.moments(approxPolygon);
        final int centerX = (int) (moment.get_m10() / moment.get_m00());
        final int centerY = (int) (moment.get_m01() / moment.get_m00());

        // We need to sort corner points as there is not guarantee that we will always
        // get them in same order
        for (int i = 0; i < approxPolygon.rows(); i++) {
            final double[] data = approxPolygon.get(i, 0);
            final double dataX = data[0];
            final double dataY = data[1];

            // Sorting is done in reverence to center points (centerX, centerY)
            if (dataX < centerX && dataY < centerY) {
                sortedPoints[0] = new Point(dataX, dataY);
            } else if (dataX > centerX && dataY < centerY) {
                sortedPoints[1] = new Point(dataX, dataY);
            } else if (dataX < centerX && dataY > centerY) {
                sortedPoints[2] = new Point(dataX, dataY);
            } else if (dataX > centerX && dataY > centerY) {
                sortedPoints[3] = new Point(dataX, dataY);
            }
        }

        return sortedPoints;
    }

    /**
     * Approximates a polygonal curve.
     *
     * @param poly Polygonal curve.
     * @return .
     */
    private static MatOfPoint2f approxPolygon(final MatOfPoint poly) {
        final MatOfPoint2f destination = new MatOfPoint2f();
        final MatOfPoint2f source = new MatOfPoint2f();
        poly.convertTo(source, CvType.CV_32FC2);

        // Approximates a polygonal curve with the specified precision
        Imgproc.approxPolyDP(source, destination, 0.02 * Imgproc.arcLength(source, true), true);

        return destination;
    }

    /**
     * Save image on provided path.
     *
     * @param imageToSave Image to be saved.
     * @param path        Path on which image is saved.
     */
    public static void saveImage(final Mat imageToSave, final String path) {
        Imgcodecs.imwrite(ABSOLUTE_PATH + path, imageToSave);
    }
}
