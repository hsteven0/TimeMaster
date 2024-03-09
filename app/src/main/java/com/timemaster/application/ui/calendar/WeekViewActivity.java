package com.timemaster.application.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.ui.calendar.schedule.AfternoonShiftRecycler;
import com.timemaster.application.ui.calendar.schedule.AssignShiftsActivity;
import com.timemaster.application.ui.calendar.schedule.MorningShiftRecycler;
import com.timemaster.application.ui.calendar.schedule.WeekendShiftRecycler;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.timemaster.application.ui.calendar.CalendarUtils.daysInWeekArray;
import static com.timemaster.application.ui.calendar.CalendarUtils.monthYearFromDate;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText, morningShift, afternoonShift, weekendShifts;
    private RecyclerView calendarRecyclerView, recyclerViewMorn, recyclerViewAfter, recyclerViewWeekend;
    private MorningShiftRecycler morningShiftRecycler;
    private AfternoonShiftRecycler afternoonShiftRecycler;
    private WeekendShiftRecycler weekendShiftRecycler;
    public static boolean isAfternoon = false;
    public static boolean isWeekend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        // initialize views
        calendarRecyclerView = findViewById(R.id.calendarRecyclerViewWV);
        monthYearText = findViewById(R.id.monthYearTVWV);
        morningShift = findViewById(R.id.MorningShiftsTV);
        afternoonShift = findViewById(R.id.AfternoonShiftsTV);
        weekendShifts = findViewById(R.id.WeekendShiftsTV);

        setWeekView();

        // set up click listeners for clicking on text
        morningShift.setOnClickListener(view -> {
            isAfternoon = false;
            isWeekend = false;
            startActivity(new Intent(WeekViewActivity.this, AssignShiftsActivity.class));
        });
        afternoonShift.setOnClickListener(view -> {
            isAfternoon = true;
            isWeekend = false;
            startActivity(new Intent(WeekViewActivity.this, AssignShiftsActivity.class));
        });
        weekendShifts.setOnClickListener(view -> {
            isAfternoon = false;
            isWeekend = true;
            startActivity(new Intent(WeekViewActivity.this, AssignShiftsActivity.class));
        });

        // set up recycler views
        morningShiftRecycler = new MorningShiftRecycler(this);
        recyclerViewMorn = findViewById(R.id.MorningShiftsRView);
        recyclerViewMorn.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewMorn.setAdapter(morningShiftRecycler);

        afternoonShiftRecycler = new AfternoonShiftRecycler(this);
        recyclerViewAfter = findViewById(R.id.AfternoonShiftsRView);
        recyclerViewAfter.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewAfter.setAdapter(afternoonShiftRecycler);

        weekendShiftRecycler = new WeekendShiftRecycler(this);
        recyclerViewWeekend = findViewById(R.id.WeekendShiftsRView);
        recyclerViewWeekend.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewWeekend.setAdapter(weekendShiftRecycler);

        setRecyclerAnimations();

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id==android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        LocalDate oldDate = CalendarUtils.selectedDate;
        CalendarUtils.selectedDate = date;
        setWeekView();

        // refresh data when different date is clicked
        if (!oldDate.toString().equals(CalendarUtils.selectedDate.toString()))
            displaySchedulingData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        displaySchedulingData();
    }

    private void displaySchedulingData() {
        refreshRecyclerAnimations();
        // this is where to clear the lists, and read from database to add proper
        // values to the recycler lists for displaying the right employees on the days.
        MorningShiftRecycler.morningShifts.clear();
        AfternoonShiftRecycler.afternoonShifts.clear();
        WeekendShiftRecycler.weekendShifts.clear();
        morningShiftRecycler.notifyDataSetChanged();
        afternoonShiftRecycler.notifyDataSetChanged();
        weekendShiftRecycler.notifyDataSetChanged();
        // read employee database to get the employees
        MainActivity.database.employeeTable.readDatabase();
        String abbreviatedDayOfWeek = CalendarUtils.getCurrentDayAbbr();
        String fullDayOfWeek = CalendarUtils.getCurrentDayFull();
        adjustCalendarWeight(fullDayOfWeek);

        // get weekend scheduling data
        if (fullDayOfWeek.equals("saturday") || fullDayOfWeek.equals("sunday")) {
            hideWeekdayViews();
            MainActivity.database.schedulingTable.readDates(
                    CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                    fullDayOfWeek);
        } else {
            // get weekday morning and afternoon scheduling data
            hideWeekendViews();
            MainActivity.database.schedulingTable.readDates(
                    CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                    "morn_" + abbreviatedDayOfWeek);
            MainActivity.database.schedulingTable.readDates(
                    CalendarUtils.formatYMD(CalendarUtils.selectedDate),
                    "aftr_" + abbreviatedDayOfWeek);
        }
    }

    private void setRecyclerAnimations() {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
        recyclerViewMorn.setLayoutAnimation(controller);
        recyclerViewAfter.setLayoutAnimation(controller);
        recyclerViewWeekend.setLayoutAnimation(controller);
    }

    private void refreshRecyclerAnimations() {
        recyclerViewMorn.scheduleLayoutAnimation();
        recyclerViewAfter.scheduleLayoutAnimation();
        recyclerViewWeekend.scheduleLayoutAnimation();
    }

    private void adjustCalendarWeight(String day) {
        float weightValue = 0.3F;
        if (day.equals("saturday") || day.equals("sunday")) {
            weightValue = 0.145F;
        }
        // creating layout parameters for the calendarRecyclerView
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                0,                                      // height (0 indicates it's controlled by weight)
                weightValue                             // weight (set your desired weight here)
        );
        calendarRecyclerView.setLayoutParams(layoutParams);
    }

    private void hideWeekendViews() {
        morningShift.setVisibility(View.VISIBLE);
        recyclerViewMorn.setVisibility(View.VISIBLE);
        afternoonShift.setVisibility(View.VISIBLE);
        recyclerViewAfter.setVisibility(View.VISIBLE);
        weekendShifts.setVisibility(View.GONE);
        recyclerViewWeekend.setVisibility(View.GONE);
    }

    private void hideWeekdayViews() {
        morningShift.setVisibility(View.GONE);
        recyclerViewMorn.setVisibility(View.GONE);
        afternoonShift.setVisibility(View.GONE);
        recyclerViewAfter.setVisibility(View.GONE);
        weekendShifts.setVisibility(View.VISIBLE);
        recyclerViewWeekend.setVisibility(View.VISIBLE);
    }

    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
        displaySchedulingData();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
        displaySchedulingData();
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
}