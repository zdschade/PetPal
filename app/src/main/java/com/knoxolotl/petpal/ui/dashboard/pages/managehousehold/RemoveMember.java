package com.knoxolotl.petpal.ui.dashboard.pages.managehousehold;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.Household;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.User;
import com.knoxolotl.petpal.databinding.FragmentHouseholdSelectionBinding;
import com.knoxolotl.petpal.databinding.FragmentRemoveMemberBinding;

import java.util.ArrayList;

public class RemoveMember extends Fragment {
    // Fragment for removing a member from the current household

    private FragmentRemoveMemberBinding binding;
    private View root;
    SharedPreferences sp;
    FirebaseFirestore db;

    ArrayList<User> userArray;
    RemoveMemberAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRemoveMemberBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        // Inflate the layout for this fragment

        userArray = new ArrayList<User>();

        // Confirm dialog for removing a member
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(root.getContext())
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        db = FirebaseFirestore.getInstance();

        DocumentReference householdReference = MainActivity.getCurrentHousehold().getReference();

        // Request all members of the household and show in listview
        db.collection("users").whereArrayContains("households", householdReference).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User thisUser = documentSnapshot.toObject(User.class);
                        thisUser.setRef(documentSnapshot.getReference());
                        userArray.add(thisUser);
                    }

                    Log.d("userArray", userArray.toString());

                    ListView listView = binding.removeMemberListview;
                    adapter = new RemoveMemberAdapter(root.getContext(), userArray);
                    listView.setAdapter(adapter);

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                            User memberToRemove = userArray.get(position);

                            // Confirm deletion of member
                            confirmDelete.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    removeMemberRequest(memberToRemove, position);
                                }
                            });

                            confirmDelete.show();

                            return true;
                        }
                    });

                });

        return root;
    }

    private void removeMemberRequest(User member, int position) {
        // Remove member from household
        DocumentReference householdReference = db.document("households/" + sp.getString("household", null));

        member.getRef().update("households", FieldValue.arrayRemove(householdReference));

        householdReference.update("members", FieldValue.arrayRemove(member.getRef()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Member", "DocumentSnapshot successfully removed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Member", "Error removing document", e);
                    }
                });

        // update listview
        userArray.remove(position);
        adapter.notifyDataSetChanged();
    }
}