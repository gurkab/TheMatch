package com.gurka.theMatch;

import com.gurka.theMatch.data.Applicant;
import com.gurka.theMatch.data.Program;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MatchProcess {

    Map<String, Program> programMap = new HashMap<>();
    List<Applicant> applicantList = new ArrayList<>();
    Queue<Applicant> applicantQueue = new LinkedList<>();
    List<Applicant> didNotMatch = new ArrayList<>();

    public void run() {

        for (File file : new File("src/main/resources/Programs").listFiles()) {
            buildPrograms(file);
        }

        for (File file : new File("src/main/resources/Applicants").listFiles()) {
            buildApplicants(file);
        }

        System.out.println("************************************************************");
        System.out.println("Let's start The Match");
        System.out.println("************************************************************");

        while (!applicantQueue.isEmpty()) {
            Applicant currentApplicant = applicantQueue.peek();
            String currentProgramName = currentApplicant.getPreferredPrograms().get(currentApplicant.getCurrentMatchedProgram());

            System.out.println("Matching applicant: " + currentApplicant.getName());

            if (currentApplicant.getCurrentMatchedProgram() > currentApplicant.getPreferredPrograms().size()) {
                System.out.println("Applicant " + currentApplicant.getName() + " has reached the bottom of their rank list without matching. Unfortunately they have not matched. Better luck next year!!");
                applicantQueue.remove();
                didNotMatch.add(currentApplicant);
                System.out.println("************************************************************");
                continue;
            }

            if(match(currentApplicant,currentProgramName)) {
                applicantQueue.remove();
                System.out.println("************************************************************");
                continue;
            }

            currentApplicant.setCurrentMatchedProgram(currentApplicant.getCurrentMatchedProgram()+1);
            System.out.println("************************************************************");
        }

        System.out.println("************************************************************");
        System.out.println("Match is complete!!! Let's see the results...");
        System.out.println("************************************************************");

        for (Map.Entry<String,Program> entry : programMap.entrySet()) {
            System.out.println(entry.getKey() + " matched the following applicants:");
            for (Map.Entry<String,Integer> entry1 : entry.getValue().getAcceptedApplicants().entrySet()) {
                System.out.println(entry1.getKey());
            }
            System.out.println("************************************************************");
        }

        System.out.println("Below applicants unfortunately did not match...");

        for (Applicant applicant: didNotMatch) {
            System.out.println(applicant.getName());
        }

    }

    private void buildPrograms(File file) {
        Program program = new Program();
        program.setName(FilenameUtils.getBaseName(file.getName()));
        int rankCount = 1;

        try {
            Scanner scanner = new Scanner(file);
            program.setCapacity(Integer.parseInt(scanner.nextLine()));
            while (scanner.hasNext()) {
                program.getPreferredApplicants().put(scanner.nextLine(),rankCount);
                rankCount++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!");
            System.exit(1);
        }

        programMap.put(program.getName(),program);
    }

    private void buildApplicants(File file) {
        Applicant applicant = new Applicant();
        applicant.setName(FilenameUtils.getBaseName(file.getName()));
        int rankCount = 1;

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                applicant.getPreferredPrograms().put(rankCount,scanner.nextLine());
                rankCount++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!");
            System.exit(1);
        }

        applicantList.add(applicant);
        applicantQueue.add(applicant);
    }

    private boolean match(Applicant applicant, String programName) {
        Program program = programMap.get(programName);
        Integer rank = program.getPreferredApplicants().get(applicant.getName());

        System.out.println("Checking to see if " + applicant.getName() + " can match at " + programName);

        if (rank != null) {
            System.out.println(programName + " has ranked " + applicant.getName() + ": " + rank);

            if (program.getAcceptedApplicants().size() < program.getCapacity()) {
                System.out.println(programName + " has not reached their resident capacity of " + program.getCapacity() + ". " + applicant.getName() + " will match at " + programName + "! For now...");
                program.getAcceptedApplicants().put(applicant.getName(), rank);

                return true;
            } else {
                System.out.println(programName + " has reached their resident capacity of " + program.getCapacity() + ". Let's see if we can kick anyone out!");

                String rankedLast = null;
                Integer lastRanking = Integer.MIN_VALUE;

                for (Map.Entry<String, Integer> entry : program.getAcceptedApplicants().entrySet()) {
                    if (entry.getValue() > lastRanking) {
                        lastRanking = entry.getValue();
                        rankedLast = entry.getKey();
                    }
                }

                if (rank < lastRanking) {
                    System.out.println(applicant.getName() + " was ranked higher by " + programName + " than the least ranked accepted applicant!");
                    System.out.println(rankedLast + " will be kicked out of " + programName + " and will have to re-enter the match");
                    program.getAcceptedApplicants().remove(rankedLast);
                    program.getAcceptedApplicants().put(applicant.getName(), rank);

                    for (Applicant applicant1 : applicantList) {
                        if (applicant1.getName().equals(rankedLast)) {
                            applicantQueue.add(applicant1);
                            applicant1.setCurrentMatchedProgram(applicant1.getCurrentMatchedProgram()+1);
                        }
                    }

                    return true;
                } else {
                    System.out.println(applicant.getName() + " was NOT ranked higher than any of the other accepted applicants at " + programName);

                    return false;
                }
            }
        } else {
            System.out.println(programName + " has NOT ranked " + applicant.getName());

            return false;
        }
    }
}
