package com.timemaster.application.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper
{
    // Database Info
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "employee_database.sqlite"; // name of the database itself

    public EmployeeTable employeeTable = new EmployeeTable();
    public SchedulingTable schedulingTable = new SchedulingTable();
    public AvailabilityTable availabilityTable = new AvailabilityTable();

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // setting up the employee database table as String query
        String employee = "CREATE TABLE " + employeeTable.EMPLOYEE_TABLE_NAME + " ("
                + employeeTable.EMPLOYEE_FIRST_NAME + " TEXT,"
                + employeeTable.EMPLOYEE_LAST_NAME + " TEXT,"
                + employeeTable.EMPLOYEE_NICK_NAME + " TEXT,"
                + employeeTable.EMPLOYEE_QUALIFICATION + " TEXT,"
                + employeeTable.EMPLOYEE_EMAIL + " TEXT,"
                + employeeTable.EMPLOYEE_PHONE_NUMBER + " TEXT,"
                + employeeTable.EMPLOYEE_ACTIVE + " TEXT,"
                + employeeTable.EMPLOYEE_ID + " INTEGER"
//                + "FOREIGN KEY (" + employeeTable.EMPLOYEE_ID + ") REFERENCES " + schedulingTable.SCHEDULE_TABLE_NAME + "(" + schedulingTable.EMPLOYEE_ID + ")"
                + ")";

        // setting up the schedule database table as String query
        String schedule = "CREATE TABLE " + schedulingTable.SCHEDULE_TABLE_NAME + " ("
                + schedulingTable.EMPLOYEE_ID + " INTEGER,"
                + schedulingTable.DATE + " TEXT NOT NULL,"
                + schedulingTable.EMPLOYEE_SCHEDULE_TIME + " TEXT"
//                + "FOREIGN KEY (" + schedulingTable.EMPLOYEE_ID + ") REFERENCES " + employeeTable.EMPLOYEE_TABLE_NAME + "(" + employeeTable.EMPLOYEE_ID + ")"
                + ")";

        // setting up the schedule database table as String query
        String availability = "CREATE TABLE " + availabilityTable.AVAILABILITY_TABLE_NAME + " ("
                + availabilityTable.MORNING_MON + " TEXT,"
                + availabilityTable.MORNING_TUE + " TEXT,"
                + availabilityTable.MORNING_WED + " TEXT,"
                + availabilityTable.MORNING_THU + " TEXT,"
                + availabilityTable.MORNING_FRI + " TEXT,"
                + availabilityTable.AFTER_MON + " TEXT,"
                + availabilityTable.AFTER_TUE + " TEXT,"
                + availabilityTable.AFTER_WED + " TEXT,"
                + availabilityTable.AFTER_THU + " TEXT,"
                + availabilityTable.AFTER_FRI + " TEXT,"
                + availabilityTable.SATURDAY + " TEXT,"
                + availabilityTable.SUNDAY + " TEXT,"
                + availabilityTable.EMPLOYEE_ID + " INTEGER"
                + ")";

        // execute to create the database tables
        sqLiteDatabase.execSQL(employee);
        sqLiteDatabase.execSQL(schedule);
        sqLiteDatabase.execSQL(availability);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVers, int newVers) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + employeeTable.EMPLOYEE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + schedulingTable.SCHEDULE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + availabilityTable.AVAILABILITY_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
