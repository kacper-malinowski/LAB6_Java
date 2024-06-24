package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        //Chosen in the desktop app
        String sourceDirectory = "D:\\Studia\\BB\\sheets_backup\\";
        String targetDirectory = "D:\\Studia\\BB\\sheets_instruments\\";
        int numThreads = Runtime.getRuntime().availableProcessors();

        FileOperations fileOperations = new FileOperations();

        fileOperations.findDirectories(sourceDirectory);
        String chosenDirectory = fileOperations.getDirectoryPathList().get(2);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (String directory : fileOperations.getDirectoryPathList()) {
            //executor.submit(fileOperations.sortFilesTask(directory, targetDirectory, logText));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}