package com.example.finalProject.service.aidFunction;

import com.example.finalProject.object.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.example.finalProject.general.Terms.DATA;
import static com.example.finalProject.general.Terms.EVENT_LIST;
import static com.example.finalProject.general.Terms.GENERAL_INFORMATION;
import static com.example.finalProject.general.Terms.HISTORY;
import static com.example.finalProject.general.Terms.LOCAL;
import static com.example.finalProject.general.Terms.NUMBER_IN_LIST;
import static com.example.finalProject.general.Terms.NUMBER_OF_EVENT;
import static com.example.finalProject.general.Terms.PERSONS;
import static com.example.finalProject.general.Terms.TOTAL_SUM;

@Service
public class AidFunction {
    private final GetDataFromDB getDataFromDB;

    @Autowired
    public AidFunction(GetDataFromDB getDataFromDB){
        this.getDataFromDB = getDataFromDB;
    }

    public GeneralInformation checkNewData(Information information, GeneralInformation generalInformation, int type) {
        int count;
        boolean wasFound;

        File file = new File("words.txt");
        Scanner reader = null;

        // Check each information that was found with dictionary to find suspicious information
        count = 0;

        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(reader.hasNextLine()) {
            String[] words = reader.nextLine().split("\\s");
            if(contains(information.getInformation(), words[0])) {
                count += Integer.parseInt(words[1]);
            }
        }

        if (count >= TOTAL_SUM) {
            generalInformation.setSuspect(true);
            saveSuspiciousInformation(information);
            wasFound = replaceInformationInDB(information);

            // If suspicious information was found, save it under the same person who uploaded this information
            if(type == LOCAL){
                if(wasFound) {
                    generalInformation.setNumOfSuspects(generalInformation.getNumOfSuspects() + 1);
                }
                generalInformation.setNumOfMessages(generalInformation.getNumOfMessages() + 1);
            } else {
                if(wasFound) {
                    generalInformation.setNumOfGlobalSuspects(generalInformation.getNumOfGlobalSuspects() + 1);
                }
                generalInformation.setNumOfGlobalMessages(generalInformation.getNumOfGlobalMessages() + 1);
            }
        }

        // Delete checked information from general DB
        if(type ==LOCAL){
            deleteInformationFromDB(information.getDate().toString());
        } else {
            deleteInformationFromGlobalDB(information.getDate().toString());
        }

        reader.close();

        return generalInformation;
    }


    // If suspicious information was found. We will forward this information to the person who posted this information
    private boolean replaceInformationInDB(Information info) {
        Person person;
        boolean newPerson;

        // Add to event list to show this information
        addToEventList(info);

        person = checkIfPersonExistInDBAndUpdate(info);

        if(person != null) {
            saveUpdatingPersonInformationInDB(person);
            newPerson = false;
        } else {
            createNewPersonAndSave(info);
            newPerson = true;
        }
        return newPerson;
    }

    private Person checkIfPersonExistInDBAndUpdate(Information info) {
        DocumentSnapshot document = getDataFromDB.getObjFromDB(info.getName(), PERSONS);

        if(document != null && document.exists()) {
            Person person = document.toObject(Person.class);
            Map<String, String> allPersonPost = new HashMap<String, String>();
            List<String> allPersonLocation = new ArrayList<String>();

            // Get all person information from DB and add new information
            person.getAllPersonPost().forEach((k,v) -> allPersonPost.put(k, v));
            allPersonPost.put(info.getDate().toString(), info.getInformation());
            person.setAllPersonPost(allPersonPost);

            person.getLocation().forEach(loc -> allPersonLocation.add(loc));

            boolean isExist = false;

            for(int i = 0 ; i < person.getLocation().size() ; i++ ) {
                if(person.getLocation().get(i).equals(info.getLocation())) {
                    isExist = true;
                }
            }

            if(!isExist) {
                allPersonLocation.add(info.getLocation());
            }

            person.setLocation(allPersonLocation);

            return person;
        }

        return null;
    }

    // If the person is not exist in DB, save it and the information about it
    private void createNewPersonAndSave(Information info) {
        Person person = new Person();
        Map<String, String> post = new HashMap<String, String>();
        List<String> location = new ArrayList<String>();

        post.put(info.getDate().toString(), info.getInformation());
        location.add(info.getLocation());

        person.setName(info.getName());
        person.setAllPersonPost(post);
        person.setLocation(location);

        saveUpdatingPersonInformationInDB(person);
    }

