package com.knoxolotl.petpal;

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
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.knoxolotl.petpal.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<DocumentReference> usersHouseholdsRefs = null;
    private ActivityMainBinding binding;
    public static ArrayList<Pet> petsArray = new ArrayList<>();
    public static ArrayList<Household> usersHouseholds;
    SharedPreferences sp;
    FirebaseFirestore db;
    Boolean firstRun = true;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public static User currentUser;
    public static Household currentHousehold;

    public static BottomNavigationView navView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());


        db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        DocumentReference usersRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("users").document(usersRef.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            User thisUser = task.getResult().toObject(User.class);
                            thisUser.setRef(task.getResult().getReference());
                            applyUserSettings(thisUser);

                            currentUser = thisUser;

                            usersHouseholds = new ArrayList<>();


                            //task.getResult().get("households");

                            if (thisUser.getHouseholds() != null  && !thisUser.getHouseholds().isEmpty()) {
                                usersHouseholdsRefs = (thisUser.getHouseholds());

                                for (DocumentReference householdRef : usersHouseholdsRefs) {
                                    Log.d("MainActivity", "householdRef: " + householdRef.getId());
                                    db.collection("households").document(householdRef.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Household thisHousehold = task.getResult().toObject(Household.class);
                                                        thisHousehold.setReference(task.getResult().getReference());
                                                        usersHouseholds.add(thisHousehold);
                                                        if (usersHouseholds.size() == usersHouseholdsRefs.size()) {
                                                            Log.d("MainActivity", "usersHouseholds: " + usersHouseholds.toString());

                                                            initActivity();
                                                        }
                                                    }
                                                }
                                            });
                                }
                            } else {
                                initActivity();
                            }

                        } else {
                            Log.d("User", "Error getting documents: ", task.getException());
                        }
                    }
                });
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

    public static ArrayList<Pet> getPetsArray() {
        return petsArray;
    }

    public static Household getCurrentHousehold() {
        return currentHousehold;
    }

    public static void setCurrentHousehold(Household household) {
        currentHousehold = household;
    }

    private void initActivity() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        navView.setVisibility(View.GONE);

        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public static ArrayList<DocumentReference> getUsersHouseholdsRefs() {return usersHouseholdsRefs; }

    public static ArrayList<Household> getUsersHouseholds() {
        return usersHouseholds;
    }

    public static void requestPetsAndHousehold(DocumentReference householdReference) {
        Log.d("MainActivity", "Requesting pets, " + householdReference.toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pets").whereEqualTo("household", householdReference)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("PetListener", "Listen failed.", error);
                            return;
                        }

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
                        Log.d("MainActivity", "Pets: " + petsArray.toString());
                    }
                });

        db.collection("households").document(householdReference.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        currentHousehold = task.getResult().toObject(Household.class);
                        currentHousehold.setReference(task.getResult().getReference());
                        Log.d("MainActivity", "Got household: " + currentHousehold.toString());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifSetup() {
        createNotificationChannel();
        setAlarm();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAlarm() {
        //TODO: Make this work with pet data

        clearAllAlarms();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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

                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("message", "One of your pets has a task due!");
                pendingIntent = PendingIntent.getBroadcast(this, idCount, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
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

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void applyUserSettings(User user) {
        if (user.getChip()) {
            sp.edit().putBoolean("chip", true).apply();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    private void clearAllAlarms() {
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent.putExtra("message", "One of your pets has a task due!");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for (int j = 100; j < 999; j++) {
            PendingIntent displayIntent = PendingIntent.getBroadcast(getApplicationContext(), j, alarmIntent, 0);
            alarmManager.cancel(displayIntent);
        }
    }
}