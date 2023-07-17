package com.sreeginy.todolist;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sreeginy.todolist.Model.ToDo;
import com.sreeginy.todolist.Utils.DatabaseHelper;
public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";
    private EditText mEditText;
    private Button mSaveButton;
    public ImageView imageView;
    private DatabaseHelper myDB;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.add_newtask, container, false);
       return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditText = view.findViewById(R.id.newTask);
        mSaveButton = view.findViewById(R.id.save);
        imageView = view.findViewById(R.id.cancelButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        myDB = new DatabaseHelper(getActivity());
        boolean isUpdate = false;
        Bundle bundle = getArguments();
        if(bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            mEditText.setText(task);

            if (task.length() > 0 ) {
                mSaveButton.setEnabled(false);
            }
        }
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(Color.GRAY);
                }else {
                    mSaveButton.setEnabled(true);
                    mSaveButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();

                if(finalIsUpdate) {
                    myDB.updateTask(bundle.getInt("id"), text);
                }else  {
                    ToDo item = new ToDo();
                    item.setTask(text);
                    item.setStatus(0);
                    myDB.insertTask(item);
                }
                dismiss();
            }
        });
    }


//    public void cancel(View view) {
//        // Perform the desired action when the cancel button is clicked
//        // For example, you can close the current activity or dismiss a dialog
//    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
    }
}
}
