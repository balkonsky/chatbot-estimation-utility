package ru.yota.estimationutility.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class ExcelWriter {
    private int i = 0;

    private final Logger log = LogManager.getLogger(ExcelWriter.class);
    private String excel_file_write = null;
    private String[] nameofcolumnarr = {"Pre-Subject",
            "Phrase",
            "First_Classification_subject",
            "First_Classification_confidence",
            "Second_Classification_subject",
            "Second_Classification_confidence",
            "Third_Classification_subject",
            "Third_Classification_confidence"};

    private Workbook workbook = null;
    private Sheet sheetreporting = null;

    private Sheet sheetsummarystatistics = null;


    public ExcelWriter() {

    }

    public ExcelWriter(String excel_file_write) {
        this.excel_file_write = excel_file_write;
    }

    public void writeToExcelFile(ArrayList<String> truelistsubject,
                                 ArrayList<String> truelistphrase,
                                 ArrayList<String> predlistfirstsubject,
                                 ArrayList<String> predlistfirstconfidence,
                                 ArrayList<String> predlistsecondsubject,
                                 ArrayList<String> predlistsecondconfidence,
                                 ArrayList<String> predlistthirdsubject,
                                 ArrayList<String> predlistthirdconfidence) {
        log.debug(" writing to report file...");
        try {
            if (truelistphrase.size() == truelistphrase.size() &&
                    truelistphrase.size() == predlistfirstsubject.size() &&
                    predlistfirstsubject.size() == predlistfirstconfidence.size() &&
                    predlistfirstconfidence.size() == predlistsecondsubject.size() &&
                    predlistsecondsubject.size() == predlistsecondconfidence.size() &&
                    predlistsecondconfidence.size() == predlistthirdsubject.size() &&
                    predlistthirdsubject.size() == predlistthirdconfidence.size()) {
                log.debug("size of all list's are equals, next...");
                workbook = new XSSFWorkbook();
                sheetreporting = workbook.createSheet("classification");
                log.debug("create excel sheet with name  - classification");
                Row row1 = sheetreporting.createRow(0);
                for (int i = 0; i < nameofcolumnarr.length; i++) {
                    Cell cell = row1.createCell(i);
                    cell.setCellValue(nameofcolumnarr[i]);
                }

                log.debug("create name of excel columns : {}", nameofcolumnarr);
                workbook.write(new FileOutputStream(excel_file_write));


                for (int i = 0; i < truelistsubject.size(); i++) {
                    Row row = sheetreporting.createRow(i + 1);
                    Cell cell1 = row.createCell(0);
                    Cell cell2 = row.createCell(1);
                    Cell cell3 = row.createCell(2);
                    Cell cell4 = row.createCell(3);
                    Cell cell5 = row.createCell(4);
                    Cell cell6 = row.createCell(5);
                    Cell cell7 = row.createCell(6);
                    Cell cell8 = row.createCell(7);
                    cell1.setCellValue(truelistsubject.get(i));
                    cell2.setCellValue(truelistphrase.get(i));
                    cell3.setCellValue(predlistfirstsubject.get(i));
                    cell4.setCellValue(predlistfirstconfidence.get(i));
                    cell5.setCellValue(predlistsecondsubject.get(i));
                    cell6.setCellValue(predlistsecondconfidence.get(i));
                    cell7.setCellValue(predlistthirdsubject.get(i));
                    cell8.setCellValue(predlistthirdconfidence.get(i));
                    workbook.write(new FileOutputStream(excel_file_write));
                }
                log.debug("all data is successfully written to the excel file");
            } else {
                log.error("Size of list's is not equals, stop program");
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("Error writing file: ", e);
            System.exit(0);
        }
    }

    public Workbook getCurrentWorkbook() {
        if (!workbook.equals(null)) {
            return workbook;
        } else {
            throw new NullPointerException("excel workbook is not initialized");
        }
    }

    public void writeSummaryStatistics(Map<String, Integer> successpredphrasemap,String accuracy) {
        try {
            sheetsummarystatistics = workbook.createSheet("summary statistics");
            for (Map.Entry<String, Integer> map : successpredphrasemap.entrySet()) {
                Row row = sheetsummarystatistics.createRow(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(map.getKey());
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(String.valueOf(map.getValue()));
                i++;
                workbook.write(new FileOutputStream(excel_file_write));
                if(i==successpredphrasemap.size()){
                    Row row1 = sheetsummarystatistics.createRow(i+1);
                    Cell cell3 = row1.createCell(0);
                    cell3.setCellValue("accuracy");
                    Cell cell4 = row1.createCell(1);
                    cell4.setCellValue(accuracy);
                    workbook.write(new FileOutputStream(excel_file_write));
                }
            }
        }
        catch (Exception e ){
            log.error("error write summary statistics ",e);
        }

    }
}
