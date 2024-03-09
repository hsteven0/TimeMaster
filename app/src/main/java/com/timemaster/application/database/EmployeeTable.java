package com.timemaster.application.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.timemaster.application.MainActivity;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.employee.EmployeeManager;

public class EmployeeTable
{
    // Database Info
    public final String EMPLOYEE_TABLE_NAME = "employee_data"; // name employee data table

    // Data Columns - Employee Data
    public final String EMPLOYEE_FIRST_NAME = "first_name";
    public final String EMPLOYEE_LAST_NAME = "last_name";
    public final String EMPLOYEE_NICK_NAME = "nick_name";
    public final String EMPLOYEE_EMAIL = "email";
    public final String EMPLOYEE_PHONE_NUMBER = "phone_number";
    public final String EMPLOYEE_QUALIFICATION = "qualification";
    public final String EMPLOYEE_ACTIVE = "is_active";
    /*
    * will be the primary key for each table and also a foreign key that relates the tables
    * employee_data and schedule_data together for each employee on each day
    * */
    public final String EMPLOYEE_ID = "id";

    public void saveEmployee(Employee employee) {
        // get the Database to write
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();
        // this will store employee data
        ContentValues values = new ContentValues();

        // assigns employee data to its proper column to create a proper key, value pair
        values.put(EMPLOYEE_FIRST_NAME, employee.getFirstName());
        values.put(EMPLOYEE_LAST_NAME, employee.getLastName());
        values.put(EMPLOYEE_NICK_NAME, employee.getNickName());
        values.put(EMPLOYEE_EMAIL, employee.getEmail());
        values.put(EMPLOYEE_PHONE_NUMBER, employee.getPhoneNum());
        values.put(EMPLOYEE_QUALIFICATION, employee.getQualification());
        values.put(EMPLOYEE_ACTIVE, employee.isActive());
        values.put(EMPLOYEE_ID, employee.getEmployeeID());

        // add to the employee list
        MainActivity.employeeManager.addEmployee(employee);

        // put employee data into the table and close
        writeData.insert(EMPLOYEE_TABLE_NAME, null, values);
        writeData.close();
    }

    public void readDatabase() {
        /*
         * This function will read the database and add employees from the database into the
         * main employee list.
         * */
        // get the database for reading
        SQLiteDatabase readData = MainActivity.database.getReadableDatabase();
        // get data from database query and access it line by line using the cursor class
        Cursor cursor = readData.rawQuery("SELECT * FROM " + EMPLOYEE_TABLE_NAME, null);

        if (MainActivity.employeeManager != null)
            MainActivity.employeeManager.getEmployeeList().clear();
        EmployeeManager.ID = 0;

        // traverse the database and add employee objects with saved data
        // to the employee list manager
        while (cursor.moveToNext()) {
            // create and add employee object from database
            String nickName = cursor.getString(cursor.getColumnIndexOrThrow("nick_name"));
            Employee employee = new Employee(
                    cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("qualification")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
            employee.setActive(cursor.getString(cursor.getColumnIndexOrThrow("is_active")));
            employee.setNickName(nickName);
            MainActivity.employeeManager.addEmployee(employee);
        }
        cursor.close();
        readData.close();
    }

    public void editEmployee(Employee employee, String firstName, String lastName, String nickName, String qualification, String email, String phoneNumber) {
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();
        ContentValues values = new ContentValues();

        // replace the old values with new values in the database
        values.put(EMPLOYEE_FIRST_NAME, firstName);
        values.put(EMPLOYEE_LAST_NAME, lastName);
        values.put(EMPLOYEE_NICK_NAME, nickName);
        values.put(EMPLOYEE_QUALIFICATION, qualification);
        values.put(EMPLOYEE_EMAIL, email);
        values.put(EMPLOYEE_PHONE_NUMBER, phoneNumber);

        // get employee and use setter methods to update object in real time
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setNickName(nickName);
        employee.setQualification(qualification);
        employee.setEmail(email);
        employee.setPhoneNum(phoneNumber);

        // get the correct employee from database by id and pass in updated info
        writeData.update(EMPLOYEE_TABLE_NAME, values, "id=?", new String[]{ String.valueOf(employee.getEmployeeID()) });
        writeData.close();
    }

    public void archiveEmployee(Employee employee) {
        SQLiteDatabase writeData = MainActivity.database.getWritableDatabase();
        ContentValues values = new ContentValues();

        // update employee as inactive
        values.put(EMPLOYEE_ACTIVE, "FALSE");
        employee.setActive("FALSE");

        // get the correct employee from database by id and save active status changes
        writeData.update(EMPLOYEE_TABLE_NAME, values, "id=?", new String[]{ String.valueOf(employee.getEmployeeID()) });
        writeData.close();
    }
}
