package com.timemaster.application.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.timemaster.application.MainActivity;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.ui.calendar.CalendarUtils;
import com.timemaster.application.ui.calendar.schedule.AfternoonShiftRecycler;
import com.timemaster.application.ui.calendar.schedule.AssignShiftsActivity;
import com.timemaster.application.ui.calendar.schedule.MorningShiftRecycler;
import com.timemaster.application.ui.calendar.schedule.WeekendShiftRecycler;

import java.time.LocalDate;

/*
 * Example:
 *   EMP_ID      DATE           TIME
 *     1      2023/10/23      morn_mon
 *     2      2023/10/23      aftr_mon
 * */
// EACH AVAILABLE STUDENT SHOULD HAVE AT LEAST ONE SHIFT/WEEK
public class SchedulingTable
{
    // Database Info
    public final String SCHEDULE_TABLE_NAME = "schedule_data"; // name scheduling data table

    // Data Columns - Scheduling Info
    public final String EMPLOYEE_SCHEDULE_TIME = "schedule_time";
    public final String DATE = "date";
    /*
     * will be the primary key for each table and also a foreign key that relates the tables
     * employee_data and schedule_data together for each employee on each day
     * */
    public final String EMPLOYEE_ID = "id";

    public void storeDateData(Employee employee) {
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = CalendarUtils.formatYMD(CalendarUtils.selectedDate);
        String time = CalendarUtils.getScheduleTime();

        values.put(DATE, date);
        values.put(EMPLOYEE_ID, employee.getEmployeeID());
        values.put(EMPLOYEE_SCHEDULE_TIME, time);

        if (AssignShiftsActivity.availableEmployees != null && AssignShiftsActivity.availableEmployees.contains(employee))
            AssignShiftsActivity.availableEmployees.remove(employee);

        writeData.insert(SCHEDULE_TABLE_NAME, null, values);
        writeData.close();
    }

    public void readDates(String date, String time) {
        SQLiteDatabase readData = MainActivity.database.getReadableDatabase();
        Cursor cursor = readData.rawQuery("SELECT * FROM " + SCHEDULE_TABLE_NAME, null);

        while (cursor.moveToNext()) {
            // get the day first
            if (cursor.getString(cursor.getColumnIndexOrThrow(DATE)).equals(date)) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(EMPLOYEE_SCHEDULE_TIME)).equals(time)) {
                    Employee employee = MainActivity.employeeManager.getEmployeeById(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id"))));
                    if (time.contains("morn_")) {
                        MorningShiftRecycler.morningShifts.add(employee);
                    } else if (time.contains("aftr_")) {
                        AfternoonShiftRecycler.afternoonShifts.add(employee);
                    } else {
                        WeekendShiftRecycler.weekendShifts.add(employee);
                    }
                }
            }
        }

        cursor.close();
        readData.close();
    }

    public int checkShiftedEmployees(String date) {
        int count = 0;
        SQLiteDatabase readData = MainActivity.database.getReadableDatabase();
        Cursor cursor = readData.rawQuery("SELECT * FROM " + SCHEDULE_TABLE_NAME, null);

        while (cursor.moveToNext()) {
            // get the day first
            if (cursor.getString(cursor.getColumnIndexOrThrow(DATE)).equals(date)) {
                // get # of times the day shows up.
                // weekdays: 4 times = fully assigned
                // weekends: 3 times = fully assigned
                count++;
            }
        }

        cursor.close();
        readData.close();
        return count;
    }

    public void deleteFromShift(Employee employee, String time) {
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();

        String date = CalendarUtils.formatYMD(CalendarUtils.selectedDate);
        String scheduleTime = CalendarUtils.isWeekend(CalendarUtils.selectedDate) ?
                CalendarUtils.getCurrentDayFull() : time + CalendarUtils.getCurrentDayAbbr();
        // for adding back to dropdown list when user has AssignShiftsActivity focused
        if (AssignShiftsActivity.availableEmployees != null && !AssignShiftsActivity.availableEmployees.contains(employee))
            AssignShiftsActivity.availableEmployees.add(employee);

        writeData.delete(SCHEDULE_TABLE_NAME, "id=? and date=? and schedule_time=?",
                new String[]{ String.valueOf(employee.getEmployeeID()), date, scheduleTime });
        writeData.close();
    }

    public void deleteUnavailableEmployee(Employee employee, String scheduleTime, SQLiteDatabase writeData) {
        Cursor cursor = writeData.rawQuery("SELECT * FROM " + SCHEDULE_TABLE_NAME, null);
        LocalDate today = LocalDate.now();

        while (cursor.moveToNext()) {
            String dateValue = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
            LocalDate date = CalendarUtils.revertFormatYMD(dateValue);

            if (date.isAfter(today)) {
                writeData.delete(SCHEDULE_TABLE_NAME, "id=? and date=? and schedule_time=?",
                        new String[]{ String.valueOf(employee.getEmployeeID()), dateValue, scheduleTime });
            }
        }

        cursor.close();
    }
}
