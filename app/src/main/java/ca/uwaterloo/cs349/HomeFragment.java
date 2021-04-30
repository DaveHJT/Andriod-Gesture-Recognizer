package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SharedViewModel mViewModel;
    private RecognitionDrawingView recognitionDrawingView;
    private ArrayList<ThumbnailView> topMatchThumbnails;
    private ArrayList<TextView> topMatchNames;
    private ArrayList<LinearLayout> topMatchWindows;
    private View root;
    final int topSize = 3;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        this.root = root;

        // get the draw status in view model, whether the new gesture is drawn
        Gesture newGesture = mViewModel.getNewGesture().getValue();

        // set the instruction message
        final TextView instructionMessage = root.findViewById(R.id.text_home);
        final String defaultInstruction = "Draw a gesture here";
        instructionMessage.setText(defaultInstruction);

        // setup canvas: from cs349 sample code: panzoom
        // setup custom ImageView class that captures strokes
        // and draws them over the background image
        recognitionDrawingView = new RecognitionDrawingView(getContext());
        recognitionDrawingView.setMinimumHeight(2000);
        recognitionDrawingView.setMinimumWidth(1000);

        // disabled before the start draw button is clicked
        recognitionDrawingView.setDrawEnabled(true);

        // add to layout
        LinearLayout layout = root.findViewById(R.id.canvas_home);
        layout.addView(recognitionDrawingView);

        // set up the matches table
        TableRow matchesTable = root.findViewById(R.id.matches_table_home);

        topMatchNames = new ArrayList<>();
        topMatchThumbnails = new ArrayList<>();
        topMatchWindows = new ArrayList<>();

        // three top matches thumbnails
        ThumbnailView topMatchThumbnail1 = new ThumbnailView(root.getContext());
        ThumbnailView topMatchThumbnail2 = new ThumbnailView(root.getContext());
        ThumbnailView topMatchThumbnail3 = new ThumbnailView(root.getContext());
        topMatchThumbnails.add(topMatchThumbnail1);
        topMatchThumbnails.add(topMatchThumbnail2);
        topMatchThumbnails.add(topMatchThumbnail3);

        // get the layouts in xml files
        TextView topMatchName1 = root.findViewById(R.id.match1_name_home);
        TextView topMatchName2 = root.findViewById(R.id.match2_name_home);
        TextView topMatchName3 = root.findViewById(R.id.match3_name_home);
        topMatchNames.add(topMatchName1);
        topMatchNames.add(topMatchName2);
        topMatchNames.add(topMatchName3);

        LinearLayout topMatchWindow1 = root.findViewById(R.id.match1_home);
        LinearLayout topMatchWindow2 = root.findViewById(R.id.match2_home);
        LinearLayout topMatchWindow3 = root.findViewById(R.id.match3_home);
        topMatchWindow1.addView(topMatchThumbnail1, 1);
        topMatchWindow2.addView(topMatchThumbnail2, 1);
        topMatchWindow3.addView(topMatchThumbnail3, 1);
        topMatchWindows.add(topMatchWindow1);
        topMatchWindows.add(topMatchWindow2);
        topMatchWindows.add(topMatchWindow3);


        return root;
    }

    public void hideInstructionMessage() {
        TextView instructionMessage = getView().findViewById(R.id.text_home);
        instructionMessage.setVisibility(View.INVISIBLE);
    }

    public void showInstructionMessage() {
        TextView instructionMessage = getView().findViewById(R.id.text_home);
        instructionMessage.setVisibility(View.VISIBLE);
    }

    public void clearTopMatcheWindows() {
        for (int i = 0; i < topSize; i++) {
            topMatchThumbnails.get(i).clearGesture();
            topMatchNames.get(i).setVisibility(View.INVISIBLE);
            topMatchWindows.get(i).setVisibility(View.INVISIBLE);
        }
    }

    public class RecognitionDrawingView extends DrawingView {

        public RecognitionDrawingView(Context context) {
            super(context);
        }

        @Override
        public void submitDrawing() {
            if (newGesture.getPoints().size() > 1) { // avoid single point
                newGesture.samplePath();
                ArrayList<Pair<String, Gesture>> topMatches = mViewModel.getMatchingResult(newGesture);
                if (topMatches.size() <= topSize) {
                    for (int i = 0; i < topMatches.size(); i++) {
                        topMatchNames.get(i).setText(topMatches.get(i).first);
                        topMatchNames.get(i).setVisibility(VISIBLE);
                        topMatchThumbnails.get(i).setGesture(topMatches.get(i).second);
                        topMatchWindows.get(i).setVisibility(VISIBLE);
                    }
                }
            } else {
                showInstructionMessage();
            }
        }

        @Override
        public void startDrawing() {
            hideInstructionMessage();
            clearTopMatcheWindows();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (newGesture != null) {
                canvas.drawPath(newGesture.getPath(), paintbrush);

                // DEBUG: draw the points
                //for (PointF point : newGesture.getPoints()) {
                //    canvas.drawPoint(point.x, point.y, paintbrush);
                //}

                // DEBUG: draw the normalized points
                // ArrayList<PointF> normalizedPoints = newGesture.getNormalizedPoints();
                //if (normalizedPoints != null) {
                //    for (PointF point : normalizedPoints) {
                //        canvas.drawPoint(point.x, point.y, paintbrush);
                //    }
                //}
            }
        }
    }
}