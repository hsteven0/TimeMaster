package com.timemaster.application.ui.employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.timemaster.application.MainActivity;
import com.timemaster.application.R;

/*
* This class displays all employees in a list format. Utilizes RecyclerView to format
* employee information.
* */
public class ViewEmployeesActivity extends AppCompatActivity {
    private EmployeeRecycler employeeRecycler;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employees);
        // read from database to initialize the EmployeeManager employeeList
        MainActivity.database.employeeTable.readDatabase();

        // display the employee with the help of recycler format
        employeeRecycler = new EmployeeRecycler(this);
        recyclerView = findViewById(R.id.ViewEmployeeList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(employeeRecycler);
        setRecyclerAnimations();

        FloatingActionButton addEmployees = findViewById(R.id.AddEmployeeButton);
        // Set the default background color
        int defaultBackgroundColor = ContextCompat.getColor(this, R.color.orange_500);
        addEmployees.setBackgroundTintList(android.content.res.ColorStateList.valueOf(defaultBackgroundColor));
        // Set the ripple color
        int rippleColor = ContextCompat.getColor(this, R.color.orange_700);
        addEmployees.setRippleColor(rippleColor);
        // Link functionality to the button
        addEmployees.setOnClickListener(view -> redirectToEmployeeCreationPage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerAnimations();
    }

    @Override
    public void onBackPressed() {
        this.redirectToMain();
    }

    private void setRecyclerAnimations() {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(controller);
    }

    private void refreshRecyclerAnimations() {
        recyclerView.scheduleLayoutAnimation();
    }

    private void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void redirectToEmployeeCreationPage() {
        Intent intent = new Intent(this, EmployeeActivity.class);
        startActivity(intent);
    }
}