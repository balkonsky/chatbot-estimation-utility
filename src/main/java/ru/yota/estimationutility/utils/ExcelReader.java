package ru.yota.estimationutility.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;

public class ExcelReader {
    private String filepath = null;
    private String sheetname = null;
    private Logger log = LogManager.getLogger(ExcelReader.class);

    private ArrayList<String> truelistphrase = new ArrayList();
    private ArrayList<String> truelistsubject = new ArrayList();
    private XSSFWorkbook xssfWorkbook = null;
    private XSSFSheet xssfSheet = null;
    private DataFormatter formatter = null;

    public ExcelReader(String filepath,String sheetname) {
        this.filepath = filepath;
        this.sheetname = sheetname;
    }

    public void readPhraseFromExcel() {
        log.debug("reading phrase... ");
        try {
            xssfWorkbook = new XSSFWorkbook(new FileInputStream(filepath));
            log.debug("excel file location: {}",filepath);
            xssfSheet = xssfWorkbook.getSheet(sheetname);
            log.debug("excel sheet name: {}",sheetname);
            for (int i = 0; i < xssfSheet.getPhysicalNumberOfRows(); i++) {
                formatter = new DataFormatter();
                String line = formatter.formatCellValue(xssfSheet.getRow(i).getCell(1));
                log.debug("read phrase from excel file: {}",line);
                truelistphrase.add(line);
            }
            xssfWorkbook.close();
        } catch (Exception e) {
            log.error("Error reading phrase: ", e);
            System.exit(0);
        }
    }

    public void readSubjectFromExcel() {
        log.debug("reading subject");
        try {
            xssfWorkbook = new XSSFWorkbook(new FileInputStream(filepath));
            xssfSheet = xssfWorkbook.getSheet(sheetname);
            for (int i = 0; i < xssfSheet.getPhysicalNumberOfRows(); i++) {
                truelistsubject.add(xssfSheet.getRow(i).getCell(0).getStringCellValue());
            }
        } catch (Exception e) {
            log.error("Error reading subject", e);
            System.exit(0);
        }
    }

    public ArrayList<String> getTrueListPhrase(){
        return truelistphrase;
    }

    public ArrayList<String> getTrueListSubject(){
        return truelistsubject;
    }
}
