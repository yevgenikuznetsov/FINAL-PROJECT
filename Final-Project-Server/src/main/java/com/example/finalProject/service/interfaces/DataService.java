package com.example.finalProject.service.interfaces;

import com.example.finalProject.object.GeneralInformation;
import com.example.finalProject.object.History;
import com.example.finalProject.object.Information;

import java.util.ArrayList;

public interface DataService {

    void saveInformation(String url, String location);

    boolean checkInformation(String url);

    boolean checkInGlobalDB(String location);

    History checkTime();

    ArrayList<Information> getAllEvent();

    ArrayList<History> getAllHistory();

    ArrayList<Information> getEventList();

    GeneralInformation getGeneralInformation();
}
