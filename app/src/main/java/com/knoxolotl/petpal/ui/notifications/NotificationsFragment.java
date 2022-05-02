package com.knoxolotl.petpal.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.knoxolotl.petpal.DataHistory;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsFragment extends Fragment {
    // Fragment to show log of all data history for all pets in current household

    private FragmentNotificationsBinding binding;
    SharedPreferences sp;

    ArrayList<HashMap<String, Object>> dataList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());

        // Set up data to pass to the adapter

        dataList = new ArrayList<>();
        ArrayList<Pet> pets = MainActivity.getPetsArray();

        for (Pet pet : pets) {
            for (DataHistory data : pet.getAllDataHistory()) {
                HashMap<String, Object> thisData = new HashMap<>();
                thisData.put("pet_name", pet.getName());
                thisData.put("type", data.getType());
                thisData.put("username", data.getUsername());
                thisData.put("item_time", data.getItem_time());
                thisData.put("log_time", data.getLog_time());

                Log.d("This data", thisData.toString());

                dataList.add(thisData);
            }
        }

        ListView listView = binding.logListview;
        LogAdapter adapter = new LogAdapter(root.getContext(), dataList);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}