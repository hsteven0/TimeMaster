package com.timemaster.application.ui.employee;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.ui.employee.fragments.FragmentControl;

import java.util.List;
import java.util.stream.Collectors;

/*
* This class displays each employee from the employee list with formatting from employee_item.xml
* */
public class EmployeeRecycler extends RecyclerView.Adapter<EmployeeRecycler.ViewHolder> {
    private final Context context;
    List<Employee> employeeList = MainActivity.employeeManager.getEmployeeList();
    // for displaying active employees
    List<Employee> filterEmployees = employeeList
            .stream()
            .filter(item -> "TRUE".equals(item.isActive()))
            .collect(Collectors.toList());

    public EmployeeRecycler(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get all active employees from list
        Employee employee = filterEmployees.get(position);
        String fullName = String.format("%s %s", employee.getFirstName(), employee.getLastName());
        if (!employee.getNickName().isEmpty())
            fullName = String.format("%s %s (%s)", employee.getFirstName(), employee.getLastName(), employee.getNickName());

        // set TextView text with employee information
        holder.fullName.setText(fullName);
        holder.email.setText(employee.getEmail());
        holder.phoneNumber.setText(employee.getPhoneNum());
        holder.qualification.setText(employee.getQualification());

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, EmployeeActivity.class);
            FragmentControl.setEditing(true);

            // pass in clicked employee's information to the next activity (EmployeeEditing)
            // this is to be used in the EmployeeEditingFragment.class
            i.putExtra("employee_id", employee.getEmployeeID());
            i.putExtra("first_name", employee.getFirstName());
            i.putExtra("last_name", employee.getLastName());
            i.putExtra("nick_name", employee.getNickName());
            i.putExtra("qualification", employee.getQualification());
            i.putExtra("email", employee.getEmail());
            i.putExtra("phone_number", employee.getPhoneNum());

            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return filterEmployees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName, email, phoneNumber, qualification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.ViewFullName);
            email = itemView.findViewById(R.id.ViewEmail);
            phoneNumber = itemView.findViewById(R.id.ViewPhoneNumber);
            qualification = itemView.findViewById(R.id.ViewQualification);
        }
    }
}