package com.timemaster.application.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.timemaster.application.MainActivity;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.employee.EmployeeManager;
import com.timemaster.application.ui.employee.fragments.EmployeeAvailabilityFragment;
import com.timemaster.application.ui.employee.fragments.FragmentControl;

import java.util.ArrayList;
import java.util.List;

/*
* Example:
*     ID     MORN_MON     MORN_TUE     MORN_WED... AFTR_MON     AFTER_TUE....
*     1      true or 1   false or 0....
*     2      true or 1   false or 0....
* This table is used to determine who can be scheduled so that correct data (date, time, employee)
* is entered in the SchedulingTable.
* */
public class AvailabilityTable
{
    // Database Info
    public final String AVAILABILITY_TABLE_NAME = "availability_data";

    // Data Columns - Days of the week - Morning and Afternoon
    public final String MORNING_MON = "morn_mon";
    public final String MORNING_TUE = "morn_tue";
    public final String MORNING_WED = "morn_wed";
    public final String MORNING_THU = "morn_thu";
    public final String MORNING_FRI = "morn_fri";
    public final String AFTER_MON = "aftr_mon";
    public final String AFTER_TUE = "aftr_tue";
    public final String AFTER_WED = "aftr_wed";
    public final String AFTER_THU = "aftr_thu";
    public final String AFTER_FRI = "aftr_fri";
    public final String SATURDAY = "saturday";
    public final String SUNDAY = "sunday";
    /*
     * will be the primary key for each table and also a foreign key that relates the tables
     * employee_data and schedule_data together as well as this availability table
     * */
    public final String EMPLOYEE_ID = "id";

    public void storeAvailability(Employee employee) {
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String available, time;
        // to reset index for getting days[] from monday through fri for both morning and afternoon
        int pos = 0;
        for (int i = 0; i < 12; i++) {
            if (i < 5) {
                time = "morn_";
            } else if (i < 10) {
                pos = 5;
                time = "aftr_";
            } else {
                // i = 11, 12
                time = "";
            }
            // get available days from the employee activity
            if (EmployeeAvailabilityFragment.available_days.contains(time + EmployeeManager.days[i - pos]))
                 available = "true";
            else available = "false";

            values.put(time + EmployeeManager.days[i - pos], available);
        }
        values.put(EMPLOYEE_ID, employee.getEmployeeID());
        writeData.insert(AVAILABILITY_TABLE_NAME, null, values);
        writeData.close();
        EmployeeAvailabilityFragment.available_days.clear();
    }

    public boolean readAvailability(int employeeID) {
        boolean toggle = false;
        SQLiteDatabase readData = MainActivity.database.getReadableDatabase();
        Cursor cursor = readData.rawQuery("SELECT * FROM " + AVAILABILITY_TABLE_NAME, null);

        while (cursor.moveToNext()) {
            // find the correct employee by id
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            if (id == employeeID)
                break;
        }

        // get the specific row, col for this employee
        if (cursor.getString(EmployeeAvailabilityFragment.buttonIndex-1).equals("true")) {
            // set button to toggled, add it to list
            // button condition if (enabled) should prevent duplicate days or
            // removal of non-existing days
            toggle = true;
            if (FragmentControl.isEditing()) {
                String colName = cursor.getColumnName(EmployeeAvailabilityFragment.buttonIndex-1);
                EmployeeAvailabilityFragment.available_days.add(colName);
            }
        }

        cursor.close();
        readData.close();
        return toggle;
    }

    public void updateAvailability(Employee e) {
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();
        ContentValues values = new ContentValues();

        Cursor cursor = writeData.rawQuery("SELECT * FROM " + AVAILABILITY_TABLE_NAME + " WHERE " + EMPLOYEE_ID + "=" + e.getEmployeeID(), null);
        cursor.moveToFirst();

        String available, time;
        // to reset index for getting days[] from monday through fri for both morning and afternoon
        int pos = 0;
        for (int i = 0; i < 12; i++) {
            if (i < 5) {
                time = "morn_";
            } else if (i < 10) {
                pos = 5;
                time = "aftr_";
            } else {
                // i = 11, 12
                time = "";
            }

            String columnName = time + EmployeeManager.days[i - pos];
            int columnIndex = cursor.getColumnIndex(columnName);

            // get available days from the employee activity
            if (EmployeeAvailabilityFragment.available_days.contains(columnName))
                available = "true";
            else {
                // should do removal here.
                String columnValue = cursor.getString(columnIndex);
                if (columnValue.equals("true")) {
                    MainActivity.database.schedulingTable.deleteUnavailableEmployee(e, columnName, writeData);
                }
                available = "false";
            }

            values.put(time + EmployeeManager.days[i - pos], available);
        }

        writeData.update(AVAILABILITY_TABLE_NAME, values, "id=?", new String[]{ String.valueOf(e.getEmployeeID()) });
        cursor.close();
        writeData.close();
        EmployeeAvailabilityFragment.available_days.clear();
    }

    public List<Employee> getAvailableEmployees(String day) {
        List<Employee> employeeListFiltered = new ArrayList<>();
        SQLiteDatabase readData = MainActivity.database.getReadableDatabase();
        Cursor cursor = readData.rawQuery("SELECT * FROM " + AVAILABILITY_TABLE_NAME, null);

        // find the correct index for day param
        int i = 0;
        for (; i < 12; i++) {
            if (day.equals(cursor.getColumnName(i))) {
                break;
            }
        }

        // add employees who are working that day to filteredList
        for (Employee employee : MainActivity.employeeManager.getEmployeeList()) {
            cursor.moveToNext();
            if (cursor.getString(i).equals("true") && employee.isActive().equals("TRUE"))
                employeeListFiltered.add(employee);
        }

        cursor.close();
        readData.close();
        return employeeListFiltered;
    }
}
