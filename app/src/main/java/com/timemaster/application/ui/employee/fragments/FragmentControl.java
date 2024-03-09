package com.timemaster.application.ui.employee.fragments;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/*
* This class controls which tabs/fragments will be viewed within the main employee activity
* (activity_employee.xml)
* */
public class FragmentControl extends FragmentStateAdapter
{
    private static boolean isEditing = false;
    @SuppressLint("StaticFieldLeak")
    public static EmployeeCreationFragment employeeCreation;

    public FragmentControl(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return employeeCreation;
        }
        return new EmployeeAvailabilityFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public static boolean isEditing() {
        return isEditing;
    }

    public static void setEditing(boolean editing) {
        isEditing = editing;
    }
}
