package com.knoxolotl.petpal.ui.dashboard.pages.managehousehold;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.databinding.FragmentAddMemberBinding;


public class AddMember extends Fragment {
    // Fragment for adding a member to your currently household

    private FragmentAddMemberBinding binding;
    private View root;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //TODO: Add requesting feature

        binding = FragmentAddMemberBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        TextInputLayout newMemberEmailField = binding.newMemberEmailField;
        Button addMemberButton = binding.addMemberRequestButton;

        db = FirebaseFirestore.getInstance();
        
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").whereEqualTo("email", newMemberEmailField.getEditText().getText().toString()).limit(1).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                newMemberEmailField.setError("User does not exist");
                            }
                            else {
                                DocumentReference newMemberRef = db.collection("users").document(queryDocumentSnapshots.getDocuments().get(0).getId());
                                MainActivity.getCurrentHousehold().getReference().update("members", FieldValue.arrayUnion(newMemberRef));
                                newMemberRef.update("households", FieldValue.arrayUnion(MainActivity.getCurrentHousehold().getReference()));
                            }
                        });
            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}