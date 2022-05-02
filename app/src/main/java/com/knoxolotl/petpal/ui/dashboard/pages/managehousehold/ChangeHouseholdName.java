package com.knoxolotl.petpal.ui.dashboard.pages.managehousehold;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.Household;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentChangeHouseholdNameBinding;
import com.knoxolotl.petpal.databinding.FragmentHouseholdSelectionBinding;


public class ChangeHouseholdName extends Fragment {
    // Fragment for chaning the name of the currently selected household

    private FragmentChangeHouseholdNameBinding binding;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChangeHouseholdNameBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Household currentHousehold = MainActivity.getCurrentHousehold();

        binding.householdNameField.getEditText().setText(currentHousehold.getName());

        Button updateButton = binding.updateHouseholdName;

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = binding.householdNameField.getEditText().getText().toString();
                MainActivity.getCurrentHousehold().setName(newName);

                db.collection("households").document(MainActivity.getCurrentHousehold().getReference().getId())
                    .update("name", newName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Household name updated", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return root;
    }
}