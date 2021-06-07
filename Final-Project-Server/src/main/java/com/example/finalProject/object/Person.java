package com.example.finalProject.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String name;

    private Map<String, String> allPersonPost;
    private List<String> location;
}
