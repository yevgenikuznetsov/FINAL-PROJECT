package com.example.finalProject.service.aidFunction;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class GetDataFromDB {
    public DocumentSnapshot getObjFromDB(String string, String collection){
        DocumentReference documentReference = FirestoreClient.getFirestore().collection(collection).document(string);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = null;

        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return document;
    }

    public ApiFuture<QuerySnapshot> getArrayFromDB(String string){
        CollectionReference allData = FirestoreClient.getFirestore().collection(string);
        return allData.get();
    }

    public ApiFuture<QuerySnapshot> getArrayFromGlobalDB(String string){
        Firestore db = FirestoreClient.getFirestore(FirebaseApp.getInstance("global"));
        CollectionReference allData = db.collection(string);
        return allData.get();
    }
}
