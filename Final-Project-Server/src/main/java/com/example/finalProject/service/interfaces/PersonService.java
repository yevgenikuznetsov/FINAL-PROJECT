package com.example.finalProject.service.interfaces;

import com.example.finalProject.object.Person;
import com.example.finalProject.object.Remark;

public interface PersonService {

    Person getPerson(String name);

    void SaveRemarkToDb(Remark remark, String name);

    Remark readRemarkFromDB(String name);
}
