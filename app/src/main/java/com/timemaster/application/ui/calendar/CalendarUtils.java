package com.timemaster.application.ui.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarUtils
{
    public static LocalDate selectedDate;

    /*
    * returns: morn_ or aftr_ + day
      or saturday/sunday
    * */
    public static String getScheduleTime()
    {
        // getting the day of the week in short form. ie: mon, tue, etc.
        String abbreviatedDayOfWeek = CalendarUtils.getCurrentDayAbbr();
        String fullDayOfWeek = CalendarUtils.getCurrentDayFull();
        // get proper string formats. is it morning, afternoon or the weekend?
        String mornOrAfter = (WeekViewActivity.isAfternoon ? "aftr_" : "morn_") + abbreviatedDayOfWeek;
        return WeekViewActivity.isWeekend ? fullDayOfWeek : mornOrAfter;
    }

    public static String getDayName(LocalDate day)
    {
        // returns the day's full name in lowercase. ex. saturday, sunday
        return getFormattedDayOfWeek(day).getDisplayName(TextStyle.FULL, Locale.US).toLowerCase();
    }

    public static String getDayNameShort(LocalDate day) {
        return getFormattedDayOfWeek(day).getDisplayName(TextStyle.SHORT, Locale.US).toLowerCase();
    }

    public static String getCurrentDayAbbr() {
        // returns the day's short name in lowercase. ex. mon, tue
        return getFormattedDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).toLowerCase();
    }

    public static String getCurrentDayFull() {
        // returns the day's full name in lowercase. ex. saturday, sunday
        return getFormattedDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US).toLowerCase();
    }

    public static boolean isWeekend(LocalDate date) {
        return getFormattedDayOfWeek(date).getDisplayName(TextStyle.FULL, Locale.US).equalsIgnoreCase("saturday")
            || getFormattedDayOfWeek(date).getDisplayName(TextStyle.FULL, Locale.US).equalsIgnoreCase("sunday");
    }

    public static DayOfWeek getFormattedDayOfWeek() {
        // gets the name of the selected date.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = CalendarUtils.selectedDate.format(formatter);
        LocalDate date = LocalDate.parse(formattedDate, formatter);
        return date.getDayOfWeek();
    }

    public static DayOfWeek getFormattedDayOfWeek(LocalDate day) {
        // gets the name of the selected date.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = day.format(formatter);
        LocalDate date = LocalDate.parse(formattedDate, formatter);
        return date.getDayOfWeek();
    }

    public static String formatYMD(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return date.format(formatter);
    }

    public static LocalDate revertFormatYMD(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public static String formattedDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static ArrayList<LocalDate> daysInMonthArray()
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek)
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(),prevMonth.getMonth(),prevDaysInMonth + i - dayOfWeek));
            else if(i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(),nextMonth.getMonth(),i - dayOfWeek - daysInMonth));
            else
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i - dayOfWeek));
        }
        return  daysInMonthArray;
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate)
    {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate))
        {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    private static LocalDate sundayForDate(LocalDate current)
    {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo))
        {
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }
}
