package ca.uwaterloo.cs349;

import android.animation.FloatEvaluator;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.animation.PathInterpolator;

import java.util.ArrayList;

public class Gesture {
    private Path path;
    private ArrayList<PointF> points;
    private ArrayList<PointF> normalizedPoints;
    private ArrayList<PointF> reversedNormalizedPoints;
    private Float length;
    private final int sampleSize = 128;
    private final String DELIMITER = ",";
    private final float standardDist = 200;
    private float offsetScore = Float.POSITIVE_INFINITY;

    Gesture() {
        path = new Path();
        points = new ArrayList<>();
        length = 0f;
    }

    // set start point
    public void moveTo(float x, float y) {
        path.moveTo(x, y);
        points.add(new PointF(x, y));
        length = 0f;
    }

    // continue to draw line
    public void lineTo(float x, float y) {
        path.lineTo(x, y);
        PointF lastPoint = points.get(points.size() - 1);
        PointF curPoint = new PointF(x, y);

        length += calcDist(lastPoint, curPoint);
        points.add(curPoint);

        // System.out.println("Length: " + length);
    }

    public Float getLength() {
        return length;
    }

    public ArrayList<PointF> getPoints() {
        return points;
    }

    // Take samples uniformly along the path and replace the path and points with
    // newly sampled path and points.
    public void samplePath() {
        float sampleInterval = length / sampleSize;
        FloatEvaluator evaluator = new FloatEvaluator();

        ArrayList<PointF> sampledPoints = new ArrayList<>();
        Path sampledPath = new Path();

        if (length == 0) { // means that the path is 1 dimensional
            sampledPath = path;
            PointF onlyPoint = points.get(0);
            for (int i = 0; i < sampleSize + 1; i++) {
                sampledPoints.add(new PointF(onlyPoint.x, onlyPoint.y));
            }
        } else {

            float newDist = sampleInterval;

            for (int i = 0; i < points.size(); i++) {
                PointF oldPoint = points.get(i);
                if (i == 0) {
                    sampledPath.moveTo(oldPoint.x, oldPoint.y);
                    sampledPoints.add(oldPoint);
                } else {
                    PointF lastOldPoint = points.get(i - 1);
                    float oldDist = calcDist(oldPoint, lastOldPoint);
                    while (oldDist >= newDist) {
                        float fraction = newDist / oldDist;
                        float newX = evaluator.evaluate(fraction, lastOldPoint.x, oldPoint.x);
                        float newY = evaluator.evaluate(fraction, lastOldPoint.y, oldPoint.y);
                        PointF newPoint = new PointF(newX, newY);
                        sampledPath.lineTo(newX, newY);
                        sampledPoints.add(newPoint);

                        lastOldPoint = newPoint;
                        oldDist -= newDist;

                        // reset the distance needed to sample
                        newDist = sampleInterval;
                    }
                    newDist -= oldDist;
                }
            }
        }

        // replace the old path and points
        path = sampledPath;
        points = sampledPoints;

        // automatically normalize the points since the points
        // are sampled and will not changed later on
        normalizePoints();
    }

    private void normalizePoints() {
        normalizedPoints = normalizePoints(NormalizeOrder.NORMAL);
        reversedNormalizedPoints = normalizePoints(NormalizeOrder.REVERSED);
    }

