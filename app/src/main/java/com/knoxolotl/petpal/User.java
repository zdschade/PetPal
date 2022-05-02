package com.knoxolotl.petpal;

import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class User {

    String email;
    DocumentReference ref;
    Boolean chip;
    ArrayList<DocumentReference> households;

    public User(String email, DocumentReference ref) {
        this.email = email;
        this.ref = ref;
    }

    public User(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DocumentReference getRef() {
        return ref;
    }

    public void setRef(DocumentReference ref) {
        this.ref = ref;
    }

    public Boolean getChip() {
        return chip;
    }

    public void setChip(Boolean chip) {
        this.chip = chip;
    }

    public ArrayList<DocumentReference> getHouseholds() {
        return households;
    }

    public void setHouseholds(ArrayList<DocumentReference> households) {
        this.households = households;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", ref=" + ref +
                ", chip=" + chip +
                ", households=" + households +
                '}';
    }
}
