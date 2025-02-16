package edu.ucsd.cse110.habitizer.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.habitizer.app.ui.editroutine.EditRoutineFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.RoutineTaskFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.TaskListFragment;


public class MainActivity extends AppCompatActivity {
    private MainViewModel activityModel;
    private ActivityMainBinding view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        // Initialize the Model
        var modelOwner = this;
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setUpObservers();
        setContentView(view.getRoot());
    }


    private void setUpObservers() {
        activityModel.getScreen().observe(screen -> {
            if (screen == null) return;
            swapFragments(screen);
        });
    }

    private void swapFragments(Screen screen) {
        Fragment fragment = switch (screen) {
            case PREVIEW_SCREEN -> TaskListFragment.newInstance();
            case ACTIVE_ROUTINE_SCREEN -> RoutineTaskFragment.newInstance();
            case EDIT_ROUTINE_SCREEN -> EditRoutineFragment.newInstance();
        };

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}