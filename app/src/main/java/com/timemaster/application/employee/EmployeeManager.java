package com.timemaster.application.employee;

import com.timemaster.application.ui.employee.EmployeeActivity;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManager
{
    private List<Employee> employeeList;
    public static String[] qualifications = { "Closing", "Opening", "Opening and Closing", "Trainee" };
    public static String[] days = { "mon", "tue", "wed", "thu", "fri", "saturday", "sunday" };
    public static int ID = 0;

    public EmployeeManager() {
        employeeList = new ArrayList<Employee>();
    }

    public void addEmployee(Employee employee) {
        this.employeeList.add(employee);
    }

    public Employee getEmployeeByName(String employeeName) {
        for (Employee e : this.getEmployeeList()) {
            if (e.getFirstName().equals(employeeName)) {
                return e;
            }
        }
        return null;
    }

    public Employee getEmployeeByName(String firstName, String lastName) {
        for (Employee e : this.getEmployeeList()) {
            if (e.getFirstName().equals(firstName) && e.getLastName().equals(lastName)) {
                return e;
            }
        }
        return null;
    }

    public String employeeDupeInfo(String firstName, String lastName, String email, String phoneNum)
    {
        for (Employee e : this.getEmployeeList()) {
            EmployeeActivity.employee = e;
            if (e.getPhoneNum().equals(phoneNum)) {
                return "phone";
            }
            if (e.getEmail().equals(email)) {
                return "email";
            }
            if (e.getFirstName().equals(firstName) && e.getLastName().equals(lastName)) {
                return "name";
            }
        }
        // no dupe found!
        EmployeeActivity.employee = null;
        return null;
    }

    public Employee getEmployeeById(int id) {
        for (Employee e : this.getEmployeeList()) {
            if (e.getEmployeeID() == id) {
                return e;
            }
        }
        return null;
    }

    public Employee getEmployeeByNickName(String nickName) {
        for (Employee e : this.getEmployeeList()) {
            if (e.getNickName().equals(nickName)) {
                return e;
            }
        }
        return null;
    }

    public boolean checkEmail(String email) {
        /*
        * An email address is valid if:
        *  1. given email has @ sign
        *  2. at least 1 char for the email name before @
        *  3. the position of '.' is after the position of '@'
        *  4. at least 1 char after the last '.', representing the domain of the email
        * */
        // get the index positions of characters '@' and last '.'
        int iAt = email.indexOf('@');
        int iDot = email.lastIndexOf('.');
        // RegEx to allow valid characters - Excludes !%*$&#, etc.
        String validCharsPattern = "^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        // returns: True if valid, else it is not valid
        return iAt >= 1 && iDot > iAt && (email.length() - iDot) > 1 && email.matches(validCharsPattern);
    }

    public boolean checkPhoneNumber(String phoneNumber) {
        // TODO: Check and remove spaces from total string length?
        return phoneNumber.length() == 10;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }
}