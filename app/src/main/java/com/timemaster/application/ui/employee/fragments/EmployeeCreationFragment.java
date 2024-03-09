package com.timemaster.application.ui.employee.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.timemaster.application.MainActivity;
import com.timemaster.application.R;
import com.timemaster.application.employee.Employee;
import com.timemaster.application.employee.EmployeeManager;
import com.timemaster.application.ui.employee.EmployeeActivity;
import com.timemaster.application.ui.employee.ViewEmployeesActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeCreationFragment extends Fragment
{
    private TextInputEditText firstNameField, lastNameField, nickNameField, emailField, phoneNumberField;
    private AutoCompleteTextView qualificationField;
    private int employeeID;
    private String[] qualificationsList;
    private String firstName, lastName, nickName, qualification, email, phoneNumber;
    private String origFirstName, origLastName, origNickName, origQualification, origEmail, origPhoneNumber;
    private Context context;
    private OnBackPressedCallback callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        // prompt user to confirm discarding changes
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                alertBackButton();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize view objects
        Button addButton = view.findViewById(R.id.AddEmployeeButton);
        Button editButton = view.findViewById(R.id.EditEmployeeButton);
        Button archiveButton = view.findViewById(R.id.ArchiveEmployeeButton);
        // set visibilities of the buttons
        addButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        archiveButton.setVisibility(View.GONE);

        firstNameField = view.findViewById(R.id.EmployeeFirstName);
        lastNameField = view.findViewById(R.id.EmployeeLastName);
        nickNameField = view.findViewById(R.id.EmployeeNickName);
        qualificationField = view.findViewById(R.id.EmployeeQualification);
        emailField = view.findViewById(R.id.EmployeeEmail);
        phoneNumberField = view.findViewById(R.id.EmployeePhone);

        // initialize list and preset the text for qualification field
        qualificationsList = getResources().getStringArray(R.array.qualifications);
        qualification = "Trainee";
        qualificationField.setText(qualification);
        // set up View objects for editing - fill in correct data
        if (FragmentControl.isEditing()) {
            addButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            archiveButton.setVisibility(View.VISIBLE);
            initEmployeeData();
        }
        // continue setting up the dropdown using array adapter, after filling the field
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, qualificationsList);
        qualificationField.setAdapter(adapter);

        // get selected item from user
        qualificationField.setOnItemClickListener((adapterView, view12, pos, l) -> {
            String oldItem = qualification;
            String selectedItem = (String) adapterView.getItemAtPosition(pos);
            qualificationField.setText(selectedItem);
            qualification = selectedItem;
            // convert the array[] to ArrayList
            List<String> arrayList = new ArrayList<>(Arrays.asList(qualificationsList));
            arrayList.remove(selectedItem);
            arrayList.add(oldItem);
            // convert ArrayList back to array[]
            qualificationsList = arrayList.toArray(new String[0]);
            // change adapter to adjusted array
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, qualificationsList);
            qualificationField.setAdapter(adapter1);
        });
        initKeyListeners();

        // listen for button clicks, perform actions when buttons are clicked
        addButton.setOnClickListener(view1 -> addEmployeeButton());
        editButton.setOnClickListener(view1 -> editEmployeeButton());
        archiveButton.setOnClickListener(view1 -> archiveEmployeeButton());
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout linearLayout = ((LinearLayout) EmployeeActivity.tabs.getChildAt(0));
        // set a listener to detect whether "Availability" tab is clicked
        linearLayout.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // tab has been clicked...
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // display toast if not creatable, and don't let user access this tab
                    //   - NOTE: creatable if all the fields are filled out.
                    if (!determineCreatable()) {
                        Toast.makeText(view.getContext(),
                                "Please enter employee information before setting availability!",
                                Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean determineCreatable() {
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        email = emailField.getText().toString();
        phoneNumber = phoneNumberField.getText().toString();
        return (!(firstName.isEmpty() || lastName.isEmpty() ||
                email.isEmpty() || phoneNumber.isEmpty()));
    }

    public void addEmployeeButton() {
        // Button ID: AddEmployeeButton
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        nickName = nickNameField.getText().toString();
        email = emailField.getText().toString();
        phoneNumber = phoneNumberField.getText().toString();
        qualification = qualificationField.getText().toString();
        Employee employee = new Employee(firstName, lastName, qualification, email, phoneNumber);

        if (errorCheckFields(firstName, lastName, nickName, email, phoneNumber, employee, getContext())) {
          /*  need to subtract 1, because the employee object has been created and the ID is
              incremented by 1. then if errorCheckFields() returns true, the employee object's
              ID becomes ID+1. If user attempts to add again, the id stays as ID+1, skipping
              an employee. Ex. 1, 2, 4. (3 failed, so nothing in database and it stays as 3)*/
            EmployeeManager.ID--;
            return;
        }
        String success = String.format("%s %s has been added!", firstName, lastName);
        if (!nickName.isEmpty()) {
            employee.setNickName(nickName);
            success = String.format("%s %s (%s) has been added!", firstName, lastName, nickName);
        }
        this.redirectToEmployeeList();

        // add employee to database and EmployeeManager
        MainActivity.database.employeeTable.saveEmployee(employee);
        MainActivity.database.availabilityTable.storeAvailability(employee);

        // reset fields and display a successful message
        Toast.makeText(getContext(), success, Toast.LENGTH_LONG).show();
        firstNameField.setText("");
        lastNameField.setText("");
        nickNameField.setText("");
        emailField.setText("");
        phoneNumberField.setText("");
    }

    public void editEmployeeButton() {
        // button ID: EditEmployeeButton
        // use employee's id to get correct employee from the main, unfiltered list.
        // index positions in an original list do not stay the same in a filtered list
        Employee employee = MainActivity.employeeManager.getEmployeeList().get(employeeID);
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        nickName = nickNameField.getText().toString();
        email = emailField.getText().toString();
        phoneNumber = phoneNumberField.getText().toString();
        qualification = qualificationField.getText().toString();
        if (errorCheckFields(firstName, lastName, nickName, email, phoneNumber, employee, getContext())) {
            return;
        }

        // pass in edited values and the employee object from list for edit function in database
        MainActivity.database.employeeTable.editEmployee(employee,
                firstName, lastName, nickName,
                qualification,
                email, phoneNumber);
        // if null is in the available_days list, this means that the AvailabilityFragment
        // was not loaded. Therefore, there is nothing to update, as the user didn't even
        // check that Availability tab in Edit mode.
        if (!EmployeeAvailabilityFragment.available_days.contains("null"))
            MainActivity.database.availabilityTable.updateAvailability(employee);
        Toast.makeText(getContext(), "Employee successfully saved!", Toast.LENGTH_LONG).show();
        this.redirectToEmployeeList();
    }

    public void archiveEmployeeButton() {
        // button ID: ArchiveEmployeeButton
        Employee employee = MainActivity.employeeManager.getEmployeeList().get(employeeID);
        String message = String.format("Are you sure you want to archive employee: %s %s?",
                employee.getFirstName(), employee.getLastName());
        if (!employee.getNickName().isEmpty()) {
            message = String.format("Are you sure you want to archive employee: %s %s (%s)?",
                    employee.getFirstName(), employee.getLastName(), employee.getNickName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle("Archive Employee");

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MainActivity.database.employeeTable.archiveEmployee(employee);
            String success = String.format("%s %s has been archived!", employee.getFirstName(), employee.getLastName());
            if (!employee.getNickName().isEmpty()) {
                success = String.format("%s %s (%s) has been archived!",
                        employee.getFirstName(), employee.getLastName(), employee.getNickName());
            }

            // notify user that employee has been archived
            Toast.makeText(getContext(), success, Toast.LENGTH_LONG).show();
            this.redirectToEmployeeList();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> { /* do nothing */ });
        builder.show();
    }

    private void redirectToEmployeeList() {
        FragmentControl.setEditing(false);
        Intent intent = new Intent(getContext(), ViewEmployeesActivity.class);
        startActivity(intent);
    }

    private void initEmployeeData() {
        // retrieve values from employee clicked on the ViewList
        employeeID = this.getActivity().getIntent().getIntExtra("employee_id", 0);
        firstName = this.getActivity().getIntent().getStringExtra("first_name");
        lastName = this.getActivity().getIntent().getStringExtra("last_name");
        nickName = this.getActivity().getIntent().getStringExtra("nick_name");
        qualification = this.getActivity().getIntent().getStringExtra("qualification");
        email = this.getActivity().getIntent().getStringExtra("email");
        phoneNumber = this.getActivity().getIntent().getStringExtra("phone_number");
        // "null" is used to check if the availability needs to be updated
        EmployeeAvailabilityFragment.available_days.add("null");

        // pre-set text in text box fields with employee data
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        nickNameField.setText(nickName);
        qualificationField.setText(qualification);
        emailField.setText(email);
        phoneNumberField.setText(phoneNumber);

        // change dropdown options
        List<String> list = new ArrayList<>();
        list.add("Closing");
        list.add("Opening");
        list.add("Opening and Closing");
        list.add("Trainee");
        list.remove(qualification);
        qualificationsList = list.toArray(new String[0]);

        // set these values as original data
        origFirstName = firstName;
        origLastName = lastName;
        origNickName = nickName;
        origQualification = qualification;
        origEmail = email;
        origPhoneNumber = phoneNumber;
    }

    private boolean errorCheckFields(String firstName, String lastName, String nickName, String email, String phoneNumber, Employee e, Context context) {
        // has user filled out all the fields?
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(context, "Make sure all fields are filled out!", Toast.LENGTH_LONG).show();
            return true;
        }

        String dupeCheck = MainActivity.employeeManager.employeeDupeInfo(firstName, lastName, email, phoneNumber);
        // dupe found and don't compare employee object with itself when editing
        if (dupeCheck != null && EmployeeActivity.employee != e) {
            switch (dupeCheck) {
                case "name":
                    // same name, but no nick name entered
                    Employee nickedEmployee = MainActivity.employeeManager.getEmployeeByNickName(nickName);
                    if (nickName.isEmpty()) {
                        Toast.makeText(context,
                                String.format("Employee %s %s already exists! Consider adding a nick name!",
                                        firstName, lastName),
                                Toast.LENGTH_LONG).show();
                        // same name, same nick name
                    } else if (nickedEmployee != null && !e.getNickName().equals(nickedEmployee.getNickName())) {
                        Toast.makeText(context,
                                String.format("%s %s already has the nick name: %s!",
                                        nickedEmployee.getFirstName(), nickedEmployee.getLastName(), nickName),
                                Toast.LENGTH_LONG).show();
                        // same name, different nicks? Valid.
                    } else return false;
                    return true;
                case "phone":
                    Toast.makeText(context,
                            String.format("Employee %s %s already has the phone number: %s!",
                                    EmployeeActivity.employee.getFirstName(),
                                    EmployeeActivity.employee.getLastName(), phoneNumber),
                            Toast.LENGTH_LONG).show();
                    return true;
                case "email":
                    Toast.makeText(context,
                            String.format("Employee %s %s already has the email: %s!",
                                    EmployeeActivity.employee.getFirstName(),
                                    EmployeeActivity.employee.getLastName(), email),
                            Toast.LENGTH_LONG).show();
                    return true;
            }
        }

        // has user entered a valid email address?
        if (!MainActivity.employeeManager.checkEmail(email)) {
            Toast.makeText(context, "Please enter a valid email!", Toast.LENGTH_LONG).show();
            return true;
        }
        // has user entered a valid phone number? length = 10? contains non-digits?
        if (!MainActivity.employeeManager.checkPhoneNumber(phoneNumber) || !phoneNumber.matches("[0-9]+")) {
            Toast.makeText(context, "Please enter a valid phone number!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void alertBackButton() {
        if (dontAlert()) {
            // all fields are empty? -> don't need to prompt user
            goBack();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to discard your changes?");
        builder.setTitle("Discard Changes");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> goBack());
        builder.setNegativeButton("No", (dialogInterface, i) -> { /* do nothing */ });
        builder.show();
    }

    private void goBack() {
        // disable callback and force call onBackPressed() to go back
        callback.setEnabled(false);
        FragmentControl.setEditing(false);
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    private boolean dontAlert() {
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        nickName = nickNameField.getText().toString();
        qualification = qualificationField.getText().toString();
        email = emailField.getText().toString();
        phoneNumber = phoneNumberField.getText().toString();
        if (FragmentControl.isEditing()) {
            return origFirstName.equals(firstName) && origLastName.equals(lastName) &&
                   origNickName.equals(nickName) && origQualification.equals(qualification) &&
                   origEmail.equals(email) && origPhoneNumber.equals(phoneNumber);
        }
        return firstName.isEmpty() && lastName.isEmpty()
                && email.isEmpty() && phoneNumber.isEmpty();
    }

    private void initKeyListeners() {
        // listener for every edit field, to check if the fields are all filled out
        // then, the user is granted swiping capabilities to get to the next
        // fragment for availability.
        firstNameField.setOnKeyListener((view15, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (determineCreatable()) {
                    EmployeeActivity.viewPager2.setUserInputEnabled(true);
                } else EmployeeActivity.viewPager2.setUserInputEnabled(false);
            }
            return false;
        });
        lastNameField.setOnKeyListener((view14, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (determineCreatable()) {
                    EmployeeActivity.viewPager2.setUserInputEnabled(true);
                } else EmployeeActivity.viewPager2.setUserInputEnabled(false);
            }
            return false;
        });
        emailField.setOnKeyListener((view13, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (determineCreatable()) {
                    EmployeeActivity.viewPager2.setUserInputEnabled(true);
                } else EmployeeActivity.viewPager2.setUserInputEnabled(false);
            }
            return false;
        });
        phoneNumberField.setOnKeyListener((view12, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (determineCreatable()) {
                    EmployeeActivity.viewPager2.setUserInputEnabled(true);
                } else EmployeeActivity.viewPager2.setUserInputEnabled(false);
            }
            return false;
        });
    }

    public interface IFragmentListener
    {
        void addEmployeeButton();
        void editEmployeeButton();
        void archiveEmployeeButton();
    }
}