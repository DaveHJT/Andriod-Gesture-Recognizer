package ca.uwaterloo.cs349;


import android.content.Context;
import android.graphics.Path;
import android.text.Layout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// referenced the code example in this tutorial:
// https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

public class LibraryRecyclerAdapter extends RecyclerView.Adapter<LibraryRecyclerAdapter.ViewHolder> {

    private ArrayList<Pair<String, Gesture>> gestureLibrary;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    // data is passed into the constructor
    LibraryRecyclerAdapter(Context context, ArrayList<Pair<String, Gesture>> gestureLibrary, ItemClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.gestureLibrary = gestureLibrary;
        this.clickListener = clickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row_library, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView and ImageView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set name
        String gestureName = gestureLibrary.get(position).first;
        holder.gestureName.setText(gestureName);

        // set thumbnail
        Gesture gesture = gestureLibrary.get(position).second;
        holder.gestureThumbnail.setGesture(gesture);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return gestureLibrary.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView gestureName;
        ThumbnailView gestureThumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            gestureName = itemView.findViewById(R.id.gesture_name);
            gestureThumbnail = new ThumbnailView(itemView.getContext());

            LinearLayout gestureInfo = itemView.findViewById(R.id.gesture_info);
            gestureInfo.addView(gestureThumbnail, 0);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int pos);
    }
}