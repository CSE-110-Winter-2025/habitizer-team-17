package edu.ucsd.cse110.habitizer.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
    }
}