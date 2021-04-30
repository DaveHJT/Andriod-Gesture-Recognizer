package ca.uwaterloo.cs349;

import android.content.SharedPreferences;
import android.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Gesture> newGesture;
    private MutableLiveData<ArrayList<Pair<String, Gesture>>> gestureLibrary;
    private SharedPreferences.Editor gestureEditor;
    private int gestureEditPos;

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shared model");

        newGesture = new MutableLiveData<>();

        gestureLibrary = new MutableLiveData<>();
        gestureLibrary.setValue(new ArrayList<Pair<String, Gesture>>());

        // init the gesture edit pos as null
        gestureEditPos = -1;
    }

    public MutableLiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Gesture> getNewGesture() {
        return newGesture;
    }

    public void setNewGesture(Gesture newGesture) {
        this.newGesture.setValue(newGesture);
    }

    public MutableLiveData<ArrayList<Pair<String, Gesture>>> getGestureLibrary() {
        return gestureLibrary;
    }

    public void addGestureInLibrary(String newGestureName) {
        if (newGestureName != null && newGesture != null) {
            if (isEditing()) {
                // add the new gesture to the original pos in library
                gestureLibrary.getValue().set(gestureEditPos, new Pair<>(newGestureName, newGesture.getValue()));
            } else {
                gestureLibrary.getValue().add(new Pair<>(newGestureName, newGesture.getValue()));
            }

            // clear new gesture
            resetNewGesture();

            // save the library
            saveLibraryToEditor();
        }
    }

    public void editGestureInLibrary() {
        if (gestureEditPos < gestureLibrary.getValue().size()) {
            String gestureName = gestureLibrary.getValue().get(gestureEditPos).first;
            addGestureInLibrary(gestureName);
            // deleteGesture(gestureEditPos);
            resetEditing();
        }
    }

    public void saveLibraryToEditor() {
        if (gestureEditor != null) {
            // first element is the total number of gestures
            int sizeLibrary = gestureLibrary.getValue().size();
            gestureEditor.putInt("size", sizeLibrary);

            for (int i = 0; i < sizeLibrary; i++) {
                Pair<String, Gesture> pair = gestureLibrary.getValue().get(i);
                String iStr = String.valueOf(i);
                // save the name
                gestureEditor.putString("name" + iStr, pair.first);
                gestureEditor.putString("path" + iStr, pair.second.serialize());
                gestureEditor.commit();
            }
        }
    }

    public void loadLibraryFromPreference(SharedPreferences gestureLibraryPref) {
        // clear the library
        gestureLibrary.getValue().clear();

        // first element is the total number of gestures
        int sizeLibrary = gestureLibraryPref.getInt("size", 0);

        for (int i = 0; i < sizeLibrary; i++) {
            String iStr = String.valueOf(i);

            // the gesture name ERROR if nothing found
            String defValue = "ERROR";
            // get the data from preference
            String gestureName = gestureLibraryPref.getString("name" + iStr, defValue);
            String serializedGesture = gestureLibraryPref.getString("path" + iStr, defValue);

            // cast the json string to Path object
            Gesture gesturePath = new Gesture(serializedGesture);

            Pair<String, Gesture> pair = new Pair<>(gestureName, gesturePath);

            // add the gesture to library
            gestureLibrary.getValue().add(pair);
        }
    }

    public void setGestureEditor(SharedPreferences.Editor gestureEditor) {
        this.gestureEditor = gestureEditor;
    }

    // delete the gesture at pos in gesture library
    public void deleteGesture(int pos) {
        if (pos < gestureLibrary.getValue().size()) {
            gestureLibrary.getValue().remove(pos);
            saveLibraryToEditor();

            // reset
            resetEditing();

            //notify the observers
            gestureLibrary.setValue(gestureLibrary.getValue());
        }
    }

    // rename the gesture at pos in gesture library
    public void renameGesture(int pos, String newName) {
        if (pos < gestureLibrary.getValue().size()) {
            Pair<String, Gesture> oldPair = gestureLibrary.getValue().get(pos);
            Pair<String, Gesture> newPair = new Pair<>(newName, oldPair.second);
            gestureLibrary.getValue().set(pos, newPair);
            saveLibraryToEditor();

            //notify the observers
            gestureLibrary.setValue(gestureLibrary.getValue());
        }
    }

    //  set the name and let user edit the gesture
    public void editGesture(int pos) {
        if (pos < gestureLibrary.getValue().size()) {
            gestureEditPos = pos;
        }
    }

    public void resetNewGesture() {
        newGesture.setValue(null);

        // notify observers
        newGesture.setValue(newGesture.getValue());
    }

    public void resetEditing() {
        gestureEditPos = -1;
    }

    public String getEditingGestureName() {
        if (gestureEditPos < gestureLibrary.getValue().size()) {
            return gestureLibrary.getValue().get(gestureEditPos).first;
        }
        return null;
    }

    public String getGestureNameInLibrary(int pos) {
        if (gestureEditPos < gestureLibrary.getValue().size()) {
            return gestureLibrary.getValue().get(pos).first;
        }
        return null;
    }

    public boolean isEditing() {
        if (gestureEditPos == -1) {
            return false;
        } else {
            return true;
        }
    }

    // return the top 3 gestures with lowest distance offset of matching
    public ArrayList<Pair<String, Gesture>> getMatchingResult(Gesture gestureTarget) {
        ArrayList<Pair<String, Gesture>> topMatches = new ArrayList<>();
        final int topSize = 3;
        for (Pair<String, Gesture> gesturePair : gestureLibrary.getValue()) {
            Gesture gestureMatching = gesturePair.second;
            gestureMatching.updateDistOffset(gestureTarget);
            float distOffset = gestureMatching.getOffsetScore();
            // System.out.println(gesturePair.first + " : " + distOffset);

            for (int i = 0; i < topSize; i++) {
                if (topMatches.size() <= i || distOffset < topMatches.get(i).second.getOffsetScore()) {
                    topMatches.add(i, new Pair(gesturePair.first, gesturePair.second));
                    if (topMatches.size() > topSize) {
                        topMatches.remove(topSize);
                    }
                    break;
                }
            }
        }
        return topMatches;
    }
}