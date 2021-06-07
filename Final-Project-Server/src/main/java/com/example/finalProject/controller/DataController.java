package com.example.finalProject.controller;

import com.example.finalProject.object.GeneralInformation;
import com.example.finalProject.object.History;
import com.example.finalProject.object.Information;
import com.example.finalProject.service.interfaces.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class DataController {
    private final DataService collectDataService;

    @Autowired
    public DataController(DataService collectDataService) {
        this.collectDataService = collectDataService;
    }

    // Collect the information from the url
    @GetMapping("/collectInformation")
    public void postInformation(@RequestParam("url") String url, @RequestParam("location") String location) {
        collectDataService.saveInformation(url, location);
    }

    // Check information if there is any suspicious information
    @GetMapping("/checkInformation")
    public boolean checkInformation(@RequestParam("url") String url) {
        return collectDataService.checkInformation(url);
    }

    // Get the last suspicious information to event list table
    @GetMapping("/getEventList")
    public ArrayList<Information> getEventList() {
        return collectDataService.getEventList();
    }

    // Get all search history
    @GetMapping("/getHistoryList")
    public ArrayList<History> gethistorytList() {
        return collectDataService.getAllHistory();
    }

    // Get general information about the suspicious persons and messages
    @GetMapping("/getMoreInformation")
    public GeneralInformation getMoreInformation() {
        return collectDataService.getGeneralInformation();
    }

    // Check suspicious information in Global DB
    @GetMapping("/checkDataInGlobal/location={location}")
    public boolean checkDataInGlobal(@PathVariable("location") String location) {
        return collectDataService.checkInGlobalDB(location); }

    // Check the last time that we search in global DB
    @GetMapping("/getSearchTimeInGlobal")
    public History checkTime(){
        return collectDataService.checkTime();
    }

    // Get all event
    @GetMapping("/getAllEvent")
    public  ArrayList<Information> getAllEvent() { return collectDataService.getAllEvent();}
}
