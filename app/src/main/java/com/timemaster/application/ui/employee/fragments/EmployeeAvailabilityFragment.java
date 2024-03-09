package com.timemaster.application.ui.employee.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.ui.employee.EmployeeActivity;

import java.util.HashSet;
import java.util.Set;

public class EmployeeAvailabilityFragment extends Fragment
{
    public static Set<String> available_days = new HashSet<>();
    public static int buttonIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee_availability, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize buttons from view
        Button addButton = view.findViewById(R.id.AddEmployeeFromAvailability);
        Button editButton = view.findViewById(R.id.EditEmployeeFromAvailability);
        Button archiveButton = view.findViewById(R.id.ArchiveEmployeeFromAvailability);
        // control visibility
        if (FragmentControl.isEditing()) {
            addButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            archiveButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            archiveButton.setVisibility(View.GONE);
        }
        // add proper functionality to each button
        EmployeeActivity employeeActivity = (EmployeeActivity) requireActivity();
        addButton.setOnClickListener(view1 -> {
            employeeActivity.addEmployeeButton();
        });
        editButton.setOnClickListener(view1 -> {
            employeeActivity.editEmployeeButton();
        });
        archiveButton.setOnClickListener(view1 -> {
            employeeActivity.archiveEmployeeButton();
        });

        int employeeID = this.getActivity().getIntent().getIntExtra("employee_id", 0);
        // this fragment has been loaded, so clear the list if null is in it.
        // the following code will add the days available from database using setChecked()
        if (available_days.contains("null"))
            available_days.clear();
        // set click listeners for each ToggleButton
        for (; buttonIndex < 13; buttonIndex++) {
            int buttonID = getResources().getIdentifier("AvailButton_" + buttonIndex, "id",getContext().getPackageName());
            ToggleButton buttonView = (ToggleButton) view.findViewById(buttonID);
            if (FragmentControl.isEditing()) {
                boolean enable = MainActivity.database.availabilityTable.readAvailability(employeeID);
                buttonView.setChecked(enable);
            }
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToggleButton b = (ToggleButton)view;
                    addAvailability((String) b.getTag(), b.isChecked());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        buttonIndex = 1;
    }

    private void addAvailability(String button, boolean enabled) {
        switch (button) {
            case "morn_mon": // monday morning
                if (enabled) available_days.add("morn_mon");
                else available_days.remove("morn_mon");
                break;
            case "morn_tue":
                if (enabled) available_days.add("morn_tue");
                else available_days.remove("morn_tue");
                break;
            case "morn_wed":
                if (enabled) available_days.add("morn_wed");
                else available_days.remove("morn_wed");
                break;
            case "morn_thu":
                if (enabled) available_days.add("morn_thu");
                else available_days.remove("morn_thu");
                break;
            case "morn_fri":
                if (enabled) available_days.add("morn_fri");
                else available_days.remove("morn_fri");
                break;
            case "aftr_mon": // monday afternoon
                if (enabled) available_days.add("aftr_mon");
                else available_days.remove("aftr_mon");
                break;
            case "aftr_tue": // tuesday afternoon
                if (enabled) available_days.add("aftr_tue");
                else available_days.remove("aftr_tue");
                break;
            case "aftr_wed": // wednesday afternoon
                if (enabled) available_days.add("aftr_wed");
                else available_days.remove("aftr_wed");
                break;
            case "aftr_thu": // thursday afternoon
                if (enabled) available_days.add("aftr_thu");
                else available_days.remove("aftr_thu");
                break;
            case "aftr_fri": // fri afternoon
                if (enabled) available_days.add("aftr_fri");
                else available_days.remove("aftr_fri");
                break;
            case "saturday": // saturday
                if (enabled) available_days.add("saturday");
                else available_days.remove("saturday");
                break;
            case "sunday": // sunday
                if (enabled) available_days.add("sunday");
                else available_days.remove("sunday");
                break;
        }
    }
}