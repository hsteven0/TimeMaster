package com.timemaster.application.ui.calendar.schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.employee.Employee;

import java.util.ArrayList;
import java.util.List;

public class WeekendShiftRecycler extends RecyclerView.Adapter<WeekendShiftRecycler.ViewHolder>
{
    private final Context context;
    public static List<Employee> weekendShifts = new ArrayList<>();
    private boolean isSwiped = false;

    public WeekendShiftRecycler(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public WeekendShiftRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item_shift, parent, false);
        return new WeekendShiftRecycler.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekendShiftRecycler.ViewHolder holder, int position) {
        Employee employee = weekendShifts.get(position);
        holder.fullName.setText(employee.getDisplayName());
        holder.qualification.setText(employee.getQualification());
        // click listener for when user clicks on a recyclerview item
        holder.itemView.setOnClickListener(view -> {
            // determines if the item should swipe out by "toggling" it on/off
            isSwiped = !isSwiped;
            // determines where the item is, or if it has been swiped out.
            isSwiped = holder.cardView.getTranslationX() == 0; // if false, it will slide back in

            /*
            Perform the swipe animation/translation depending on isSwiped, to show additional
            options for this recyclerview item.
            Inspiration: https://github.com/gxingh/RecyclerViewSwipeDelete/tree/master
              - this example uses actual swipe functionality from android
              - all that is needed to perform the 'swipe' look is setTranslationX() function.
                 - thanks to: https://developer.android.com/reference/android/view/View#setTranslationX(float)
            */
            if (isSwiped) {
                // set the swipe distance for translation
                float swipeDistance = 0.125f * holder.cardView.getWidth();

                // translationX function moves from left to right
                holder.cardView.setTranslationX(swipeDistance);
                holder.icon.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundColor(Color.parseColor("#FF4E4E"));
            } else {
                // reset the translation
                resetTranslation(holder);
            }
        });
        holder.icon.setOnClickListener(view -> {
            removeFromShift(employee);
            Snackbar snackbar = Snackbar.make(holder.itemView.findViewById(R.id.ShiftItemLL),
                            String.format("%s has been removed from this shift!", employee.getDisplayName()),
                            Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", view1 -> undoRemoval(employee));
            snackbar.show();
            resetTranslation(holder);
        });
        // detect dataset changes
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                resetTranslation(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weekendShifts.size();
    }

    private void resetTranslation(@NonNull WeekendShiftRecycler.ViewHolder holder) {
        holder.cardView.setTranslationX(0);
        holder.icon.setVisibility(View.GONE);
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void removeFromShift(Employee employee) {
        MainActivity.database.schedulingTable.deleteFromShift(employee, "weekend");
        weekendShifts.remove(employee);
        notifyDataSetChanged();
    }

    private void undoRemoval(Employee employee) {
        MainActivity.database.schedulingTable.storeDateData(employee);
        weekendShifts.add(employee);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName, qualification;
        private CardView cardView;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.DeleteShiftIcon);
            cardView = (CardView) itemView.findViewById(R.id.CardEmployeeItemShift);
            fullName = cardView.findViewById(R.id.ShiftViewFullName);
            qualification = cardView.findViewById(R.id.ShiftViewQualification);
        }
    }
}