    // Update the general information
    public void updateNumOfNewMassages(int numOfNewMassages, int numOfNewPerson, int type) {
        int previousNumber;
        List<Integer> monthTemp;

        DocumentSnapshot document = getDataFromDB.getObjFromDB(GENERAL_INFORMATION, PERSONS);

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue() - 1;

        if(document != null && document.exists()) {
            GeneralInformation information = document.toObject(GeneralInformation.class);

            if(type == LOCAL){
                information.setNumOfMessages(numOfNewMassages + information.getNumOfMessages());
                information.setNumOfSuspects(numOfNewPerson + information.getNumOfSuspects());
            } else {
                information.setNumOfGlobalMessages(numOfNewMassages + information.getNumOfGlobalMessages());
                information.setNumOfGlobalSuspects(numOfNewPerson + information.getNumOfGlobalSuspects());
            }

            monthTemp = information.getMonth();
            previousNumber = monthTemp.get(month);
            monthTemp.set(month, previousNumber + numOfNewMassages);

            information.setMonth(monthTemp);

            updateGeneralInformation(information);
        } else {
            // If general information not exist create new one
            GeneralInformation newGeneralInformation = new GeneralInformation();
            if(type == LOCAL){
                newGeneralInformation.setNumOfMessages(numOfNewMassages);
                newGeneralInformation.setNumOfSuspects(numOfNewPerson);
            } else {
                newGeneralInformation.setNumOfGlobalMessages(numOfNewMassages);
                newGeneralInformation.setNumOfGlobalSuspects(numOfNewPerson);
            }

            monthTemp = new ArrayList<Integer>();

            for(int i = 0 ; i < 12 ; i++) {
                monthTemp.add(0);
            }

            monthTemp.set(month, numOfNewMassages);

            newGeneralInformation.setMonth(monthTemp);
            updateGeneralInformation(newGeneralInformation);
        }
    }

    // Add new suspect information to DB
    private void addToEventList(Information info) {
        ApiFuture<QuerySnapshot> querySnapshot = getDataFromDB.getArrayFromDB(EVENT_LIST);

        try {
            if(querySnapshot.get().getDocuments().isEmpty()) {
                saveInfoToEventList(0, info);
            } else {
                DocumentSnapshot document = getDataFromDB.getObjFromDB("number", NUMBER_IN_LIST);

                if(document != null && document.exists()) {
                    NumberInList number = document.toObject(NumberInList.class);

                    if(number != null && number.getNumber() <= NUMBER_OF_EVENT) {
                        saveInfoToEventList(number.getNumber(),info);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
        }
    }

    private void saveInfoToEventList(int number, Information info) {
        NumberInList nextNumber = new NumberInList();

        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(EVENT_LIST).document("" + number).set(info);

        if(number == 0) {
            nextNumber.setNumber(1);
            dbFirebase.collection(NUMBER_IN_LIST).document("number").set(nextNumber);
        } else {
            number++;

            if(number == (NUMBER_OF_EVENT + 1)) {
                nextNumber.setNumber(0);
            } else {
                nextNumber.setNumber(number);
            }

            dbFirebase.collection(NUMBER_IN_LIST).document("number").set(nextNumber);
        }
    }

    // Save the person's update information
    private void saveUpdatingPersonInformationInDB(Person person) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(PERSONS).document(person.getName()).set(person);
    }

    // Delete the information from general place in DB
    private void deleteInformationFromDB(String date) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(DATA).document(date).delete();
    }

    // Delete the information from general place in DB
    private void deleteInformationFromGlobalDB(String date) {
        Firestore dbFirebase = FirestoreClient.getFirestore(FirebaseApp.getInstance("global"));
        dbFirebase.collection(DATA).document(date).delete();
    }

    // Save General in DB
    private void updateGeneralInformation(GeneralInformation generalInformation) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(PERSONS).document(GENERAL_INFORMATION).set(generalInformation);
    }

    private void saveSuspiciousInformation(Information information) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection("suspiciousInformation").document(information.getDate()).set(information);

        deleteInformationFromDB(information.getDate().toString());
    }

    // Save updated history in DB
    public void saveHistory(History history) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(HISTORY).document(history.getDate().toString()).set(history);
    }

    private boolean contains(String sequence, String word)
    {
        return sequence.indexOf(word) > -1;
    }
}
