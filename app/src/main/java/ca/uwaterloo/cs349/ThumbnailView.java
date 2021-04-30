package ca.uwaterloo.cs349;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.graphics.*;
        import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class ThumbnailView extends ImageView {
    Gesture gesture;
    Paint paintbrush = new Paint(Color.BLUE);
    final int strokeWidth = 30;
    final int thumbnail_size = 256;
    final float thumbnail_ratio = 0.12f;
    final float leftPadding = 30f;

    // constructor
    public ThumbnailView(Context context) {
        super(context);
        paintbrush.setStyle(Paint.Style.STROKE);
        paintbrush.setStrokeWidth(strokeWidth);
        // init the size
        setMinimumHeight(thumbnail_size);
        setMinimumWidth(thumbnail_size);
        setMaxHeight(thumbnail_size);
        setMaxWidth(thumbnail_size);

        // debug
        // setBackgroundColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gesture != null) {
            canvas.translate(leftPadding, 0);
            canvas.scale(thumbnail_ratio, thumbnail_ratio);
            canvas.drawPath(gesture.getPath(), paintbrush);
        }
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
        invalidate();
    }

    public void clearGesture() {
        gesture = null;
    }
}
