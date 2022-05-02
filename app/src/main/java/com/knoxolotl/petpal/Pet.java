package com.knoxolotl.petpal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;

public class Pet implements Serializable {
    private DocumentReference reference;
    private String name;
    private String species;
    private DocumentReference household;
    private ArrayList<String> food_schedule;
    private ArrayList<String> med_schedule;
    private ArrayList<DataHistory> food_history;
    private ArrayList<DataHistory> med_history;
    private ArrayList<DataHistory> water_history;
    private String next_vet_visit;

    private ArrayList<DataHistory> walk_history;

    private String next_litter_change;
    private ArrayList<DataHistory> litter_history;

    public Pet(DocumentReference reference, String name, String species, DocumentReference household, ArrayList<String> food_schedule, ArrayList<String> med_schedule, ArrayList<DataHistory> food_history, ArrayList<DataHistory> med_history, ArrayList<DataHistory> water_history, String next_vet_visit) {
        this.reference = reference;
        this.name = name;
        this.species = species;
        this.household = household;
        this.food_schedule = food_schedule;
        this.med_schedule = med_schedule;
        this.food_history = food_history;
        this.med_history = med_history;
        this.water_history = water_history;
        this.next_vet_visit = next_vet_visit;
    }

    // dog constructor
    public Pet(DocumentReference reference, String name, String species, DocumentReference household, ArrayList<String> food_schedule, ArrayList<String> med_schedule, ArrayList<DataHistory> food_history, ArrayList<DataHistory> med_history, ArrayList<DataHistory> water_history, String next_vet_visit, ArrayList<DataHistory> walk_history) {
        this.reference = reference;
        this.name = name;
        this.species = species;
        this.household = household;
        this.food_schedule = food_schedule;
        this.med_schedule = med_schedule;
        this.food_history = food_history;
        this.med_history = med_history;
        this.water_history = water_history;
        this.next_vet_visit = next_vet_visit;
        this.walk_history = walk_history;
    }

    // cat constructor
    public Pet(DocumentReference reference, String name, String species, DocumentReference household, ArrayList<String> food_schedule, ArrayList<String> med_schedule, ArrayList<DataHistory> food_history, ArrayList<DataHistory> med_history, ArrayList<DataHistory> water_history, String next_vet_visit, String next_litter_change, ArrayList<DataHistory> litter_history) {
        this.reference = reference;
        this.name = name;
        this.species = species;
        this.household = household;
        this.food_schedule = food_schedule;
        this.med_schedule = med_schedule;
        this.food_history = food_history;
        this.med_history = med_history;
        this.water_history = water_history;
        this.next_vet_visit = next_vet_visit;
        this.next_litter_change = next_litter_change;
        this.litter_history = litter_history;
    }

    public Pet() {

    }

    public DocumentReference getReference() {
        return reference;
    }

    /*public String getPetIDString() {
        String[] splitPetID = this.reference.split("\'");
        return splitPetID[1];
    }*/

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public DocumentReference getHousehold() {
        return household;
    }

    /*public String getHouseholdIDString() {
        String[] splitHouseholdID = this.household.split("\'");
        return splitHouseholdID[1];
    }*/

    public void setHousehold(DocumentReference household) {
        this.household = household;
    }

    public ArrayList<String> getFood_schedule() {
        return food_schedule;
    }

    public void setFood_schedule(ArrayList<String> food_schedule) {
        this.food_schedule = food_schedule;
    }

    public ArrayList<String> getMed_schedule() {
        return med_schedule;
    }

    public void setMed_schedule(ArrayList<String> med_schedule) {
        this.med_schedule = med_schedule;
    }

    public ArrayList<DataHistory> getFood_history() {
        return food_history;
    }

    public void setFood_history(ArrayList<DataHistory> food_history) {
        this.food_history = food_history;
    }

    public ArrayList<DataHistory> getMed_history() {
        return med_history;
    }

    public void setMed_history(ArrayList<DataHistory> med_history) {
        this.med_history = med_history;
    }

    public ArrayList<DataHistory> getWater_history() {
        return water_history;
    }

    public void setWater_history(ArrayList<DataHistory> water_history) {
        this.water_history = water_history;
    }

    public ArrayList<String> getDaysItemHistory(String item, String date) {
        ArrayList<String> daysItemHistory = new ArrayList<>();
        ArrayList<DataHistory> itemToCheck = new ArrayList<>();

        switch (item) {
            case "food":
                itemToCheck = this.getFood_history();
                break;
            case "med":
                itemToCheck = this.getMed_history();
                break;
            case "water":
                itemToCheck = this.getWater_history();
                break;
            case "litter":
                itemToCheck = this.getLitter_history();
                break;
            case "walk":
                itemToCheck = this.getWalk_history();
        }

        for (DataHistory thisHistory : itemToCheck) {
            try {
                if (thisHistory.getLog_time().equals(date)) {
                    daysItemHistory.add(thisHistory.getItem_time());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return daysItemHistory;
    }


    public String getNext_vet_visit() {
        return next_vet_visit;
    }

    public void setNext_vet_visit(String next_vet_visit) {
        this.next_vet_visit = next_vet_visit;
    }

    public ArrayList<DataHistory> getWalk_history() {
        return walk_history;
    }

    public void setWalk_history(ArrayList<DataHistory> walk_history) {
        this.walk_history = walk_history;
    }

    public String getNext_litter_change() {
        return next_litter_change;
    }

    public void setNext_litter_change(String next_litter_change) {
        this.next_litter_change = next_litter_change;
    }

    public ArrayList<DataHistory> getLitter_history() {
        return litter_history;
    }

    public void setLitter_history(ArrayList<DataHistory> litter_history) {
        this.litter_history = litter_history;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "_id='" + reference + '\'' +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", household='" + household + '\'' +
                ", food_schedule=" + food_schedule +
                ", med_schedule=" + med_schedule +
                ", food_history=" + food_history +
                ", med_history=" + med_history +
                ", water_history=" + water_history +
                ", next_vet_visit='" + next_vet_visit + '\'' +
                ", walkHistory=" + walk_history +
                ", next_litter_change='" + next_litter_change + '\'' +
                ", litter_history=" + litter_history +
                '}';
    }

    public ArrayList<String> getNonEmptyValues() {
        ArrayList<String> nonEmptyValues = new ArrayList<>();
        if (!this.food_schedule.isEmpty()) {
            nonEmptyValues.add("food");
        }
        if (!this.med_schedule.isEmpty()) {
            nonEmptyValues.add("med");
        }
        if (!this.next_vet_visit.equals("")) {
            nonEmptyValues.add("next vet visit");
        }
        /*if (this.species.toLowerCase().equals("dog") && !this.getWalk_history().isEmpty()) {
            nonEmptyValues.add("walk");
        }*/
        return nonEmptyValues;
    }

    public ArrayList<DataHistory> getAllDataHistory() {
        ArrayList<DataHistory> allDataHistory = new ArrayList<>();
        allDataHistory.addAll(this.getFood_history());
        allDataHistory.addAll(this.getMed_history());
        allDataHistory.addAll(this.getWater_history());
        allDataHistory.addAll(this.getLitter_history());
        allDataHistory.addAll(this.getWalk_history());
        return allDataHistory;
    }

    public ArrayList<String> getAllScheduleTimes() {
        ArrayList<String> allScheduleTimes = new ArrayList<>();
        allScheduleTimes.addAll(this.getFood_schedule());
        allScheduleTimes.addAll(this.getMed_schedule());
        return allScheduleTimes;
    }

}
