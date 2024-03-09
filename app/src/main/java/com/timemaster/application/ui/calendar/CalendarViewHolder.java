package com.timemaster.application.ui.calendar;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timemaster.application.MainActivity;
import com.timemaster.application.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth;
    public final ImageView check, wrong;
    private final CalendarAdapter.OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days)
    {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);

        dayOfMonth = parentView.findViewById(R.id.cellDayText);
        check = parentView.findViewById(R.id.ProperlyAssigned);
        wrong = parentView.findViewById(R.id.WronglyAssigned);

        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;
    }

    public void setViewState(LocalDate date) {
        int employeeCount = MainActivity.database.schedulingTable.checkShiftedEmployees(CalendarUtils.formatYMD(date));
        String dayName = CalendarUtils.getDayName(date);

        if (employeeCount == 0) setUnassigned();
        else if (((dayName.equals("saturday") || dayName.equals("sunday")) && employeeCount == 3) || employeeCount == 4)
            setProperlyAssigned();
        else setWronglyAssigned();
    }

    private void setWronglyAssigned() {
        wrong.setVisibility(View.VISIBLE);
        check.setVisibility(View.GONE);
        parentView.setBackgroundColor(Color.parseColor("#FF8989"));
    }

    private void setProperlyAssigned() {
        wrong.setVisibility(View.GONE);
        check.setVisibility(View.VISIBLE);
        parentView.setBackgroundColor(Color.parseColor("#D4FBCE"));
    }

    private void setUnassigned() {
        wrong.setVisibility(View.GONE);
        check.setVisibility(View.GONE);
        parentView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}
