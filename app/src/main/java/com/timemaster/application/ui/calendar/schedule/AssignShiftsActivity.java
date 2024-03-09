package com.timemaster.application.ui.calendar.schedule;

import static com.timemaster.application.ui.calendar.WeekViewActivity.isAfternoon;
import static com.timemaster.application.ui.calendar.WeekViewActivity.isWeekend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.ui.calendar.CalendarUtils;
import com.timemaster.application.ui.calendar.WeekViewActivity;

import java.util.List;

public class AssignShiftsActivity extends AppCompatActivity
{
    private TextView date, shiftPreview;
    private Button assignButton;
    private AutoCompleteTextView dropdown;
    private RecyclerView assignShiftsRecycler;
    private RecyclerView.Adapter<?> recycleAdapterType;
    public static Employee employee;
    public static List<Employee> availableEmployees, shiftList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_shifts);
        // get access to views
        dropdown = findViewById(R.id.AssignShiftsDropdown);
        assignShiftsRecycler = findViewById(R.id.ShiftPreviewRecycler);
        date = findViewById(R.id.AssignShiftsDateTV);
        shiftPreview = findViewById(R.id.ShiftPreviewText);
        assignButton = findViewById(R.id.AssignShiftsButtonTV);

        // set up recycler view
        setShiftType();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        assignShiftsRecycler.setLayoutManager(linearLayoutManager);
        assignShiftsRecycler.setAdapter(recycleAdapterType);

        // set up date TextView
        date.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));

        MainActivity.database.employeeTable.readDatabase();

        availableEmployees = MainActivity.database.availabilityTable.getAvailableEmployees(CalendarUtils.getScheduleTime());
        // set up the shift list
        if (isAfternoon) {
            shiftList = AfternoonShiftRecycler.afternoonShifts;
        } else if (isWeekend) {
            shiftList = WeekendShiftRecycler.weekendShifts;
        } else {
            shiftList = MorningShiftRecycler.morningShifts;
        }

        updateDropdownList();

        // get selected item from user
        dropdown.setOnItemClickListener((adapterView, view, position, l) -> {
            if (availableEmployees.isEmpty()) return;
            Employee selectedEmployee = (Employee) adapterView.getItemAtPosition(position);

            // remove selected from list, add unselected
            availableEmployees.remove(selectedEmployee);
            availableEmployees.add(AssignShiftsActivity.employee);
            ArrayAdapter<Employee> adapter = new ArrayAdapter<>(AssignShiftsActivity.this, R.layout.dropdown_item, availableEmployees);
            dropdown.setAdapter(adapter);

            AssignShiftsActivity.employee = MainActivity.employeeManager.getEmployeeById(selectedEmployee.getEmployeeID());
        });

        assignButton.setOnClickListener(view -> {
            Employee e = AssignShiftsActivity.employee;
            if (availableEmployees.isEmpty() && e == null) {
                Toast.makeText(AssignShiftsActivity.this,
                        "You have no employees for this day to assign!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if ((isWeekend && shiftList.size() == 3) || (!isWeekend && shiftList.size() == 2)) {
                Toast.makeText(AssignShiftsActivity.this,
                        String.format("Too many employees for this shift! Maximum: %s", isWeekend ? "3" : "2"),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (((isWeekend && shiftList.size() == 2) || (!isWeekend && shiftList.size() == 1)) && checkShiftList(e)) {
                String errorMessage;
                if (!isWeekend) {
                    if (isAfternoon) {
                        errorMessage = "You need at least one employee qualified for closing!";
                    } else {
                        errorMessage = "You need at least one employee qualified for opening!";
                    }
                } else {
                    errorMessage = "Need an employee for opening, one for closing, or one with both skills!";
                }

                Toast.makeText(AssignShiftsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                return;
            }

            shiftList.add(e);
            MainActivity.database.schedulingTable.storeDateData(e);
            updateSchedulingData();
            updateDropdownList();
            if (this.getCurrentFocus() != null) this.getCurrentFocus().clearFocus();
        });
    }

    public boolean checkShiftList(Employee employee) {
        int openingAmt = 0;
        int closingAmt = 0;
        int doingBothAmt = 0;

        shiftList.add(employee);
        for (Employee emp : shiftList) {
            switch (emp.getQualification()) {
                case "Closing":
                    closingAmt++;
                    break;
                case "Opening":
                    openingAmt++;
                    break;
                case "Opening and Closing":
                    doingBothAmt++;
                    break;
            }
        }
        shiftList.remove(employee);

        if (isWeekend) {
            return !(doingBothAmt >= 1 || (closingAmt >= 1 && openingAmt >= 1));
        } else if (isAfternoon) {
            return !(closingAmt >= 1 || doingBothAmt >= 1);
        }
        return !(openingAmt >= 1 || doingBothAmt >= 1);
    }

    private void updateSchedulingData() {
        // this is where to clear the lists, and read from database to add proper
        // values to the recycler lists for displaying the right employees on the days.
        MorningShiftRecycler.morningShifts.clear();
        AfternoonShiftRecycler.afternoonShifts.clear();
        WeekendShiftRecycler.weekendShifts.clear();
        recycleAdapterType.notifyDataSetChanged();
        // read employee database to get the employees
        MainActivity.database.employeeTable.readDatabase();
        String abbreviatedDayOfWeek = CalendarUtils.getCurrentDayAbbr();
        String fullDayOfWeek = CalendarUtils.getCurrentDayFull();

        // get weekend scheduling data
        if (fullDayOfWeek.equals("saturday") || fullDayOfWeek.equals("sunday")) {
            MainActivity.database.schedulingTable.readDates(
                    CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                    fullDayOfWeek);
        }
        // get weekday morning or afternoon scheduling data
        else if (isAfternoon) {
            MainActivity.database.schedulingTable.readDates(
                    CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                    "aftr_" + abbreviatedDayOfWeek);
        } else {
            MainActivity.database.schedulingTable.readDates(
                    CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                    "morn_" + abbreviatedDayOfWeek);
        }
    }

    private void updateDropdownList() {
        // remove assigned employees from the dropdown employee list
        for (Employee shiftEmployee : shiftList) {
            Employee employeeToRemove = MainActivity.employeeManager.getEmployeeById(shiftEmployee.getEmployeeID());
            if (employeeToRemove != null && availableEmployees.contains(employeeToRemove)) {
                availableEmployees.remove(employeeToRemove);
            }
        }

        // set layout of the dropdown box using ArrayAdapter
        if (availableEmployees.isEmpty()) {
            dropdown.setText("No Available Employees!");
            AssignShiftsActivity.employee = null;
        } else {
            ArrayAdapter<Employee> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, availableEmployees);
            dropdown.setText(availableEmployees.get(0).getDisplayNameTitle());
            AssignShiftsActivity.employee = availableEmployees.get(0);
            availableEmployees.remove(0);
            dropdown.setAdapter(adapter);
        }
    }

    public void goBack(View v) {
        startActivity(new Intent(AssignShiftsActivity.this, WeekViewActivity.class));
    }

    private void setShiftType() {
        if (isAfternoon) {
            recycleAdapterType = new AfternoonShiftRecycler(this);
            shiftPreview.setText(R.string.preview_afternoon_shift);
        } else if (isWeekend) {
            recycleAdapterType = new WeekendShiftRecycler(this);
            shiftPreview.setText(R.string.preview_weekend_shift);
        } else {
            recycleAdapterType = new MorningShiftRecycler(this);
            shiftPreview.setText(R.string.preview_morning_shift);
        }
    }
}
