package ru.yota.estimationutility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import ru.yota.estimationutility.utils.ExcelWriter;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.*;


public class ConfusionMatrix {
    private Workbook workbook = null;
    private Sheet sheetconfusionmatrix = null;
    private Row rowconfusionmatrix = null;
    private final Logger log = LogManager.getLogger(ConfusionMatrix.class);
    private Map <String,Integer> successpredphrasemap = null;
    private ExcelWriter excelWriter = new ExcelWriter();

    private String excel_file_write = null;
    private int q = 0;

    public ConfusionMatrix(String excel_file_write) {
        this.excel_file_write = excel_file_write;
    }


    public void initMatrix(ArrayList<String> sortlistuniquetruesubjects, ArrayList<String> sortlistuniquepredsubjects, Workbook currentworkbook) {
        log.debug("init confusion matrix...");
        try {
            this.workbook = currentworkbook;
            sheetconfusionmatrix = workbook.createSheet("ConfusionMatrix");
            log.debug("create sheet with name: {}", sheetconfusionmatrix.getSheetName());
            rowconfusionmatrix = sheetconfusionmatrix.createRow(0);
            // формируем столбцы из вручную размеченных тематик
            for (int i = 0; i < sortlistuniquetruesubjects.size(); i++) {
                Cell cell = rowconfusionmatrix.createCell(i + 1);
                cell.setCellValue(sortlistuniquetruesubjects.get(i));
                workbook.write(new FileOutputStream(excel_file_write));
            }
            Cell cell = rowconfusionmatrix.createCell(sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells() + 1);
            cell.setCellValue("precision");
            workbook.write(new FileOutputStream(excel_file_write));
            Cell celll1 = rowconfusionmatrix.createCell(sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells() + 1);
            celll1.setCellValue("recall");
            workbook.write(new FileOutputStream(excel_file_write));
            //формируем строки из размеченных тематик классификатором и заполняем матрицу нулями
            for (int i = 0; i < sortlistuniquepredsubjects.size(); i++) {
                rowconfusionmatrix = sheetconfusionmatrix.createRow(i + 1);
                for (int j = 0; j < sortlistuniquetruesubjects.size(); j++) {
                    Cell cell1 = rowconfusionmatrix.createCell(0);
                    cell1.setCellValue(sortlistuniquepredsubjects.get(i));
                    Cell cell2 = rowconfusionmatrix.createCell(j + 1);
                    cell2.setCellValue("0");
                    workbook.write(new FileOutputStream(excel_file_write));
                }
            }
            log.debug("matrix is successfully initialization");

        } catch (Exception e) {
            log.error("error with init matrix", e);
        }
    }

