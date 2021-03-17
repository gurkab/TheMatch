package com.gurka.theMatch.data;

import lombok.Data;

import java.util.*;

@Data
public class Applicant {

    private String name;
    private Map<Integer,String> preferredPrograms = new HashMap<>();
    private Integer currentMatchedProgram;

    public Applicant() {
        currentMatchedProgram=1;
    }
}
