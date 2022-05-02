package com.knoxolotl.petpal.ui.home;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.knoxolotl.petpal.AlarmReceiver;
import com.knoxolotl.petpal.DataHistory;
import com.knoxolotl.petpal.Household;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentHomeBinding;
import com.knoxolotl.petpal.ui.home.carousel.CarouselAdapter;
import com.knoxolotl.petpal.ui.home.modulegrid.PetDataAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;
    private RequestQueue queue;
    private JsonArrayRequest allPetsRequest;
    private ArrayList<Pet> petsArray;
    private ArrayList<Household> householdsArray;
    public CarouselAdapter adapter;
    private RecyclerView carouselView;
    private RecyclerView stagGridView;
    public PetDataAdapter stagGridAdapter;
    LinearLayoutManager layoutManager;
    StaggeredGridLayoutManager stagLayoutManager;
    PagerSnapHelper snapHelper;
    int currSelectedPet;
    SharedPreferences sp;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    ArrayList<DocumentReference> householdRefs;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        petsArray = new ArrayList<Pet>();  // array of pets
        householdsArray = new ArrayList<Household>(); // array of households
        householdRefs = new ArrayList<DocumentReference>();

        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());

        // set up carousel
        carouselView = root.findViewById(R.id.carousel);
        layoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false);
        carouselView.setLayoutManager(layoutManager);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(carouselView);  // attach the snap helper to the carousel

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference householdReference = db.document("households/" + sp.getString("household", null));

        currSelectedPet = 0;  // set the current selected pet to 0

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference householdReference = db.document("households/" + sp.getString("household", null));

        db.collection("pets").whereEqualTo("household", householdReference)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("PetListener", "Listen failed.", error);
                            return;
                        }

                        petsArray = null;
                        petsArray = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getData().get("name") != null) {
                                Pet thisPet = doc.toObject(Pet.class);
                                thisPet.setReference(doc.getReference());
                                fixMissingValues(thisPet);
                                Log.d("MainActivity", "Got pet: " + thisPet.toString());
                                petsArray.add(thisPet);
                            }
                        }
                        Log.d("Pet Requesting", "Local request");

                        if (petsArray.size() > 0) {
                            populatePets(petsArray);
                        }

                        if (isAdded()) {
                            requireActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                            //notifSetup();
                        }


                        //Log.d("HomeFragment", MainActivity.getCurrentHousehold().toString());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populatePets(ArrayList<Pet> petArray) {
        Log.d("HomeFragment", "Populating pets");
        adapter = new CarouselAdapter(petArray, root.getContext(), new CarouselAdapter.OnItemClickListener() {
            public void onItemClick(Pet pet) {
                //((ViewGroup)stagGridView.getParent()).removeView(stagGridView);

                currSelectedPet = adapter.getItemPosition(pet);

                stagGridView = root.findViewById(R.id.staggered_grid);
                stagLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                stagGridView.setLayoutManager(stagLayoutManager);

                stagGridAdapter = new PetDataAdapter(pet, root.getContext(), new PetDataAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Pet item) {
                        Log.d("Clicked", item.toString());
                    }
                });
                stagGridView.setAdapter(stagGridAdapter);

                adapter.scrollToPet(carouselView, adapter.getItemPosition(pet));
                showNextUp(pet);
            }
        });

        carouselView.setAdapter(adapter);

        stagGridView = root.findViewById(R.id.staggered_grid);
        stagLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        stagGridView.setLayoutManager(stagLayoutManager);

        stagGridAdapter = new PetDataAdapter(petsArray.get(currSelectedPet), root.getContext(), new PetDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pet item) {
                Log.d("Clicked", item.toString());
            }
        });
        stagGridView.setAdapter(stagGridAdapter);

        adapter.scrollToPet(carouselView, currSelectedPet);
        showNextUp(adapter.getCurrentPet());
    }

    private static Pet fixMissingValues(Pet pet) {
        if (pet.getMed_schedule() == null) {
            pet.setMed_schedule(new ArrayList<String>());
        } if (pet.getFood_schedule() == null) {
            pet.setFood_schedule(new ArrayList<String>());
        } if (pet.getFood_history() == null) {
            pet.setFood_history(new ArrayList<DataHistory>());
        } if (pet.getMed_history() == null) {
            pet.setMed_history(new ArrayList<DataHistory>());
        } if (pet.getWalk_history() == null) {
            pet.setWalk_history(new ArrayList<DataHistory>());
        } if (pet.getLitter_history() == null) {
            pet.setLitter_history(new ArrayList<DataHistory>());
        } if (pet.getWater_history() == null) {
            pet.setWater_history(new ArrayList<DataHistory>());
        } if (pet.getNext_litter_change() == null) {
            pet.setNext_litter_change("");
        }

        return pet;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNextUp(Pet pet) {
        //TODO: Add case for food time == med time

        TextView nextUpView = root.findViewById(R.id.next_up_view);

        DateTimeFormatter mtime = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime now = LocalTime.parse(LocalDateTime.now().format(mtime));

        LocalTime nextUp = null;
        String nextUpType = "";

        for (String time : pet.getFood_schedule()) {
            LocalTime t = LocalTime.parse(time);
            Log.d("t", t.toString());

            if (t.isAfter(now)) {
                Log.d("time parsing", "isafter");
                if (nextUp == null) {
                    Log.d("time parsing", "first time");
                    nextUp = t;
                    nextUpType = "Food";
                } else if (t.isBefore(nextUp)) {
                    Log.d("time parsing", "time replaced: " + t.toString());
                    nextUp = t;
                    nextUpType = "Food";
                }
            }
        }

        for (String time : pet.getMed_schedule()) {
            LocalTime t = LocalTime.parse(time);
            Log.d("t", t.toString());

            if (t.isAfter(now)) {
                Log.d("time parsing", "isafter");
                if (nextUp == null) {
                    Log.d("time parsing", "first time");
                    nextUp = t;
                    nextUpType = "Medicine";
                } else if (t.isBefore(nextUp)) {
                    Log.d("time parsing", "time replaced: " + t.toString());
                    nextUp = t;
                    nextUpType = "Medicine";
                }
            }
        }

        if (nextUp == null) {
            nextUpView.setText("All tasks completed!");
        } else {
            nextUpView.setText("Next up: "  + nextUpType + " @ " + cleanTime(nextUp.toString()));
        }
    }

    public String cleanTime(String inTime) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAlarm() {
        //TODO: Make this work with pet data

        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        int idCount = 0;

        for (Pet pet : petsArray) {
            idCount += 100;
            ArrayList<String> theseSchedules = pet.getAllScheduleTimes();
            for (String time : theseSchedules) {
                idCount += 1;
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DATE, Calendar.DATE);

                Date date = cal.getTime();

                Long millis = date.getTime();

                Intent intent = new Intent(root.getContext(), AlarmReceiver.class);
                intent.putExtra("message", "One of your pets has a task due!");
                pendingIntent = PendingIntent.getBroadcast(root.getContext(), idCount, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                Log.d("Alarm", "Alarm " + idCount + " created");
            }
        }
    }

    private void createNotificationChannel(){
        // Create the task reminder notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PetPalReminderChannel";
            String description = "Pet Task Reminder Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Pet Task Reminders", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clearAllAlarms() {
        Intent alarmIntent = new Intent(root.getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("message", "One of your pets has a task due!");
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        for (int j = 100; j < 999; j++) {
            PendingIntent displayIntent = PendingIntent.getBroadcast(root.getContext(), j, alarmIntent, 0);
            alarmManager.cancel(displayIntent);
            Log.d("Alarm", "Alarm " + j + " cancelled");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifSetup() {
        //clearAllAlarms();
        createNotificationChannel();
        setAlarm();
    }
}

//TODO: add animations to carousel?
//https://medium.com/holler-developers/paging-image-gallery-with-recyclerview-f059d035b7e7