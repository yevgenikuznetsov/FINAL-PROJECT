package com.example.finalProject.service;

import com.example.finalProject.service.aidFunction.AidFunction;
import com.example.finalProject.object.*;
import com.example.finalProject.service.aidFunction.GetDataFromDB;
import com.example.finalProject.service.interfaces.DataService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.example.finalProject.general.Terms.DATA;
import static com.example.finalProject.general.Terms.EVENT_LIST;
import static com.example.finalProject.general.Terms.GENERAL;
import static com.example.finalProject.general.Terms.GENERAL_INFORMATION;
import static com.example.finalProject.general.Terms.GLOBAL;
import static com.example.finalProject.general.Terms.GLOBAL_DB;
import static com.example.finalProject.general.Terms.HISTORY;
import static com.example.finalProject.general.Terms.LOCAL;
import static com.example.finalProject.general.Terms.PERSONS;

@Service
public class DataServiceImpl implements DataService {
    private final AidFunction aidFunction;
    private final GetDataFromDB getDataFromDB;

    private final int MAX_DEPTH = 2;

    @Autowired
    public DataServiceImpl(AidFunction aidFunction, GetDataFromDB getDataFromDB) {
        this.aidFunction = aidFunction;
        this.getDataFromDB = getDataFromDB;
    }

