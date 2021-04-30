package ca.uwaterloo.cs349;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.widget.ImageView;


// copied from cs349 sample code: panzoom

@SuppressLint("AppCompatCustomView")
public abstract class DrawingView extends ImageView {

    // drawing
    private boolean drawEnabled = true;
    Gesture newGesture = null;
    Paint paintbrush = new Paint(Color.BLUE);
    Bitmap background;
    final int strokeWidth = 15;

    // constructor
    public DrawingView(Context context) {
        super(context);
        paintbrush.setStyle(Paint.Style.STROKE);
        paintbrush.setStrokeWidth(strokeWidth);
    }

    float x, y;
    int p_id, p_index;

    // capture touch events (down/move/up) to create a path/stroke that we draw later
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawEnabled) {
            p_id = event.getPointerId(0);
            p_index = event.findPointerIndex(p_id);

            // mapPoints returns values in-place
            float[] coordinate = new float[]{event.getX(p_index), event.getY(p_index)};
            x = coordinate[0];
            y = coordinate[1];

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startDrawing();

                    newGesture = new Gesture();
                    newGesture.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // end early if the line go out of the boundary
                    if (x < 0 || y < 0 || x > getMeasuredWidth() ||
                    y > getMeasuredHeight()) {
                        submitDrawing();
                    } else {
                        newGesture.lineTo(x, y);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    submitDrawing();
                    break;
            }

            // force drawing
            invalidate();
        }
        return true;
    }

    public abstract void submitDrawing();
    public abstract void startDrawing();

    // set image as background
    public void setImage(Bitmap bitmap) {
        this.background = bitmap;
    }

    public void setDrawEnabled(boolean drawEnabled) {
        this.drawEnabled = drawEnabled;
    }

    public void reset() {
        newGesture = null;
        // drawEnabled = true;
        invalidate();
    }
}
