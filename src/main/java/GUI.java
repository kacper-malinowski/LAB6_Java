import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.example.*;

import java.util.List;

public class GUI {
    private JPanel mainPanel;
    private JButton targetDirButton;
    private JButton sourceDirButton;
    private JLabel sourceDir;
    private JLabel targetDir;
    private JList foundDirList;
    private JButton sortButton;
    private JScrollPane foundDirScrollPane;
    private JPanel foundDirPanel;
    private JTextArea logText;
    private JPanel logPanel;
    private JCheckBox selectAllCheckBox;
    private JComboBox threadsComboBox;
    private JButton emptyDirButton;
    private List<JCheckBox> checkBoxList;

    private String targetDirectory = null;
    private String sourceDirectory = null;

    private List<String> chosenDirList;


    public static void main(String[] args) {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().mainPanel);
        frame.setPreferredSize(new Dimension(1024, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public GUI() {
        checkBoxList = new ArrayList<JCheckBox>();
        chosenDirList = new ArrayList<String>();

        targetDirButton.addActionListener(e -> ChooseDirectory(targetDirButton, "target"));
        sourceDirButton.addActionListener(e -> ChooseDirectory(sourceDirButton, "source"));
        emptyDirButton.addActionListener(e -> EmptyTargetDirectory());
        sortButton.addActionListener(e -> AssignSorting());
        selectAllCheckBox.addActionListener(e -> CheckAllBoxes());

        foundDirPanel.setLayout(new BoxLayout(foundDirPanel, BoxLayout.Y_AXIS));
        foundDirScrollPane.setViewportView(foundDirPanel);

        //foundDirPanel = new JPanel();
        foundDirPanel.setLayout(new BoxLayout(foundDirPanel, BoxLayout.Y_AXIS));

        //logText = new JTextArea();
        logText.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logText);
        logPanel.setLayout(new BorderLayout());
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); ++i) {
            threadsComboBox.addItem(i);
        }

        logText.append("Welcome to The Nut Segregator!\n Choose the source and target directory to continue\n");
    }

    private void CheckAllBoxes() {
        if (selectAllCheckBox.isSelected()) {
            for (JCheckBox checkBox : checkBoxList) {
                checkBox.setSelected(true);
            }
        } else {
            for (JCheckBox checkBox : checkBoxList) {
                checkBox.setSelected(false);
            }
        }
    }


    private void UpdateDirList(List<String> directoryPathList) {
        foundDirPanel.removeAll();
        checkBoxList.clear();

        for (String path : directoryPathList) {
            JCheckBox checkBox = new JCheckBox(path);
            checkBoxList.add(checkBox);
            foundDirPanel.add(checkBox);
        }

        foundDirPanel.revalidate();
        foundDirPanel.repaint();
    }

    private void ChooseDirectory(JButton button, String buttonType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(button);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            if (buttonType.equals("target")) {
                targetDirectory = selectedFolder.getAbsolutePath();
                targetDir.setText(targetDirectory);

            } else if (buttonType.equals("source")) {
                sourceDirectory = selectedFolder.getAbsolutePath();
                sourceDir.setText(sourceDirectory);
                FileOperations fileOperations = new FileOperations();
                fileOperations.findDirectories(sourceDirectory);

                UpdateDirList(fileOperations.getDirectoryPathList());
                logText.append("Now select the directories you wish to sort\n");
            }
            System.out.println(buttonType + " directory selected: " + selectedFolder.getAbsolutePath());
        }
    }

    private void EmptyTargetDirectory() {
        if (targetDirectory == null) {
            logText.append("Error: Target directory not chosen!\n");
            return;
        }
        Path targetPath = Paths.get(targetDirectory);
        if (Files.exists(targetPath)) {
            clearDirectory(targetPath);
            logText.append("Target directory cleared successfully.\n");
        } else {
            logText.append("Error: Target directory doesn't exist!\n");
        }


    }

    private void clearDirectory(Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    clearDirectory(entry);
                }
                Files.delete(entry);
            }
        } catch (IOException e) {
            logText.append("Error: Failed to clear directory!\n");
        }
    }


    private void AssignSorting() {

        if (sourceDirectory == null || targetDirectory == null) {
            logText.append("Error: Please select the source and target directory!\n");
            return;
        }

        Boolean isFolderSelected = false;
        for (JCheckBox checkBox : checkBoxList) {
            if (checkBox.isSelected()) {
                isFolderSelected = true;
                break;
            }
        }
        if (!isFolderSelected) {
            logText.append("Error: Select at least one directory to sort!\n");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(threadsComboBox.getSelectedItem().toString()));

        FileOperations fileOperations = new FileOperations();
        fileOperations.CreateDirectories(targetDirectory);
        chosenDirList.clear();
        for (JCheckBox checkBox : checkBoxList) {
            if (checkBox.isSelected()) {
                chosenDirList.add(checkBox.getText());
            }
        }

        for (String directory : chosenDirList) {
            executor.submit(fileOperations.sortFilesTask(directory, targetDirectory, logText));
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        logText.append("Sorting finished!\n");
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(13, 8, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setPreferredSize(new Dimension(300, 367));
        mainPanel.setRequestFocusEnabled(false);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 24, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(11);
        label1.setHorizontalTextPosition(0);
        label1.setText("The Nut Segregator");
        mainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 7, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, 69), null, 0, false));
        targetDirButton = new JButton();
        targetDirButton.setText("Choose Target Directory");
        mainPanel.add(targetDirButton, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 30), null, 0, false));
        sourceDir = new JLabel();
        sourceDir.setText("Choose Origin Directory");
        mainPanel.add(sourceDir, new com.intellij.uiDesigner.core.GridConstraints(3, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 30), null, 0, false));
        targetDir = new JLabel();
        targetDir.setText("Choose Target Directory");
        mainPanel.add(targetDir, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 30), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Threads used");
        mainPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        foundDirScrollPane = new JScrollPane();
        mainPanel.add(foundDirScrollPane, new com.intellij.uiDesigner.core.GridConstraints(3, 6, 9, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        foundDirPanel = new JPanel();
        foundDirPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        foundDirScrollPane.setViewportView(foundDirPanel);
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 7, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(30, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(30, -1), null, 0, false));
        logPanel = new JPanel();
        logPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(logPanel, new com.intellij.uiDesigner.core.GridConstraints(11, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logText = new JTextArea();
        logPanel.add(logText, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, 1, null, new Dimension(11, 45), null, 0, false));
        sourceDirButton = new JButton();
        sourceDirButton.setText("Choose Origin Directory");
        mainPanel.add(sourceDirButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 30), null, 0, false));
        sortButton = new JButton();
        sortButton.setText("Sort");
        mainPanel.add(sortButton, new com.intellij.uiDesigner.core.GridConstraints(10, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, 30), null, 0, false));
        selectAllCheckBox = new JCheckBox();
        selectAllCheckBox.setText("Select All");
        mainPanel.add(selectAllCheckBox, new com.intellij.uiDesigner.core.GridConstraints(2, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(3, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, 1, null, new Dimension(30, -1), new Dimension(30, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 30), null, 0, false));
        threadsComboBox = new JComboBox();
        mainPanel.add(threadsComboBox, new com.intellij.uiDesigner.core.GridConstraints(8, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(-1, 5), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(-1, 5), 0, false));
        emptyDirButton = new JButton();
        emptyDirButton.setText("Empty Target Directory");
        mainPanel.add(emptyDirButton, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 30), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
