package ca.uwaterloo.cs349;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences gestureLibraryPref;
    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        gestureLibraryPref = getSharedPreferences("gesture library", MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        SharedPreferences.Editor gestureEditor = gestureLibraryPref.edit();
        viewModel.setGestureEditor(gestureEditor);


        // DEBUG: clear all the gestures in library
        // gestureEditor.clear();
        // gestureEditor.commit();
    }

    // save the gesture library on pause
    @Override
    protected void onPause() {
        super.onPause();
        viewModel.saveLibraryToEditor();
    }

    // load the gesture library on pause
    @Override
    protected void onResume() {
        super.onResume();
        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.loadLibraryFromPreference(gestureLibraryPref);
    }
}