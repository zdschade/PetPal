package com.knoxolotl.petpal.ui.dashboard.pages.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentDashboardBinding;
import com.knoxolotl.petpal.databinding.FragmentSettingsPageBinding;

public class SettingsPage extends Fragment {
    private FragmentSettingsPageBinding binding;
    View root;
    SharedPreferences sp;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsPageBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        db = FirebaseFirestore.getInstance();

        CheckBox chipCheckbox = binding.chipCheckbox;
        if (sp.getBoolean("chip", false)) {
            chipCheckbox.setChecked(true);
        }

        chipCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean("chip", true).apply();
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("chip", true);
            } else {
                sp.edit().putBoolean("chip", false).apply();
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("chip", false);
            }
            Toast.makeText(root.getContext(), "Changes will be applied on restart", Toast.LENGTH_SHORT).show();
        });


        //Log.d("args recieved", getArguments().getString("settings item"));


        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_setting_page, container, false);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}