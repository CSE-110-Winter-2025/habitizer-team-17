package edu.ucsd.cse110.habitizer.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.RoutineTaskFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.TaskListFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;

    private MainViewModel model;

    private MutableLiveData<Screen> screen;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        var modelOwner = this;
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        model = modelProvider.get(MainViewModel.class);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setUpObservers();
        setContentView(view.getRoot());
    }



    private void setUpObservers(){
        System.out.println("set up observers");
        var app = (HabitizerApplication) getApplication();
        screen = app.getScreen();
        screen.observeForever(screen -> {
            if(screen == null) return;
            swapFragments(screen);
        });
    }

    private void swapFragments(Screen screen){
        System.out.println("it is called, screen is: " + screen.toString());
        Fragment fragment;
        switch(screen){

            case PREVIEW_SCREEN:
                fragment = TaskListFragment.newInstance();
                break;
            case ACTIVE_ROUTINE_SCREEN:
                fragment = RoutineTaskFragment.newInstance();
                break;

            default:
                fragment = TaskListFragment.newInstance();
                break;
        }

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
}