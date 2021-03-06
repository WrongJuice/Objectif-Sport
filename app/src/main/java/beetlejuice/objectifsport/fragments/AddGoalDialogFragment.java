package beetlejuice.objectifsport.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import beetlejuice.objectifsport.R;
import beetlejuice.objectifsport.Services.DataManager;
import beetlejuice.objectifsport.activities.MainActivity;
import beetlejuice.objectifsport.adapters.MainFragmentPageAdapter;
import beetlejuice.objectifsport.model.Sport;
import beetlejuice.objectifsport.model.goals.Goal;

import org.threeten.bp.Duration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import mobi.upod.timedurationpicker.TimeDurationPickerDialog;

/**
 * The type Add goal dialog fragment.
 */
public class AddGoalDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener{

    private EditText timeGoalInput;
    private EditText distanceGoalInput;
    private EditText deadlineGoalInput;
    /**
     * The Time goal.
     */
    CheckBox timeGoal;
    /**
     * The Distance goal.
     */
    CheckBox distanceGoal;
    /**
     * The Deadline.
     */
    CheckBox deadline;

    private Calendar deadlineDate; // ... \-(^|^)-/
    private Duration timeGoalDuration; // milli
    private Double distance; // km
    private Sport selectedSport;

    /**
     * Instantiates a new Add goal dialog fragment.
     */
    public AddGoalDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    /**
     * New instance add goal dialog fragment.
     *
     * @return the add goal dialog fragment
     */
    public static AddGoalDialogFragment newInstance() {
        return new AddGoalDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_goal_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Get views
        EditText goalDescription = view.findViewById(R.id.activity_description);
        Spinner sportSelect = view.findViewById(R.id.sport_select);
        Button cancel = view.findViewById(R.id.cancel);
        Button addGoal = view.findViewById(R.id.add_goal);
        timeGoal = view.findViewById(R.id.time_goal);
        distanceGoal = view.findViewById(R.id.distance_goal);
        deadline = view.findViewById(R.id.deadline);
        timeGoalInput = view.findViewById(R.id.time_goal_input);
        distanceGoalInput = view.findViewById(R.id.distance_goal_input);
        deadlineGoalInput = view.findViewById(R.id.deadline_goal_input);
        RelativeLayout timeContainer = view.findViewById(R.id.time_container);
        RelativeLayout distanceContainer = view.findViewById(R.id.distance_container);

        // Populate views
        Objects.requireNonNull(getDialog()).setTitle("Add Activity");
        ArrayList<String> sportNames = new ArrayList<>();
        for (Sport sport : DataManager.getSports()) sportNames.add(sport.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, sportNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSelect.setAdapter(adapter);

        goalDescription.requestFocus(); // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        addGoal.setEnabled(false); // disabled by default

        // When you select a new sport
        sportSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // get the sport associated
                selectedSport = DataManager.getSports().get(sportSelect.getSelectedItemPosition());
                if (selectedSport.getAuthorizedGoals() == 1) {
                    distanceContainer.setVisibility(View.GONE);
                    timeContainer.setVisibility(View.VISIBLE);
                    addGoal.setEnabled(timeGoal.isChecked());
                    timeGoal.setOnCheckedChangeListener((buttonView, isChecked) ->{
                        inputVisibiliytyManager();
                        if (timeGoal.isChecked()) displayTimePicker(view.getContext());
                        addGoal.setEnabled(isChecked);
                    });
                    deadline.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        inputVisibiliytyManager();
                        if (deadline.isChecked()) displayDatePicker(view.getContext());
                    });
                } else if (selectedSport.getAuthorizedGoals() == 2) {
                    timeContainer.setVisibility(View.GONE);
                    distanceContainer.setVisibility(View.VISIBLE);
                    addGoal.setEnabled(distanceGoal.isChecked());
                    distanceGoal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        inputVisibiliytyManager();
                        addGoal.setEnabled(isChecked);
                        distanceGoalInput.requestFocus();
                    });
                    deadline.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        inputVisibiliytyManager();
                        if (deadline.isChecked()) displayDatePicker(view.getContext());
                    });
                } else {
                    timeContainer.setVisibility(View.VISIBLE);
                    distanceContainer.setVisibility(View.VISIBLE);
                    timeGoal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        inputVisibiliytyManager();
                        if (timeGoal.isChecked()) displayTimePicker(getActivity());
                        addGoal.setEnabled(isChecked || distanceGoal.isChecked());
                    });
                    distanceGoal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        inputVisibiliytyManager();
                        addGoal.setEnabled(isChecked || timeGoal.isChecked());
                        distanceGoalInput.requestFocus();
                    });
                    deadline.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        inputVisibiliytyManager();
                        if (deadline.isChecked()) displayDatePicker(getActivity());
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        // If we click on the deadlineGoalInput we re-post the datePickerDialog
        deadlineGoalInput.setOnClickListener(v -> displayDatePicker(view.getContext()));

        // If we click on the timeGoalInput we re-post the timePickerDialog
        timeGoalInput.setOnClickListener(v -> displayTimePicker(view.getContext()));

        // Try to add the goal
        addGoal.setOnClickListener(v -> {
            // valid inputs
            switch (selectedSport.getAuthorizedGoals()) {
                case 1 :
                    if (deadline.isChecked() && deadlineDate == null)
                        deadlineGoalInput.setError(getResources().getString(R.string.deadline_goal_duration_msg));
                    else if (timeGoalDuration == null)
                        timeGoalInput.setError(getResources().getString(R.string.time_goal_duration_msg));
                    else {
                        // we add (sport 1)
                        if (deadline.isChecked())
                            DataManager.addGoal(new Goal(selectedSport,
                                    goalDescription.getText().toString(), deadlineDate.getTime(), timeGoalDuration));
                        else DataManager.addGoal(new Goal(selectedSport, goalDescription.getText().toString(), timeGoalDuration));
                        dismiss();
                        break;
                    }
                    break;
                case 2 :
                    try {
                        distance = Double.valueOf(distanceGoalInput.getText().toString());
                    } catch (Exception e) {
                        distanceGoalInput.setError(getResources().getString(R.string.distance_goal_duration_msg));
                        break;
                    }
                    if (deadline.isChecked() && deadlineDate == null)
                        deadlineGoalInput.setError(getResources().getString(R.string.deadline_goal_duration_msg));
                    else {
                        // we add (sport 2)
                        if (deadline.isChecked())
                            DataManager.addGoal(new Goal(selectedSport,
                                    goalDescription.getText().toString(), deadlineDate.getTime(), distance));
                        else DataManager.addGoal(new Goal(selectedSport, goalDescription.getText().toString(), distance));
                        dismiss();
                        break;
                    }
                    break;
                default:
                    int objective = (distanceGoal.isChecked() && timeGoal.isChecked())? 0
                            :(timeGoal.isChecked()? 1:2);
                    switch (objective) {
                        case 1 :
                            if (timeGoalDuration == null)
                                timeGoalInput.setError(getResources().getString(R.string.time_goal_duration_msg));
                            else if (deadline.isChecked() && deadlineDate == null)
                                deadlineGoalInput.setError(getResources().getString(R.string.deadline_goal_duration_msg));
                            else {
                                // we add (sport 0 with 1 checked)
                                if (deadline.isChecked())
                                    DataManager.addGoal(new Goal(selectedSport,
                                            goalDescription.getText().toString(), deadlineDate.getTime(), timeGoalDuration));
                                else DataManager.addGoal(new Goal(selectedSport, goalDescription.getText().toString(), timeGoalDuration));
                                dismiss();
                                break;
                            }
                            break;
                        case 2 :
                            try {
                                distance = Double.valueOf(distanceGoalInput.getText().toString());
                            } catch (Exception e) {
                                distanceGoalInput.setError(getResources().getString(R.string.distance_goal_duration_msg));
                                break;
                            }
                            if (deadline.isChecked() && deadlineDate == null)
                                deadlineGoalInput.setError(getResources().getString(R.string.deadline_goal_duration_msg));
                            else {
                                // we add (sport 0 with 2 checked)
                                if (deadline.isChecked())
                                    DataManager.addGoal(new Goal(selectedSport,
                                            goalDescription.getText().toString(), deadlineDate.getTime(), distance));
                                else DataManager.addGoal(new Goal(selectedSport, goalDescription.getText().toString(), distance));
                                dismiss();
                                break;
                            }
                            break;
                        default :
                            try {
                                distance = Double.valueOf(distanceGoalInput.getText().toString());
                            } catch (Exception e) {
                                distanceGoalInput.setError(getResources().getString(R.string.distance_goal_duration_msg));
                                break;
                            }
                            if (timeGoalDuration == null) {
                                timeGoalInput.setError(getResources().getString(R.string.time_goal_duration_msg));
                                break;
                            } else if (deadline.isChecked() && deadlineDate == null)
                                deadlineGoalInput.setError(getResources().getString(R.string.deadline_goal_duration_msg));
                            else {
                                // we add (sport 0 with 0 checked)
                                if (deadline.isChecked())
                                    DataManager.addGoal(new Goal(selectedSport,
                                            goalDescription.getText().toString(), deadlineDate.getTime(), timeGoalDuration, distance));
                                else DataManager.addGoal(new Goal(selectedSport, goalDescription.getText().toString(), timeGoalDuration, distance));
                                dismiss();
                                break;
                            }
                    }
            }
            MainFragmentPageAdapter mFPA = ((MainActivity) getActivity()).getMainFragmentPageAdapter();
            if (mFPA.getMyGoalsFragment() != null)
                mFPA.getMyGoalsFragment().getGoalAdapter().notifyDataSetChanged();
        });

        cancel.setOnClickListener(v -> {
            // close dialog
            dismiss();
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("Selected_is_"+position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Input visibiliyty manager.
     */
// Repeated code, if a specific sport is selected, some views need to disappear
    public void inputVisibiliytyManager(){
        if (timeGoal.isChecked()) timeGoalInput.setVisibility(View.VISIBLE);
        else timeGoalInput.setVisibility(View.GONE);
        if (distanceGoal.isChecked()) distanceGoalInput.setVisibility(View.VISIBLE);
        else distanceGoalInput.setVisibility(View.GONE);
        if (deadline.isChecked()) deadlineGoalInput.setVisibility(View.VISIBLE);
        else deadlineGoalInput.setVisibility(View.GONE);
    }

    /**
     * Display date picker.
     *
     * @param context the context
     */
    public void displayDatePicker(Context context){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(context,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    deadlineDate = Calendar.getInstance();
                    deadlineDate.set(year1, monthOfYear, dayOfMonth);
                    String dateText = deadlineDate.get(Calendar.DAY_OF_MONTH) + "/" +
                            (deadlineDate.get(Calendar.MONTH) + 1) + "/" + deadlineDate.get(Calendar.YEAR);
                    deadlineGoalInput.setText(dateText);
                }, year, month, day);
        picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        picker.show();
    }

    /**
     * Display time picker.
     *
     * @param context the context
     */
    public void displayTimePicker(Context context){
        TimeDurationPickerDialog picker = new TimeDurationPickerDialog(context, (view, duration) -> {
            timeGoalDuration = Duration.ofMillis(duration);
            int hours = (int)(timeGoalDuration.getSeconds()/3600);
            int minutes = (int)((timeGoalDuration.getSeconds()%3600)/60);
            int seconds = (int)((timeGoalDuration.getSeconds()%3600)%60);
            String durationStr = hours + "h:" + minutes + "m:" + seconds + "s";
            timeGoalInput.setText(durationStr);
        }, 0);
        picker.show();
    }

}
