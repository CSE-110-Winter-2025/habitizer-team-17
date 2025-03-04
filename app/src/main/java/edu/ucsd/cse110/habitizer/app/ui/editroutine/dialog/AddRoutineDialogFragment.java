package edu.ucsd.cse110.habitizer.app.ui.editroutine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogAddRoutineBinding;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogAddTaskBinding;

public class AddRoutineDialogFragment extends DialogFragment {

    private FragmentDialogAddRoutineBinding view;
    private MainViewModel activityModel;

    public AddRoutineDialogFragment() {
        // Required empty public constructor
    }
    public static AddRoutineDialogFragment newInstance(){
        var fragment = new AddRoutineDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        this.view = FragmentDialogAddRoutineBinding.inflate(getLayoutInflater());

        var dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Add Routine")
                .setMessage("Please provide the new routine name.")
                .setView(view.getRoot())
                .setPositiveButton("Add", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();

        dialog.setOnShowListener(l -> {
            var positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);

            view.addRoutineNameText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    positiveButton.setEnabled(!editable.toString().isBlank());
                }
            });
        });

        return dialog;
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which){
        var name = view.addRoutineNameText.getText().toString().strip();
        activityModel.addRoutineToEnd(name);
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which){
        dialog.cancel();
    }
}
