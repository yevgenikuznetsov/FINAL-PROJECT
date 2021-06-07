package com.example.finalProject.service;

import com.example.finalProject.object.Person;
import com.example.finalProject.object.Remark;
import com.example.finalProject.service.aidFunction.GetDataFromDB;
import com.example.finalProject.service.interfaces.PersonService;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.finalProject.general.Terms.PERSONS;
import static com.example.finalProject.general.Terms.REMARKS;

@Service
public class PersonServiceImpl implements PersonService {
    private final GetDataFromDB getDataFromDB;

    @Autowired
    public PersonServiceImpl(GetDataFromDB getDataFromDB){
        this.getDataFromDB = getDataFromDB;
    }


    // Get all person suspicious messages and location
    @Override
    public Person getPerson(String name) {
        DocumentSnapshot document = getDataFromDB.getObjFromDB(name, PERSONS);

        Person person = null;
        if (document != null && document.exists()) {
            person = document.toObject(Person.class);
        }

        return person;
    }

    // Save new remark in DB
    @Override
    public void SaveRemarkToDb(Remark remark, String name) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(REMARKS).document(name).set(remark);
    }

    // Get all the remarks for the person
    @Override
    public Remark readRemarkFromDB(String name) {
        DocumentSnapshot document = getDataFromDB.getObjFromDB(name, REMARKS);

        Remark remark = null;
        if (document != null && document.exists()) {
            remark = document.toObject(Remark.class);
        }

        return remark;
    }
}
