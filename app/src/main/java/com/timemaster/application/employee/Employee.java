package com.timemaster.application.employee;

public class Employee
{
    private String firstName, lastName, nickName, qualification, email, phoneNum, isActive;
    private final int employeeID;

    public Employee(String firstName, String lastName, String qualification, String email, String phoneNum) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.qualification = qualification;
        this.email = email;
        this.phoneNum = phoneNum;
        this.employeeID = EmployeeManager.ID++;
        this.isActive = "TRUE";
        this.nickName = "";
    }

    public String getDisplayName() {
        String displayName = getFirstName() + " " + getLastName();
        if (!getNickName().isEmpty()) {
            displayName += " (" + getNickName() + ")";
        }
        return displayName;
    }

    public String getDisplayNameTitle() {
        return getDisplayName() + " - " + getQualification();
    }

    @Override
    public String toString() {
        return getDisplayName() + " - " + getQualification();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String isActive() {
        return isActive;
    }

    public void setActive(String active) {
        isActive = active;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getQualification() {
        return qualification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}