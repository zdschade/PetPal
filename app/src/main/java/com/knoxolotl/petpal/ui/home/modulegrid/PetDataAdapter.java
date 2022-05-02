package com.knoxolotl.petpal.ui.home.modulegrid;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.DataHistory;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PetDataAdapter extends RecyclerView.Adapter<PetDataAdapter.StaggeredGridViewHolder> {
    // adapter for the pet data grid
    // Chunky boi, does a lot of things

    Pet pet;
    Context context;
    View view;
    ArrayList<String> values;
    ArrayList<View> checkBoxViews;  // holds checkbox views to set click listeners on. This is the only way it would work

    RequestQueue queue;

    String username;

    SharedPreferences sp;

    private final OnItemClickListener listener;  // click listener. Not used for now, might expand widget later

    // the actual click listener interface
    public interface OnItemClickListener {
        void onItemClick(Pet pet);
    }

    public PetDataAdapter(Pet pet, Context context, OnItemClickListener listener) {
        this.pet = pet;
        this.context = context;
        this.listener = listener;
        queue = Volley.newRequestQueue(context);

        values = pet.getNonEmptyValues();  // get all filled-in values of pet_data
        values.add("water");  // every pet needs water!

        if (pet.getSpecies().equals("Dog")) {  // add walk category for dogs
            values.add("walk");
        }

        this.username = MainActivity.getCurrentUser().getEmail();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public PetDataAdapter.StaggeredGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stag_grid_item, parent, false);

        return new StaggeredGridViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StaggeredGridViewHolder holder, int position) {
        holder.bind(pet, listener);  // binder for the click listener

        if (values == null || pet == null) {
            return;
        }

        // title setup
        String title = values.get(position);
        Log.d("Title", title);
        if (title.equals("med")) {
            holder.itemTitle.setText("Medication");  // fix for medication
        } else {
            holder.itemTitle.setText(title.substring(0, 1).toUpperCase() + title.substring(1));  // capitalize first letter);
        }

        LayoutInflater inflater = LayoutInflater.from(context);

        // DateTime formatters
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dtfWater = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");

        //Log.d("Current pet data", pet.toString());

        // sets up the widget based on the current data
        ArrayList<String> schedule = new ArrayList<>();
        String history_type = "";
        Boolean chipSetting = MainActivity.getCurrentUser().getChip();  // setting for chip display
        View incrementView = inflater.inflate(R.layout.simple_incrementer, holder.checkboxHolder, false);
        Button thisIV = incrementView.findViewById(R.id.display_increment_button);

        // Choose which widget to set up based on the title
        switch(title) {
            case "food":
                schedule = pet.getFood_schedule();
                history_type = "food_history";
                break;
            case "med":
                schedule = pet.getMed_schedule();
                history_type = "med_history";
                break;
            case "next vet visit":
                //TODO: Format data to be pretty
                if (pet.getNext_vet_visit().equals("Next vet visit")) {
                    holder.dataPreview.setText("No next vet visit");
                } else {
                    holder.dataPreview.setText(pet.getNext_vet_visit());
                }
                holder.dataPreview.setVisibility(View.VISIBLE);
                return;
            case "water":
                //TODO: Format water better

                // This adds an incrementer instead of checkboxes
                holder.dataPreview.setText(String.valueOf(pet.getWater_history().size()));
                holder.dataPreview.setVisibility(View.VISIBLE);
                holder.dataPreview.setTextSize(24);

                holder.checkboxHolder.addView(incrementView);

                // On increment button click, add a new water history entry
                thisIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.dataPreview.setText(String.valueOf(pet.getWater_history().size() + 1));
                        Map<String, Object> newData = new HashMap<>();
                        newData.put("type", "water_history");
                        newData.put("log_time", dtfWater.format(LocalDateTime.now()));
                        newData.put("username", username);

                        updatePetData("water_history", newData, "add", null);
                    }
                });
                return;
            case "walk":
                // Walk widget

                if (pet.getWalk_history().size() == 0) {  // if there is no walk history
                    View simpleTextView = inflater.inflate(R.layout.simple_textview, holder.textviewHolder, false);
                    holder.textviewHolder.addView(simpleTextView);
                    TextView thisTextView = simpleTextView.findViewById(R.id.display_textview);
                    thisTextView.setText("No walks recorded");
                } else {  // if there is walk history
                    for (DataHistory walk : pet.getWalk_history()) {  // for every walk history entry
                        Log.d("Walk", walk.toString());
                        if (walk.getLog_time().split("-")[0].equals(LocalDateTime.now().format(dtf))) {
                            View simpleTextView = inflater.inflate(R.layout.simple_textview, holder.textviewHolder, false);
                            holder.textviewHolder.addView(simpleTextView);

                            TextView thisTextView = simpleTextView.findViewById(R.id.display_textview);
                            Log.d("Walk times", walk.getLog_time().split("-")[0] + " " + LocalDateTime.now().format(dtf));
                            thisTextView.setText(cleanTime(String.valueOf(walk.getLog_time().split("-")[1].substring(0, 5))));
                        }
                    }
                }

                holder.textviewHolder.setVisibility(View.VISIBLE);

                // on increment button click, add a new walk history entry
                holder.checkboxHolder.addView(incrementView);
                thisIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.dataPreview.setText(String.valueOf(pet.getWater_history().size() + 1));
                        Map<String, Object> newData = new HashMap<>();
                        newData.put("type", "walk_history");
                        newData.put("log_time", dtfWater.format(LocalDateTime.now()));
                        newData.put("username", username);

                        updatePetData("walk_history", newData, "add", null);
                    }
                });
                return;
            default:  // redundant default case
                schedule = new ArrayList<>();
                history_type = "";
        }

        // Set up checkboxes/chips for schedule items
        // Checkbox or chip is defined by a user setting

        checkBoxViews = new ArrayList<>();  // Need to hold there here to add onclick listeners

        for (String item : schedule) {  // for every schedule item
            View checkboxView;

            if (chipSetting) {  // if the chip setting is on, use chips
                checkboxView = inflater.inflate(R.layout.simple_chip, holder.chipHolder, false);
                Chip thisCB = checkboxView.findViewById(R.id.display_chip);
                thisCB.setText(cleanTime(item));

                if ((pet.getDaysItemHistory(title, dtf.format(LocalDateTime.now())).contains(item))) {
                    Log.d("contains", title + " " + item);
                    thisCB.setChecked(true);
                }
                holder.chipHolder.addView(checkboxView);

            } else {  // if the chip setting is off, use checkboxes
                checkboxView = inflater.inflate(R.layout.simple_checkbox, holder.checkboxHolder, false);
                CheckBox thisCB = checkboxView.findViewById(R.id.display_checkbox);

                if (schedule.size() > 3) {  // if there are more than 3 items, switch display to vertical and add time display
                    thisCB.setText(cleanTime(item));
                    thisCB.setMinWidth(100);
                    thisCB.setWidth(400);
                    holder.checkboxHolder.setOrientation(LinearLayout.VERTICAL);
                }

                if ((pet.getDaysItemHistory(title, dtf.format(LocalDateTime.now())).contains(item))) {
                    Log.d("contains", title + " " + item);
                    thisCB.setChecked(true);
                }
                holder.checkboxHolder.addView(checkboxView);
            }

            checkBoxViews.add(checkboxView);  // add to checkbox view list
        }

        // Set up onclick listeners for checkboxes/chips
        for (int c = 0; c < checkBoxViews.size(); c++) {
            CheckBox thisCheckbox;
            if (chipSetting) {  // if the chip setting is on, use chips
                thisCheckbox = checkBoxViews.get(c).findViewById(R.id.display_chip);
            } else {  // if the chip setting is off, use checkboxes
                thisCheckbox = checkBoxViews.get(c).findViewById(R.id.display_checkbox);
            }
            //Chip thisCheckbox = checkBoxViews.get(c).findViewById(R.id.display_chip);
            int finalC = c;
            ArrayList<String> finalSchedule = schedule;
            String finalHistory_type = history_type;

            // Add CheckChange listener to checkbox/chip
            thisCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Map<String, Object> newData = new HashMap<>();
                newData.put("type", finalHistory_type);
                newData.put("item_time", finalSchedule.get(finalC));
                newData.put("username", username);
                newData.put("log_time", dtf.format(LocalDateTime.now()));

                // update pet data based on check change
                if (isChecked) {  // if the checkbox is checked after interaction
                    //updateDataRequest(finalHistory_type, newData, "add", thisCheckbox);
                    updatePetData(finalHistory_type, newData, "add", thisCheckbox);
                } else if (!isChecked) {  // if the checkbox is unchecked after interaction
                    //updateDataRequest(finalHistory_type, newData, "remove", thisCheckbox);
                    updatePetData(finalHistory_type, newData, "remove", thisCheckbox);
                }
            });
        }

        // click listener for each widget. Might add a feature later
        holder.stagGridItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(pet);
                Log.d("Something", "Clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class StaggeredGridViewHolder extends RecyclerView.ViewHolder {
        LinearLayout stagGridItemLayout;
        TextView itemTitle;
        TextView dataPreview;
        Button stagIncrement;
        LinearLayout checkboxHolder;
        ChipGroup chipHolder;
        LinearLayout textviewHolder;


        public StaggeredGridViewHolder(@NonNull View itemView) {
            super(itemView);
            stagGridItemLayout = itemView.findViewById(R.id.stag_grid_item_layout);
            itemTitle = itemView.findViewById(R.id.item_title);
            dataPreview = itemView.findViewById(R.id.data_preview);
            stagIncrement = itemView.findViewById(R.id.stag_increment);
            checkboxHolder = itemView.findViewById(R.id.checkbox_holder);
            chipHolder = itemView.findViewById(R.id.chip_holder);
            textviewHolder = itemView.findViewById(R.id.textview_holder);
        }

        // I have no idea why I need this but it doesn't work without it
        public void bind(final Pet item, final PetDataAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public void updatePetData(String dataType, Map<String, Object> newValue, String operation, CheckBox thisCheckbox) {
        // Update pet data

        if (operation.equals("add")) {  // for adding data
            pet.getReference().update(dataType, FieldValue.arrayUnion(newValue))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            if (thisCheckbox != null) {  // update checkbox
                                thisCheckbox.setChecked(true);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        } else {  // for removing data
            pet.getReference().update(dataType, FieldValue.arrayRemove(newValue))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            if (thisCheckbox != null) {  // update checkbox
                                thisCheckbox.setChecked(false);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
    }

    public String cleanTime(String inTime) {
        // Take 24hr string, output in HH:MM AM/PM format
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
}

