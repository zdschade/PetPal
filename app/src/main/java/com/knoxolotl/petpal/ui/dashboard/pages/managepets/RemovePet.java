package com.knoxolotl.petpal.ui.dashboard.pages.managepets;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.DataHistory;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.databinding.FragmentRemovePetBinding;

import java.util.ArrayList;

public class RemovePet extends Fragment {
    // Fragment to remove a pet from the household

    private FragmentRemovePetBinding binding;
    private View root;
    ArrayList<Pet> petsArray;
    PetListViewAdapter adapter;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRemovePetBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ArrayList<DocumentReference> householdRefs = new ArrayList<>();

        //petsArray = new ArrayList<>();
        petsArray = MainActivity.getPetsArray();

        ListView petListview = binding.removePetsListview;

        // Dialog to confirm removal of pet
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(root.getContext())
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        adapter = new PetListViewAdapter(getContext(), petsArray);
        petListview.setAdapter(adapter);

        // Long click required to avoid accidental deletion
        petListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Pet petToRemove = petsArray.get(position);

                // On positive button click, remove pet from household
                confirmDelete.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removePetRequest(petToRemove, position);
                    }
                });

                confirmDelete.show();

                return true;
            }
        });


        return root;
    }

    private void removePetRequest(Pet pet, int position) {
        // Request to remove pet from household
        db.collection("pets").document(pet.getReference().getId()).delete();
        petsArray.remove(position);
        adapter.notifyDataSetChanged();

        MainActivity.getCurrentHousehold().getReference().update("pets", FieldValue.arrayRemove(pet.getReference()))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Household", "DocumentSnapshot successfully removed!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Household", "Error removing document", e);
                }
            });
    }

}