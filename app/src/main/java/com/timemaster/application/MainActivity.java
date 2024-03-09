package com.timemaster.application;

import static com.timemaster.application.ui.calendar.CalendarUtils.daysInMonthArray;
import static com.timemaster.application.ui.calendar.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timemaster.application.database.Database;
import com.timemaster.application.employee.EmployeeManager;
import com.timemaster.application.ui.calendar.CalendarAdapter;
import com.timemaster.application.ui.calendar.CalendarUtils;
import com.timemaster.application.ui.calendar.WeekViewActivity;
import com.timemaster.application.ui.employee.ViewEmployeesActivity;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    public static Database database;
    public static EmployeeManager employeeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // add icon to action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setIcon(R.mipmap.icon128);

        // initialize Database and EmployeeManager
        employeeManager = new EmployeeManager();
        database = new Database(this);

        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        MainActivity.database.schedulingTable.readDates(
                CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                CalendarUtils.getScheduleTime());
        setMonthView();

        Button viewEmployeeButton = findViewById(R.id.ViewEmployeesButton);
        viewEmployeeButton.setOnClickListener(view -> {
            redirectToViewEmployeesPage();
        });
    }

    @Override
    public void onBackPressed() {
        // maybe redirect to home page or do nothing. currently does nothing
    }

    public void redirectToViewEmployeesPage() {
        Intent intent = new Intent(this, ViewEmployeesActivity.class);
        startActivity(intent);
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
            Intent intent = new Intent(MainActivity.this, WeekViewActivity.class);
            startActivity(intent);
        }
    }
}