    // Collect information from url and check if there is suspicious information
    @Override
    public void saveInformation(String url, String location) {
        String webSite = "https://www.000webhost.com/?utm_source=000webhostapp&utm_campaign=000_logo&utm_medium=website&utm_content=footer_img";
        String decodeURL = "";
        Stack<UrlDepth> stack = new Stack<UrlDepth>();

        // Save the check date
        Format f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String str = f.format(new Date());

        try {
            decodeURL = java.net.URLDecoder.decode(url, StandardCharsets.UTF_8.name());
            Document document = Jsoup.connect("https://social-media-demo.000webhostapp.com/").get();

            // Find all Element in url with a attribute
            Elements links = document.select("a[href]");

            // Save new url that was found in stack
            for (Element link : links) {
                if (!link.attr("abs:href").equals(webSite))
                    stack.push(new UrlDepth(link.attr("abs:href"), 1));
            }

            // Use DFS - check each url if it contain more url and save the information that contain in url
            while (!stack.isEmpty()) {
                UrlDepth currentUrl = stack.pop();
                document = Jsoup.connect(currentUrl.getUrl()).get();
                links = document.select("a[href]");

                // Save information in DB
                collectInformation(document, str, location);

                for (Element link : links) {
                    if (!link.attr("abs:href").equals(webSite)) {
                        if (currentUrl.getDepth() + 1 < MAX_DEPTH) {
                            stack.push(new UrlDepth(link.attr("abs:href"), currentUrl.getDepth() + 1));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkInformation(String url) {
        GeneralInformation generalInformation = new GeneralInformation();
        String decodeURL = "";

        History history = new History();
        Format f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        // Save the date and time that this URL was searched
        history.setDate(f.format(new Date()));

        try{
            decodeURL = java.net.URLDecoder.decode(url,StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }

        history.setUrl("https://social-media-demo.000webhostapp.com/");
        history.setStatus(false);

        ApiFuture<QuerySnapshot> querySnapshot = getDataFromDB.getArrayFromDB(DATA);

        // Collect all new information from DB
        try {
            for(DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                generalInformation = aidFunction.checkNewData(doc.toObject(Information.class), generalInformation, LOCAL);
            }

            aidFunction.updateNumOfNewMassages(generalInformation.getNumOfMessages(), generalInformation.getNumOfSuspects(), LOCAL);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (generalInformation.isSuspect()) {
            history.setStatus(true);
        }

        aidFunction.saveHistory(history);

        return generalInformation.isSuspect();
    }

    @Override
    public boolean checkInGlobalDB(String location) {
        ApiFuture<QuerySnapshot> querySnapshot = getDataFromDB.getArrayFromGlobalDB(DATA);
        GeneralInformation generalInformation = new GeneralInformation();

        try {
            for(DocumentSnapshot doc : querySnapshot.get().getDocuments()){
                Information information = doc.toObject(Information.class);

                String[] arrOfLocation = information.getLocation().split(",", 0);

                if(location.equals(arrOfLocation[0])){
                    generalInformation = aidFunction.checkNewData(information, generalInformation, GLOBAL);
                }
            }

            aidFunction.updateNumOfNewMassages(generalInformation.getNumOfGlobalMessages(), generalInformation.getNumOfGlobalSuspects(), GLOBAL);

            saveSearchDateAndTime();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return generalInformation.isSuspect();
    }

    @Override
    public History checkTime() {
        DocumentSnapshot document = getDataFromDB.getObjFromDB(GLOBAL_DB, GENERAL);

        // Check if general information in DB exists
        if(document != null && document.exists()) {
            return document.toObject(History.class);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Information> getAllEvent(){
        ArrayList<Information> allEvent = new ArrayList<>();

        ApiFuture<QuerySnapshot> querySnapshot = getDataFromDB.getArrayFromDB("suspiciousInformation");

        try {
            // Check if event list is not empty
            if(querySnapshot.get().getDocuments().isEmpty()) {
                return null;
            }

            // Get all event list from DB
            for(DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                allEvent.add(doc.toObject(Information.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Collections.reverse(allEvent);

        return allEvent;
    }

    // Get the search history
    @Override
    public ArrayList<History> getAllHistory() {
        ArrayList<History> allHistory = new ArrayList<>();

        ApiFuture<QuerySnapshot> querySnapshot = getDataFromDB.getArrayFromDB(HISTORY);

        // Check if history list in DB is not empty
        try {
            if(querySnapshot.get().getDocuments().isEmpty()) {
                return null;
            }

            // Read all history from DB
            for(DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                allHistory.add(doc.toObject(History.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return allHistory;
    }

    // Get the last suspect messages that was found
    @Override
    public ArrayList<Information> getEventList() {
        ArrayList<Information> allEvent = new ArrayList<>();

        ApiFuture<QuerySnapshot> querySnapshot = getDataFromDB.getArrayFromDB(EVENT_LIST);

        try {
            // Check if event list is not empty
            if(querySnapshot.get().getDocuments().isEmpty()) {
                return null;
            }

            // Get all event list from DB
            for(DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                allEvent.add(doc.toObject(Information.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return allEvent;
    }

    // Get general information about messages and suspicious people
    @Override
    public GeneralInformation getGeneralInformation() {
        DocumentSnapshot document = getDataFromDB.getObjFromDB(GENERAL_INFORMATION, PERSONS);

        // Check if general information in DB exists
        if(document != null && document.exists()) {
            return document.toObject(GeneralInformation.class);

        } else {
            GeneralInformation newGeneralInformation = new GeneralInformation();
            newGeneralInformation.setMonth(new ArrayList<>(Collections.nCopies(12, 0)));

            return newGeneralInformation;
        }
    }

    private void saveNewInformationFromUrlToGlobalDB(Information information) {
        Firestore dbFirebase = FirestoreClient.getFirestore(FirebaseApp.getInstance("global"));
        dbFirebase.collection("data").document(information.getDate()).set(information);
    }

    // Save information that was collected from url
    private void saveNewInformationFromUrlToLocalDB(Information information) {
        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection(DATA).document(information.getDate()).set(information);
    }

    // Collect all the information that appears on the url
    private void collectInformation(Document document, String searchTime, String locationforDB) {
        Date date;
        String stringDate = "";

        Elements element = document.select("div.post");
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        for(Element e : element) {
            String name = e.select("p.personName").text();
            try {
                date = format.parse(e.select("span.date").text());

                Format f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                stringDate = f.format(date);

            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            String location = e.select("span.location").text();
            String information = e.select("p.post").text();

            if(location.contains(locationforDB)){
                saveNewInformationFromUrlToLocalDB(new Information(information, name, stringDate, searchTime, location));
            } else {
                saveNewInformationFromUrlToGlobalDB(new Information(information, name, stringDate, searchTime, location));
            }
        }
    }

    private void saveSearchDateAndTime(){
        Format f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String date = f.format(new Date());

        History global = new History();
        global.setDate(date);

        Firestore dbFirebase = FirestoreClient.getFirestore();
        dbFirebase.collection("general").document("searchInGlobal").set(global);
    }
}
