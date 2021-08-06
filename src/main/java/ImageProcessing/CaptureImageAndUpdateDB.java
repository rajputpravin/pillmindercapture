package ImageProcessing;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import utils.PillMinderCaptureUtil;

/**
 * Image processing logic and then update mongo Db using BaseDataAccessObject.java interface
 */
public class CaptureImageAndUpdateDB {
  public static final String IMAGE = "pillbox_cropped.jpg";

    public static void main(final String args[]) {
        // Load OpenCV
        OpenCV.loadShared();

        // Create panels
        // TODO(Put this logic to get pillbox data from cameraFeed in realtime)
        /*
         * final JPanel cameraFeed = new JPanel(); final JPanel processedFeed = new
         * JPanel();
         */
        final Mat processedImage = PillMinderCaptureUtil.preProcessImage(PillMinderCaptureUtil.loadImage(IMAGE));
        final Mat debuggingImage = PillMinderCaptureUtil.loadImage(IMAGE);

        // Mark outer rectangle and corners(marking is done for debugging)
        PillMinderCaptureUtil.markOuterRectangleAndCorners(processedImage, debuggingImage);
        // Remove all lines from processed image
        PillMinderCaptureUtil.removeLines(processedImage);
        // Get pillBoxMatrix matrix with estimated values
        final int[][] pillBoxMatrix = PillMinderCaptureUtil.getPillBoxMatrix(processedImage);
        // TODO(Put this logic to get pillbox data from cameraFeed in realtime)
        /*
         * PillMinderCaptureUtil.createJFrame(cameraFeed);, processedFeed);
         *
         * // Create video capture object (index 0 is default camera) final VideoCapture
         * camera = new VideoCapture(0);
         *
         * // Start shape detection PillMinderCapture.startPillMinderCapture(cameraFeed,
         * processedFeed, camera).run();
         */
        PillMinderCaptureUtil.saveImage(processedImage, "Inverted_And_Lines_Removed.jpg");
        /*PillMinderCaptureUtil.saveImage(debuggingImage, "debugging.jpg");*/
    }

    // TODO(Put this logic to get pillbox data from cameraFeed in realtime)
    /*
     * private static Runnable startPillMinderCapture(final JPanel cameraFeed, final
     * JPanel processedFeed, final VideoCapture camera) { return () -> { final Mat
     * frame = new Mat();
     *
     * while (true) { // Read frame from camera camera.read(frame);
     *
     * // Process frame final Mat processed =
     * PillMinderCaptureUtil.processImage(frame);
     *
     * // Mark outer contour PillMinderCaptureUtil.markOuterContour(processed,
     * frame);
     *
     * // Draw current frame PillMinderCaptureUtil.drawImage(frame, cameraFeed);
     *
     * int[][] pillBoxMatrix = PillMinderCaptureUtil.getPillBoxMatrix(processed);
     *
     * // Draw current processed image (for debugging)
     * PillMinderCaptureUtil.drawImage(processed, processedFeed); try {
     * Thread.sleep(50000); } catch (InterruptedException e) { // TODO
     * Auto-generated catch block e.printStackTrace(); } } }; }
     */
}