    // fill the normalizedPoints with points that are scaled and rotated to standard form
    private ArrayList<PointF> normalizePoints(NormalizeOrder order) {
        ArrayList<PointF> normalizeResult = null;
        if (points.size() > 0) {
            PointF startPoint = new PointF();
            if (order == NormalizeOrder.NORMAL) {
                startPoint = points.get(0);
            } else if (order == NormalizeOrder.REVERSED) {
                startPoint = points.get(points.size() - 1);
            }

            PointF centroid = new PointF(0, 0);
            // calculate centroid by take the mean of all points
            for (PointF p : points) {
                centroid.x += p.x;
                centroid.y += p.y;
            }
            centroid.x /= sampleSize;
            centroid.y /= sampleSize;

            // difference between start point and centroid
            float diffX = startPoint.x - centroid.x;
            float diffY = startPoint.y - centroid.y;

            // calculate angle and rotate
            float radAngle = (float) Math.atan((double)diffY / diffX);
            // rotate 180 more if the diffX is less than 0
            if (diffX < 0) radAngle += (float)Math.PI;
            // rotate in reverse direction to normalize the angle
            radAngle *= -1;
            float degAngle = radAngle * 180 / (float)Math.PI;
            Matrix matrix = new Matrix();
            matrix.setRotate(degAngle, centroid.x, centroid.y);
            normalizeResult = pointsOperation(points, matrix);

            // translate the centroid to (0, 0)
            matrix.reset();
            matrix.setTranslate(-centroid.x, -centroid.y);
            normalizeResult = pointsOperation(normalizeResult, matrix);
            centroid.x = 0;
            centroid.y = 0;
            startPoint = normalizeResult.get(0);

            // find the size of the bounding box
            float maxDist = 0;
            for (PointF p : normalizeResult) {
                if (Math.abs(p.x) > maxDist) maxDist = Math.abs(p.x);
                if (Math.abs(p.y) > maxDist) maxDist = Math.abs(p.y);
            }
            // scale to the standard size
            matrix.reset();
            if (maxDist != 0) {
                float ratio = standardDist / maxDist;
                matrix.setScale(ratio, ratio);
                normalizeResult = pointsOperation(normalizeResult, matrix);
            }

            // DEBUG: translate the centroid to the center
            //matrix.reset();
            //matrix.setTranslate((float)1070 / 2, (float)1630 / 2);
            //normalizedPoints = pointsOperation(normalizedPoints, matrix);
        }
        return normalizeResult;
    }

    // returns the mean distance offset of matching
    public void updateDistOffset(Gesture anotherGesture) {
        offsetScore = Float.POSITIVE_INFINITY;
        ArrayList<PointF> anotherPoints = anotherGesture.getNormalizedPoints();
        if (normalizedPoints == null || reversedNormalizedPoints == null) {
            normalizePoints();
        }
        if (anotherPoints != null && anotherPoints.size() >= sampleSize
        && normalizedPoints != null && normalizedPoints.size() >= sampleSize) {
            float offset = 0;
            for (int i = 0; i < sampleSize; i++) {
                offset += calcDist(normalizedPoints.get(i), anotherPoints.get(i));
            }

            float reversedOffset = 0;
            for (int i = 0; i < sampleSize; i++) {
                int reversedI = sampleSize - i - 1;
                reversedOffset += calcDist(reversedNormalizedPoints.get(reversedI), anotherPoints.get(i));
            }

            offsetScore = Math.min(offset, reversedOffset);

        } else {
            offsetScore = offsetScore;
        }
    }

    public float getOffsetScore() {
        return offsetScore;
    }

    protected ArrayList<PointF> getNormalizedPoints() {
        return normalizedPoints;
    }

    // do the matrix operation on each of the points
    private ArrayList<PointF> pointsOperation(ArrayList<PointF> srcPoints, Matrix matrix) {
        ArrayList<PointF> dstPoints = new ArrayList<>();
        float[] srcPointFloat;
        float[] dstPointFloat;
        for (PointF p : srcPoints) {
            srcPointFloat = new float[] { p.x, p.y };
            dstPointFloat = new float[] { p.x, p.y };
            matrix.mapPoints(dstPointFloat, srcPointFloat);
            dstPoints.add(new PointF(dstPointFloat[0], dstPointFloat[1]));
        }
        return dstPoints;
    }

    // calculate the distance between the two points
    public float calcDist(PointF p1, PointF p2) {
        return (float)Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public Path getPath() {
        return path;
    }

    public String serialize() {
        String serializedGesture = new String();
        int size = points.size();
        serializedGesture += size + DELIMITER;
        for (PointF p : points) {
            serializedGesture += p.x + DELIMITER;
            serializedGesture += p.y + DELIMITER;
        }
        return serializedGesture;
    }

    // reconstruct the gesture from a serialized string
    public Gesture(String serializedGesture) {
        path = new Path();
        points = new ArrayList<>();
        length = 0f;

        String values[] = serializedGesture.split(DELIMITER);
        int idx = 0;
        int size = Integer.parseInt(values[idx]);
        idx += 1;
        for (int i = 0; i < size; i++) {
            float x = Float.parseFloat(values[idx]);
            idx += 1;
            float y = Float.parseFloat(values[idx]);
            idx += 1;

            if (i == 0) {
                moveTo(x, y);
            } else {
                lineTo(x, y);
            }
        }
    }

    enum NormalizeOrder{NORMAL, REVERSED}
}
