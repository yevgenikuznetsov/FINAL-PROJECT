package com.example.finalProject.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralInformation {
    private int numOfMessages;
    private int numOfSuspects;
    private int numOfGlobalMessages;
    private int numOfGlobalSuspects;
    private boolean suspect;

    private List<Integer> month;
}
