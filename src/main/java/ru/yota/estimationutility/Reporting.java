package ru.yota.estimationutility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Reporting {
    private int q = 0;
    private int match = 0;
    private String accuracy = null;

    private final static Logger log = LogManager.getLogger(ConfusionMatrix.class);

    public String countAccuracy(ArrayList<String> truelistsubject, ArrayList<String> predlistfirstsubject) {
        log.debug("count accuracy...");

        try {
            if (truelistsubject.size() == predlistfirstsubject.size()) {
                for (int i = 0; i < truelistsubject.size(); i++) {
                    if ((truelistsubject.get(i)).equals(predlistfirstsubject.get(i))) {
                        match++;
                    }
                }
                accuracy = new DecimalFormat("#0.00").format(((double) match / (double) truelistsubject.size()) * 100);
                log.trace("accuracy: {}", accuracy + "%");
                return accuracy;
            } else {
                throw new NullPointerException("lists are not equals");
            }
        } catch (Exception e) {
            log.error("eror with count accuracy", e);
            System.exit(0);
        }
        return null;
    }


    public void countPrecision() {

    }

    public void countRecall() {

    }


}
