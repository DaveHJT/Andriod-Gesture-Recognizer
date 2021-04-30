package ca.uwaterloo.cs349;

import android.graphics.Path;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LibraryFragment extends Fragment implements LibraryRecyclerAdapter.ItemClickListener {

    private SharedViewModel mViewModel;
    private LibraryRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);

        // set up library recycler view
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_library);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Pair<String, Gesture>> gestureLibrary = mViewModel.getGestureLibrary().getValue();
        adapter = new LibraryRecyclerAdapter(getContext(), gestureLibrary, this);
        recyclerView.setAdapter(adapter);
        // recycler view style
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setPadding(50, 0, 0, 200);

        // monitor the change in gesture library
        mViewModel.getGestureLibrary().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                adapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onItemClick(View view, int pos) {
        EditGestureDialog editGestureDialog = new EditGestureDialog(
                new EditGestureActionOnClick(), pos, mViewModel.getGestureNameInLibrary(pos));
        String dialogTag = "dialog for editing the gesture";
        editGestureDialog.show(requireActivity().getSupportFragmentManager()
                , dialogTag);
    }

    public class EditGestureActionOnClick implements DialogActionOnClick{
        @Override
        public void onClick(String textInput, int pos) {
            final String edit = "EDIT";
            final String rename = "RENAME";
            final String delete = "DELETE";

            switch (textInput) {
                case rename:
                    // ask the name of the new gesture
                    InputTextDialog textDialog = new InputTextDialog(
                            R.string.dialog_ask_gesture_rename, new AskRenameActionOnClick(), pos);
                    String dialogTag = "dialog asking for the new name of the gesture";
                    textDialog.show(requireActivity().getSupportFragmentManager()
                            , dialogTag);
                    break;
                case edit:
                    mViewModel.editGesture(pos);
                    // clear the addition pending gesture
                    mViewModel.resetNewGesture();
                    // jump to the addition page
                    NavHostFragment.findNavController(LibraryFragment.this)
                            .navigate(R.id.action_navigation_library_to_navigation_addition);
                    break;
                case delete:
                    mViewModel.deleteGesture(pos);
                    break;
            }
        }
    }

    public class AskRenameActionOnClick implements DialogActionOnClick{
        @Override
        public void onClick(String textInput, int id) {
            mViewModel.renameGesture(id, textInput);
        }
    }
}