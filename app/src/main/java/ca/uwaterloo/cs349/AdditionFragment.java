package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

public class AdditionFragment extends Fragment implements DialogActionOnClick {

    private SharedViewModel mViewModel;
    private AdditionDrawingView pageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_addition, container, false);

        // get the draw status in view model, whether the new gesture is drawn
        Gesture newGesture = mViewModel.getNewGesture().getValue();
        Boolean newGestureDrawn = false;
        if (newGesture != null) newGestureDrawn = true;

        // set the instruction message
        final TextView instructionMessage = root.findViewById(R.id.text_addition);
        final String defaultInstruction = "Draw a new gesture here";
        final String editInstruction = "Draw a new gesture for: ";
        instructionMessage.setText(defaultInstruction);

        // setup canvas: from cs349 sample code: panzoom
        // setup custom ImageView class that captures strokes
        // and draws them over the background image
        pageView = new AdditionDrawingView(getContext());
        pageView.setMinimumHeight(2000);
        pageView.setMinimumWidth(1000);

        // disabled before the start draw button is clicked
        pageView.setDrawEnabled(true);

        // add to layout
        LinearLayout layout = root.findViewById(R.id.canvas_addition);
        layout.addView(pageView);

        // set up the reset button
        final Button buttonReset = root.findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // clear the new gesture and editing status
                mViewModel.resetNewGesture();
                mViewModel.resetEditing();
                instructionMessage.setText(defaultInstruction);
            }
        });

        final DialogActionOnClick actionOnClick = this;

        // set up the add button
        final Button buttonAdd = root.findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mViewModel.isEditing()) {
                    // ask the name of the new gesture
                    InputTextDialog dialog = new InputTextDialog(R.string.dialog_ask_gesture_name
                            , actionOnClick, -1);
                    String dialogTag = "dialog asking for the name of the new gesture";
                    dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                            , dialogTag);

                    // clear the new gesture
                    // mViewModel.setNewGesture(null);
                } else {
                    mViewModel.editGestureInLibrary();
                    // mViewModel.resetEditing();
                    instructionMessage.setText(defaultInstruction);
                }

            }
        });


        // show the two choices to the new gesture: add or reset
        mViewModel.getNewGesture().observe(getViewLifecycleOwner(), new Observer<Gesture>() {
            @Override
            public void onChanged(@Nullable Gesture newGesture) {
                if (newGesture != null) {
                    // disable drawing until the new gesture is saved or reset
                    pageView.setDrawEnabled(false);
                    // set the two choices to the new gesture: add or reset buttons visible
                    buttonAdd.setVisibility(View.VISIBLE);
                    buttonReset.setVisibility(View.VISIBLE);
                } else {
                    // enable drawing until the new gesture is saved or reset
                    pageView.reset();

                    // hide the choices
                    buttonAdd.setVisibility(View.GONE);
                    buttonReset.setVisibility(View.GONE);

                    // enable drawing and show instruction
                    instructionMessage.setVisibility(View.VISIBLE);
                    pageView.setDrawEnabled(true);

                    if (mViewModel.isEditing()) {
                        instructionMessage.setText(editInstruction
                                + mViewModel.getEditingGestureName());
                    }

                    // then this fragment is back to the start state
                }
            }
        });

        // show the existing new gesture if it's drawn and not saved
        if (newGestureDrawn) {
            instructionMessage.setVisibility(View.INVISIBLE);
        }

        return root;
    }

    public void hideInstructionMessage() {
        TextView instructionMessage = getView().findViewById(R.id.text_addition);
        instructionMessage.setVisibility(View.INVISIBLE);
    }

    public void showInstructionMessage() {
        TextView instructionMessage = getView().findViewById(R.id.text_addition);
        instructionMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String textInput, int id) {
        mViewModel.addGestureInLibrary(textInput);
    }

    public class AdditionDrawingView extends DrawingView{

        public AdditionDrawingView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // draw background
            if (background != null) {
                this.setImageBitmap(background);
            }

            // draw lines over it
            if (newGesture == null) {
                newGesture = mViewModel.getNewGesture().getValue();
            }
            if (newGesture != null) {
                canvas.drawPath(newGesture.getPath(), paintbrush);

                // DEBUG: draw the points
                //for (PointF point : newGesture.getPoints()) {
                //    canvas.drawPoint(point.x, point.y, paintbrush);
                //}
            }
        }

        // normalized the gesture just drawn and submit to the module
        @Override
        public void submitDrawing() {
            if (newGesture.getPoints().size() > 1) { // avoid single point
                // normalize the new gesture when drawing is done
                newGesture.samplePath();

                mViewModel.setNewGesture(newGesture);
            } else {
                showInstructionMessage();
            }
        }

        // hide the instruction message when drawing start
        @Override
        public void startDrawing() {
            hideInstructionMessage();
        }
    }
}