package server_side;

import java.util.ArrayList;

public class Printer {

    final ArrayList<String> fileNames;
    final String printerName;

    public Printer(String name, int sizeOfQueue) {
        fileNames = new ArrayList<>(sizeOfQueue);
        printerName = name;
    }

    public void addFile(String filename) {
        fileNames.add(filename);
    }

    public void moveFirstInQueue(int jobId) {
        String fileName = fileNames.get(jobId);
        fileNames.remove(jobId);
        fileNames.add(0, fileName);

    }

    public void deleteFile(String fileName) {
        int itemPos = fileNames.indexOf(fileName);
        if (itemPos != 0) {
            fileNames.remove(itemPos);
        }
    }

    public void listQueue() {
        for (int i = 0; i < fileNames.size(); i++) {
            System.out.println(fileNames.get(i));
        }
    }

    public void restartPrinter() {
        fileNames.clear();
    }

}
