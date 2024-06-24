package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileOperations {
    private List<String> directoryPathList = new ArrayList<String>();

    private void configureLogging() {
        Logger.getLogger("org.apache.pdfbox").setLevel(Level.SEVERE);
    }

    public List<String> getDirectoryPathList(){
        return directoryPathList.stream().toList();
    }

    public void findDirectories(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        configureLogging();
        //directoryPathList.add(directory);
        if (listOfFiles == null) {
            System.out.println("No files found");
            return;
        }

        boolean containsPdf = false;
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                containsPdf = true;
                break;
            }
        }

        if (containsPdf) {
            directoryPathList.add(folder.getAbsolutePath());
        }

        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                findDirectories(file.getAbsolutePath());
            }
        }
    }



    public Callable<Void> sortFilesTask(String directory, String targetDirectory, JTextArea logText) {
        return () -> {
            sortFiles(directory, targetDirectory, logText);
            return null;
        };
    }

    public void sortFiles(String directory, String targetDirectory, JTextArea logText) {
        InstrumentDatabase instrumentDatabase = new InstrumentDatabase();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            System.out.println("No files found");
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {

                try {

                    String instrument = instrumentDatabase.findInstrument(file.getName().toLowerCase());
                    Path targetPath = Path.of(targetDirectory + File.separator  + instrument + File.separator + file.getName()).toAbsolutePath();
                    if(instrument != "unknown" && !Files.exists(targetPath)) {
                        Files.copy(file.toPath(), targetPath);
                        SwingUtilities.invokeLater(() -> logText.append("Fast Processing: " + file.getAbsolutePath() + " finished!\n"));
                        continue;
                    }
                    PDDocument document = PDDocument.load(file);
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setEndPage(1);
                    String pdfText = stripper.getText(document);
                    document.close();

                    String[] lines = pdfText.split("\n");
                    StringBuilder reversePDFText = new StringBuilder();
                    for (int i = lines.length - 1; i >= 0; i--) {
                        reversePDFText.append(lines[i]).append("\n");
                    }
                    instrument = instrumentDatabase.findInstrument(reversePDFText.toString().toLowerCase());
                    targetPath = Path.of(targetDirectory + File.separator  + instrument + File.separator + file.getName()).toAbsolutePath();
                    //System.out.println("Znalazłem: " + instrument + " w " + file.getName());
                    if (!Files.exists(targetPath)) {

                        //System.out.println("Kopiuję plik " + file.toPath() + " do " + targetPath);
                        Files.copy(file.toPath(), targetPath);
                        SwingUtilities.invokeLater(() -> logText.append("Processing: " + file.getAbsolutePath() + " finished!\n"));
                    } else {
                        //System.out.println("Plik już istnieje w docelowym folderze");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void CreateDirectories(String targetDirectory){
        InstrumentDatabase instrumentDatabase = new InstrumentDatabase();

        for (Map.Entry<String, String[]> instrument : instrumentDatabase.instrumentKeywords.entrySet()){
            File instrumentFolder = new File(targetDirectory + File.separator + instrument.getKey());
            if (!Files.exists(instrumentFolder.toPath().toAbsolutePath())) {
                try {
                    System.out.println("Creating directory: " + instrumentFolder.toPath());
                    Files.createDirectories(instrumentFolder.toPath());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File instrumentFolder = new File(targetDirectory + File.separator + "unknown");
        if (!Files.exists(instrumentFolder.toPath().toAbsolutePath())) {
            try {
                System.out.println("Creating directory: " + "unknown");
                Files.createDirectories(instrumentFolder.toPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

