<div align="center">
  <a href="https://github.com/hsteven0/TimeMaster">
    <img src="images/timemaster_icon.png" alt="Logo" width="180" height="180">
  </a>

  <h1 align="center">Time Master</h1>

  <p align="center">
    An Employee Scheduling Application for Android Phones!
  </p>
</div>

## About the Project
<div style="width:100%; overflow:auto;">
  <p>This application is crafted to assist individuals in creating schedules based on their roster of employees.</p>

  <img align="left" height="490" width="260" src="/images/calendar_main.png" alt="App Main Page">
  <div style="margin-left:270px;">
  
  #### Main Page
  Upon launching the application, users are greeted with the Main Page interface. From this point, users have the option to navigate to the employee catalog or access the calendar to view dates.
  
  #### Employee Catalog
  The [employee catalog](#employee-roster-and-creation-pages) is a dedicated page within the application that showcases a list of employees. This page features a convenient floating button, enabling users to instantly add new employees to their roster. Clicking on any of the listed employees triggers the employee editing features, granting users easy access to modify and update employee information as needed.

   #### Calendar
   The [calendar](#the-calendar) offers a clear and organized monthly display, allowing users to easily visualize their schedules. Months can be traversed back and fourth by clicking the arrow buttons. Clicking on a day converts the calendar display to a weekly view; each day in the week view displays scheduling information about the selected day.
  </div>
</div>

## Getting Started
```
1. This project was developed using Android Studio and the Java JDK. Both of these are required.
2. Fork and clone the forked repository to your local machine.
3. Open this project through Android Studio.
4. After the gradle build has synced, you are free to run the project or make any changes.
```
<br>

## Employee Roster and Creation Pages
<div style="width:100%; overflow:auto;">
  <div style="float:left;">
    <img height="490" width="260" src="/images/employee_list.png" alt="Employee Catalog Page">
    <img height="490" width="260" src="/images/employee_create_general_tab.png" alt="Employee Creation Page - General">
    <img height="490" width="260" src="/images/employee_create_availability_tab.png" alt="Employee Creation Page - Availability">
  </div>

  <div style="clear:left;">
  <p align="left">
  
  Creating and updating employees within your business has been set up in an easy-to-follow process. The employee list page features a convenient floating button, allowing users to instantly add new employees to their roster. Upon entering the employee creation page, all required fields are indicated and checked for upon adding. Clicking on any of the listed employees triggers the [employee editing](#employee-management) features, granting users easy access to modify and update employee information as needed.
  </p>
  </div>
</div>

## Employee Management
<div style="width:100%; overflow:auto;">
  <div style="float:left;">
    <img height="490" width="260" src="/images/employee_editing_old_list.png" alt="Employee Catalog List - Edit Showcase">
    <img height="490" width="260" src="/images/employee_editing_new_info.png" alt="Employee Editing - Updating with new info">
    <img height="490" width="260" src="/images/employee_editing_updated.png" alt="Employee Catalog List - Updated Employee">
  </div>

  <div style="clear:left;">
  <p align="left">The employee editing feature redirects users to a page that resembles the employee creation interface; the input fields, along with the availability options, are pre-populated with the selected employee's data. This allows users to effortlessly modify specific details without the need to re-enter existing information.</p>
  </div>
</div>

## The Calendar
<div style="width:100%; overflow:auto;">
  <div style="float:left;">
    <img height="490" width="260" src="/images/calendar_main.png" alt="Calendar on Main Page">
    <img height="490" width="260" src="/images/calendar_shift_view.png" alt="Calendar Week, Day and Shift Views">
    <img height="490" width="260" src="/images/calendar_main_overview.png" alt="Calendar Overview">
  </div>

  <div style="clear:left;">
  <p align="left">

  The calendar featured on the main page offers users a comprehensive monthly overview, where any month can be accessible through intuitive arrow buttons situated at the top. A distinctive grey cursor/marker on the calendar indicates the current or selected day. Clicking on any day within the calendar transitions users to the week view, providing detailed information specific to the selected day for that week. In this week view, users have the ability to initiate the [scheduling process](#scheduling) by assigning employees to morning or afternoon shifts on the selected day.
  </p>

  Days that are fully scheduled (two employees in both the morning and afternoon shifts, or three on full day weekend shifts) will be visually indicated by a green background accompanied by a checkmark icon, providing users with immediate confirmation. Conversely, days that are not fully scheduled will feature a red background adorned with an "x" symbol, alerting users of incomplete scheduling. Furthermore, days with no assigned employees will be distinguished by the default white background, ensuring clarity and easy differentiation within the calendar interface.
  </div>
</div>

## Scheduling
<div style="width:100%; overflow:auto;">
  <div style="float:left;">
    <img height="490" width="260" src="/images/calendar_shift_dropdown.png" alt="Calendar on Main Page">
    <img height="490" width="260" src="/images/calendar_shift_morning_view_assigned.png" alt="Calendar Week, Day and Shift Views">
    <img height="490" width="260" src="/images/calendar_shift_removal_option.png" alt="Calendar Overview">
  </div>

  <div style="clear:left;">
  <p align="left">

  Within the week view, users will encounter morning and afternoon shifts designated for weekdays, while full-day shifts are scheduled for weekends. To begin assigning employees for specific shifts on a given day and time, users can simply click on the corresponding shift text. This action redirects to the shift assignment page. A dropdown menu allows users to select the appropriate employee from their roster based off of availability. Upon selecting the desired employee, users can confirm their choice by pressing the "add" button. In the event of an incorrect selection, users can easily revert the mistake by clicking on the employee's name and clicking the garbage can icon to remove them from the shift assignment.
  </p>
  </div>
</div>