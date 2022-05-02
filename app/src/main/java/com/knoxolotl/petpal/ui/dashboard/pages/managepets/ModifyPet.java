package com.knoxolotl.petpal.ui.dashboard.pages.managepets;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentAddPetBinding;
import com.knoxolotl.petpal.databinding.FragmentModifyPetBinding;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ModifyPet extends Fragment {

    FragmentModifyPetBinding binding;
    View root;

    Pet petToModify;

    FirebaseFirestore db;
    SharedPreferences sp;

    ArrayList<String> foodTimes;
    ArrayList<String> medTimes;
    ArrayList<String> litterTimes;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentModifyPetBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());

        petToModify = ChooseModifyPet.getPetToModify();

        foodTimes = new ArrayList<>();
        medTimes = new ArrayList<>();
        litterTimes = new ArrayList<>();

        AutoCompleteTextView petSpeciesField = binding.petSpeciesField;

        MaterialDatePicker<Long> vetVisitDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Vet Visit Date")
                .setSelection(System.currentTimeMillis())
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .build();

        Button pickVetVisit = binding.pickVetVisit;
        pickVetVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vetVisitDatePicker.show(getActivity().getSupportFragmentManager(), "vet_visit_date_picker");

                vetVisitDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        pickVetVisit.setText(df.format(selection));

                        if (petSpeciesField.getText().toString().equals("Cat")) {  //TODO: Do this somewhere else
                            binding.litterChip.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        });

        ArrayList<String> speciesOptions = new ArrayList<>(Arrays.asList("Cat", "Dog"));
        ArrayAdapter adapter = new ArrayAdapter(root.getContext(), R.layout.species_list_item, speciesOptions);
        petSpeciesField.setAdapter(adapter);
        petSpeciesField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show the litter tag if the user has selected a cat
                if (speciesOptions.get(position).equals("Cat")) {  //TODO: Do this somewhere else
                    binding.litterChip.setVisibility(View.VISIBLE);
                }
            }
        });

        final String[] species = new String[1];

        Chip foodChip = binding.foodChip;
        //ArrayList<String> foodTimes = new ArrayList<>();
        LinearLayout foodScheduleLayout = binding.foodScheduleLayout;
        ChipGroup foodTimesHolder = binding.foodTimeChipGroup;
        foodChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Food Chip", "Tapped");
            if (isChecked) {
                foodScheduleLayout.setVisibility(View.VISIBLE);
            } else {
                foodScheduleLayout.setVisibility(View.GONE);
                foodTimes.clear();
                foodTimesHolder.removeAllViews();
            }
        });

        Button addFoodTime = binding.addFoodTime;
        addFoodTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View foodTimeView = inflater.inflate(R.layout.schedule_time_display_view, foodTimesHolder, false);
                Chip thisFoodView = foodTimeView.findViewById(R.id.time_chip);

                final String[] thisTime = new String[1];

                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setTitleText("Item time")
                        .build();

                timePicker.show(getActivity().getSupportFragmentManager(), "food_time_picker");

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        thisTime[0] = cleanTime(timePicker.getHour() + ":" + timePicker.getMinute());
                        thisFoodView.setText(thisTime[0]);
                        foodTimes.add(formatTime(timePicker.getHour(), timePicker.getMinute()));
                        Log.d("Food Times (added)", foodTimes.toString());
                        foodTimesHolder.addView(foodTimeView);
                    }
                });

                thisFoodView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foodTimesHolder.removeView(foodTimeView);
                        foodTimes.remove(formatTime(timePicker.getHour(), timePicker.getMinute()));
                        Log.d("Food Times (removed)", foodTimes.toString());
                    }
                });
            }
        });

        Chip medChip = binding.medChip;
        //ArrayList<String> medTimes = new ArrayList<>();
        ChipGroup medTimesHolder = binding.medTimeChipGroup;
        LinearLayout medScheduleLayout = binding.medScheduleLayout;
        medChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Food Chip", "Tapped");
            if (isChecked) {
                medScheduleLayout.setVisibility(View.VISIBLE);
            } else {
                medScheduleLayout.setVisibility(View.GONE);
                medTimes.clear();
                medTimesHolder.removeAllViews();
            }
        });

        Button addMedTime = binding.addMedTime;
        addMedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View medTimeView = inflater.inflate(R.layout.schedule_time_display_view, medTimesHolder, false);
                Chip thisMedView = medTimeView.findViewById(R.id.time_chip);

                final String[] thisTime = new String[1];

                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setTitleText("Item time")
                        .build();

                timePicker.show(getActivity().getSupportFragmentManager(), "med_time_picker");

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        thisTime[0] = cleanTime(timePicker.getHour() + ":" + timePicker.getMinute());
                        thisMedView.setText(thisTime[0]);
                        medTimes.add(formatTime(timePicker.getHour(), timePicker.getMinute()));
                        Log.d("Med Times (added)", medTimes.toString());
                        medTimesHolder.addView(medTimeView);
                    }
                });

                thisMedView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        medTimesHolder.removeView(medTimeView);
                        medTimes.remove(formatTime(timePicker.getHour(), timePicker.getMinute()));
                        Log.d("Med Times (removed)", medTimes.toString());
                    }
                });
            }
        });

        Chip litterChip = binding.litterChip;
        //ArrayList<String> litterTimes = new ArrayList<>();
        ChipGroup litterTimesHolder = binding.litterTimeChipGroup;
        litterChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LinearLayout litterScheduleLayout = binding.litterScheduleLayout;
            Log.d("Food Chip", "Tapped");
            if (isChecked) {
                litterScheduleLayout.setVisibility(View.VISIBLE);
            } else {
                litterScheduleLayout.setVisibility(View.GONE);
            }
        });

        Button addLitterTime = binding.addLitterTime;
        addLitterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View litterTimeView = inflater.inflate(R.layout.schedule_time_display_view, litterTimesHolder, false);
                Chip thisLitterView = litterTimeView.findViewById(R.id.time_chip);

                final String[] thisTime = new String[1];

                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setTitleText("Item time")
                        .build();

                timePicker.show(getActivity().getSupportFragmentManager(), "litter_time_picker");

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        thisTime[0] = cleanTime(timePicker.getHour() + ":" + timePicker.getMinute());
                        thisLitterView.setText(thisTime[0]);
                        litterTimes.add(formatTime(timePicker.getHour(), timePicker.getMinute()));
                        Log.d("Food Times (added)", medTimes.toString());
                        litterTimesHolder.addView(litterTimeView);
                    }
                });

                thisLitterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        litterTimesHolder.removeView(litterTimeView);
                        litterTimes.remove(formatTime(timePicker.getHour(), timePicker.getMinute()));
                        Log.d("Food Times (removed)", litterTimes.toString());
                    }
                });
            }
        });


        binding.submitPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPetRequest(foodTimes, medTimes);
            }
        });


        binding.petNameField.getEditText().setText(petToModify.getName());
        binding.petSpeciesField.setText(petToModify.getSpecies());
        binding.pickVetVisit.setText(petToModify.getNext_vet_visit());
        foodTimes = petToModify.getFood_schedule();
        medTimes = petToModify.getMed_schedule();

        if (foodTimes.size() != 0 && foodTimes != null) {
            foodChip.setChecked(true);
            foodScheduleLayout.setVisibility(View.VISIBLE);
        }
        if (medTimes.size() != 0 && medTimes != null) {
            medChip.setChecked(true);
            medScheduleLayout.setVisibility(View.VISIBLE);
        }

        for (String time : foodTimes) {
            View foodTimeView = inflater.inflate(R.layout.schedule_time_display_view, foodTimesHolder, false);
            Chip thisFoodView = foodTimeView.findViewById(R.id.time_chip);

            thisFoodView.setText(cleanTime(time));

            foodTimesHolder.addView(foodTimeView);

            thisFoodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    foodTimesHolder.removeView(foodTimeView);
                    foodTimes.remove(time);
                    Log.d("Food Times (removed)", foodTimes.toString());
                }
            });
        }

        for (String time : medTimes) {
            View medTimeView = inflater.inflate(R.layout.schedule_time_display_view, medTimesHolder, false);
            Chip thisMedView = medTimeView.findViewById(R.id.time_chip);

            thisMedView.setText(cleanTime(time));

            medTimesHolder.addView(medTimeView);

            thisMedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medTimesHolder.removeView(medTimeView);
                    medTimes.remove(time);
                    Log.d("Med Times (removed)", foodTimes.toString());
                }
            });
        }

        return root;
    }

    public String cleanTime(String inTime) {
        // Take an int hour and minute, and return a string of the time in the format "HH:MM AM/PM" in 12 hour format
        Integer hour = Integer.parseInt(inTime.split(":")[0]);
        Integer minute = Integer.parseInt(inTime.split(":")[1]);

        String minuteString = minute.toString();
        String timeSuffix = "AM";
        if (hour > 12) {
            hour -= 12;
            timeSuffix = "PM";
        }
        if (minute < 10) {
            minuteString = "0" + minuteString;
        } else if (minuteString.length() == 1) {
            minuteString = minuteString + "0";
        }

        return String.valueOf(hour) + ":" + minuteString + timeSuffix;
    }

    private String formatTime(int hourIn, int minuteIn) {
        // Take an int hour and minute, and return a string of the time in the format "HH:MM" in 24hr format
        String hour = String.valueOf(hourIn);
        String minute = String.valueOf(minuteIn);

        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        if (minute.length() == 1) {
            if (minute.equals("0")) {
                minute = "00";
            } else {
                minute = "0" + minute;
            }
        }

        return hour + ":" + minute;
    }

    private void modifyPetRequest(ArrayList<String> foodTimes, ArrayList<String> medTimes) {
        // Modify the pet request with the new details
        Map<String, Object> newPet = new HashMap<>();
        newPet.put("name", binding.petNameField.getEditText().getText().toString());
        newPet.put("species", binding.petSpeciesField.getText().toString());
        newPet.put("next_vet_visit", binding.pickVetVisit.getText().toString());
        newPet.put("food_schedule", foodTimes);
        newPet.put("med_schedule", medTimes);

        DocumentReference petRef = petToModify.getReference();

        petRef.update(newPet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "New pet successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}