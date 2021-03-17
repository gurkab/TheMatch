package com.gurka.theMatch.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Program {

    private String name;
    private int capacity;
    private Map<String,Integer> preferredApplicants = new HashMap<>();
    private Map<String,Integer> acceptedApplicants = new HashMap<>();

}
