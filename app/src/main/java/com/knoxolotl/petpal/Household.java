package com.knoxolotl.petpal;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Household {

    private DocumentReference reference;
    private DocumentReference head;
    private ArrayList<DocumentReference> members;
    private ArrayList<DocumentReference> petsRefs;
    private ArrayList<Pet> pets;
    private String name;

    public Household(DocumentReference head, ArrayList<DocumentReference> members, ArrayList<DocumentReference> petsRefs, String name) {
        this.head = head;
        this.members = members;
        this.petsRefs = petsRefs;
        this.name = name;
    }

    public Household() {
    }

    public DocumentReference getHead() {
        return head;
    }

    public void setHead(DocumentReference head) {
        this.head = head;
    }

    public ArrayList<DocumentReference> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<DocumentReference> members) {
        this.members = members;
    }

    public ArrayList<DocumentReference> getPetsRefs() {
        return petsRefs;
    }

    public void setPetsRefs(ArrayList<DocumentReference> petsRefs) {
        this.petsRefs = petsRefs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Household{" +
                "reference=" + reference +
                ", head=" + head +
                ", members=" + members +
                ", petsRefs=" + petsRefs +
                ", pets=" + pets +
                ", name='" + name + '\'' +
                '}';
    }
}