    public void fillingMatrix(
            ArrayList<String> sortlistuniquetruesubjects,
            ArrayList<String> predlistfirstsubject,
            ArrayList<String> truelistsubject) {

        log.debug("filling matrix...");

        try {
            countSuccessPredPhraseMap(sortlistuniquetruesubjects,predlistfirstsubject,truelistsubject);

            // заносим информацию в матрицу по кол-во успешно отнесенных фраз по каждой тематике
            for (int i = 0; i < successpredphrasemap.size(); i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                for (Row row : sheetconfusionmatrix) {
                    for (Cell cell : row) {
                        if (cell.getStringCellValue().equals(new ArrayList<String>(successpredphrasemap.keySet()).get(i))) {
                            list_index.add(cell.getRowIndex());
                            list_index.add(cell.getColumnIndex());
                            if (list_index.size() > 3) {
                                Cell cell1 = sheetconfusionmatrix.getRow(list_index.get(2)).getCell(list_index.get(1));
                                cell1.setCellValue(String.valueOf(new ArrayList<Integer>(successpredphrasemap.values()).get(i)));
                                workbook.write(new FileOutputStream(excel_file_write));
                            }
                        }
                    }
                }
            }
            //Заносим информацию в матрицу по кол-ву не успешно отнесенных фраз по каждой тематики
            for (int i = 0; i < sortlistuniquetruesubjects.size(); i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                if (!sortlistuniquetruesubjects.get(i).equals(predlistfirstsubject.get(i))) {
                    for (int j = 1; j < sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells(); j++) {
                        Cell cell1 = sheetconfusionmatrix.getRow(0).getCell(j);
                        if (sortlistuniquetruesubjects.get(i).equals(cell1.toString())) {
                            for (int k = 1; k < sheetconfusionmatrix.getPhysicalNumberOfRows(); k++) {
                                Cell cell2 = sheetconfusionmatrix.getRow(k).getCell(0);
                                if (predlistfirstsubject.get(i).equals(cell2.toString())) {
                                    list_index.add(cell1.getRowIndex());
                                    list_index.add(cell1.getColumnIndex());
                                    list_index.add(cell2.getRowIndex());
                                    list_index.add(cell2.getColumnIndex());
                                    if (list_index.size() > 3) {
                                        Cell cell3 = sheetconfusionmatrix.getRow(list_index.get(2)).getCell(list_index.get(1));
                                        cell3.setCellValue(String.valueOf(Integer.parseInt(cell3.toString()) + 1));
                                        workbook.write(new FileOutputStream(excel_file_write));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //считаем precission по каждой тематике
            log.debug("count precision");
            ArrayList<Integer> sum_of_all_phrase_in_pred_subject = new ArrayList<Integer>();
            for (int i = 1; i < sheetconfusionmatrix.getPhysicalNumberOfRows(); i++) {
                int sum = 0;
                for (int j = 1; j < sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells() - 1; j++) {
                    Cell cell = sheetconfusionmatrix.getRow(i).getCell(j);
                    sum += Integer.parseInt(cell.toString());
                }
                sum_of_all_phrase_in_pred_subject.add(sum);
            }

            for (int i = 1; i < sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells() - 1; i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                double count = 0.0;
                Cell cell_true_subject = sheetconfusionmatrix.getRow(0).getCell(i);
                for (int j = 1; j < sheetconfusionmatrix.getPhysicalNumberOfRows(); j++) {
                    Cell cell_pred_subject = sheetconfusionmatrix.getRow(j).getCell(0);
                    if (cell_true_subject.toString().equals(cell_pred_subject.toString())) {
                        list_index.add(cell_true_subject.getRowIndex());
                        list_index.add(cell_true_subject.getColumnIndex());
                        list_index.add(cell_pred_subject.getRowIndex());
                        list_index.add(cell_pred_subject.getColumnIndex());
                        if (list_index.size() == 4) {
                            Cell cell_count = sheetconfusionmatrix.getRow(list_index.get(2)).getCell(list_index.get(1));
                            count = Double.valueOf(cell_count.toString()) / (double) sum_of_all_phrase_in_pred_subject.get(j - 1);
                            Cell cell_count_write = sheetconfusionmatrix.getRow(list_index.get(2)).createCell((sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells()) - 1);
                            cell_count_write.setCellValue(String.valueOf(new DecimalFormat("#0.00").format(count)));
                            workbook.write(new FileOutputStream(excel_file_write));
                        }
                    }
                }
            }

            //считаем recall по каждой тематике
            log.debug("count recall");
            ArrayList<Integer> sum_of_all_phrase_in_true_subject = new ArrayList<Integer>();
            for (int i = 1; i < sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells() - 1; i++) {
                int sum = 0;
                Cell cell_true_subject = sheetconfusionmatrix.getRow(0).getCell(i);
                for (int j = 1; j < sheetconfusionmatrix.getPhysicalNumberOfRows(); j++) {
                    Cell cell_pred_subject = sheetconfusionmatrix.getRow(j).getCell(0);
                    if (cell_true_subject.toString().equals(cell_pred_subject.toString())) {
                        for (int k = 1; k < sheetconfusionmatrix.getPhysicalNumberOfRows(); k++) {
                            Cell cell = sheetconfusionmatrix.getRow(k).getCell(i);
                            sum += Integer.parseInt(cell.toString());
                        }
                        sum_of_all_phrase_in_true_subject.add(sum);
                    }
                }
            }
            for (int i = 1; i < sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells() - 1; i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                double count;
                Cell cell_true_subject = sheetconfusionmatrix.getRow(0).getCell(i);
                for (int j = 1; j < sheetconfusionmatrix.getPhysicalNumberOfRows(); j++) {
                    Cell cell_pred_subject = sheetconfusionmatrix.getRow(j).getCell(0);
                    if (cell_true_subject.toString().equals(cell_pred_subject.toString())) {
                        list_index.add(cell_true_subject.getRowIndex());
                        list_index.add(cell_true_subject.getColumnIndex());
                        list_index.add(cell_pred_subject.getRowIndex());
                        list_index.add(cell_pred_subject.getColumnIndex());
                        if (list_index.size() == 4) {
                            Cell cell_count = sheetconfusionmatrix.getRow(list_index.get(2)).getCell(list_index.get(1));
                            count = Double.valueOf(cell_count.toString()) / (double) sum_of_all_phrase_in_true_subject.get(q);
                            Cell cell_count_write = sheetconfusionmatrix.getRow(list_index.get(2)).createCell((sheetconfusionmatrix.getRow(0).getPhysicalNumberOfCells()));
                            cell_count_write.setCellValue(String.valueOf(new DecimalFormat("#0.00").format(count)));
                            workbook.write(new FileOutputStream(excel_file_write));
                            this.q++;
                        }
                    }
                }
            }
            log.debug("all data is successfully generated");

        } catch (
                Exception e)

        {
            log.error("Error: ", e);
        }

    }

    public void countSuccessPredPhraseMap(
            ArrayList<String> sortlistuniquetruesubjects,
            ArrayList<String> predlistfirstsubject,
            ArrayList<String> truelistsubject) {
        //считаем кол-во успешно отнесенных фраз по каждой тематике
        try {
            successpredphrasemap = new LinkedHashMap<String, Integer>();
            for (int i = 0; i < sortlistuniquetruesubjects.size(); i++) {
                int count = 0;
                for (int j = 0; j < truelistsubject.size(); j++) {
                    if (truelistsubject.get(j).equals(sortlistuniquetruesubjects.get(i)) &&
                            truelistsubject.get(j).equals(predlistfirstsubject.get(j))) {
                        count += 1;
                    }
                }
                successpredphrasemap.put(sortlistuniquetruesubjects.get(i), count);
            }
        } catch (Exception e) {
            log.error("error with count success prediction phrase map", e);
        }
    }

    public Map<String, Integer> getSuccessPredPhraseMap() {
        if (!successpredphrasemap.equals(null)) {
            return successpredphrasemap;
        } else {
            throw new NullPointerException("success prediction phrase map is null");
        }
    }
}
