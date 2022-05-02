package com.knoxolotl.petpal.ui.auth_fragments;

import static android.content.ContentValues.TAG;

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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentCreateHouseholdBinding;
import com.knoxolotl.petpal.databinding.FragmentHomeBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateHousehold extends Fragment {
    // Fragment to create a new household

    private FragmentCreateHouseholdBinding binding;
    private View root;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreateHouseholdBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());

        TextInputLayout newHouseholdName = binding.newHouseholdName;
        Button createHousehold = binding.createHouseholdRequestButton;

        // Button to create a new household onlick
        createHousehold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = newHouseholdName.getEditText().getText().toString();
                if (newName.isEmpty()) {  // if name is empty
                    Toast.makeText(getContext(), "Please enter a name for your household", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new household
                createHouseholdRequest(newName);

            }
        });

        return root;
    }

    private void createHouseholdRequest(String newName) {
        // Request to create a new household
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usersRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ArrayList<DocumentReference> members = new ArrayList<>();
        members.add(usersRef);

        Map<String, Object> newHousehold = new HashMap<>();
        newHousehold.put("name", newName);
        newHousehold.put("head", usersRef);
        newHousehold.put("members", members);

        DocumentReference newHouseholdReference = db.collection("households").document();

        newHouseholdReference.set(newHousehold)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {  // if successful
                        Log.d(TAG, "New household successfully created!");

                        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                        NavController navController = navHostFragment.getNavController();

                        sp.edit().putString("household", newHouseholdReference.getId().toString()).apply();  // update shared preferences
                        Log.d("HouseholdSelection", "household/" + sp.getString("household", ""));

                        // request update of pets and household
                        MainActivity.requestPetsAndHousehold(db.document("households/" + sp.getString("household", null)));

                        // Navigate to home fragment
                        navController.navigate(R.id.action_createHousehold_to_navigation_home);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error creating new household");
                    }
                });

        // Update user in database
        usersRef.update("households", FieldValue.arrayUnion(newHouseholdReference));
    }
}