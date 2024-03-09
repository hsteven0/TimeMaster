package com.timemaster.application.ui.employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.ui.employee.fragments.EmployeeCreationFragment;
import com.timemaster.application.ui.employee.fragments.FragmentControl;

/*
* This class will control the addition of Employee objects and save them to a database.
* User interface defined in activity_employee.xml
* */
public class EmployeeActivity extends AppCompatActivity implements EmployeeCreationFragment.IFragmentListener
{
    private TabLayoutMediator tabMediator;
    private EmployeeCreationFragment employeeCreationFragment;
    public static TabLayout tabs;
    public static ViewPager2 viewPager2;
    public static Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.database.employeeTable.readDatabase();
        setContentView(R.layout.activity_employee);
        FragmentControl.employeeCreation = new EmployeeCreationFragment();
        if (FragmentControl.isEditing())
            setTitle("Employee Editing");
        else setTitle("Employee Creation");

        // set up fragments
        tabs = findViewById(R.id.EmployeeActivityTabs);
        viewPager2 = findViewById(R.id.EmployeeActivityViewPager);
        FragmentControl fragmentControl = new FragmentControl(this);
        viewPager2.setAdapter(fragmentControl);
        viewPager2.setUserInputEnabled(false);

        // tab stuff
        tabMediator = new TabLayoutMediator(tabs, viewPager2,
                (tab, position) -> {
                    // title of the tabs
                    switch (position) {
                        case 0:
                            if (FragmentControl.isEditing()) tab.setText("Edit General");
                            else tab.setText("General");
                            break;
                        default:
                            if (FragmentControl.isEditing()) tab.setText("Edit Availability");
                            else tab.setText("Availability");
                    }
                });
        tabMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        employeeCreationFragment = FragmentControl.employeeCreation;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabMediator.detach(); // free memory
    }

    @Override
    public void addEmployeeButton() {
        if (employeeCreationFragment != null) {
            employeeCreationFragment.addEmployeeButton();
        }
    }

    @Override
    public void editEmployeeButton() {
        if (employeeCreationFragment != null) {
            employeeCreationFragment.editEmployeeButton();
        }
    }

    @Override
    public void archiveEmployeeButton() {
        if (employeeCreationFragment != null) {
            employeeCreationFragment.archiveEmployeeButton();
        }
    }
}