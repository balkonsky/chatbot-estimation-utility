package ru.yota.estimationutility;

import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.*;


public class ConfusionMatrix {
    private Workbook workbook = new XSSFWorkbook();
    private Sheet sheet = workbook.createSheet("ConfusionMatrix");
    private Row row1 = sheet.createRow(0);
    private int q = 0;
    private final static org.apache.logging.log4j.Logger log = LogManager.getLogger(ConfusionMatrix.class);

    public void initMatrix(String excel_matrix_write, ArrayList<String> true_subject, ArrayList<String> pred_subject) {
        log.debug("init confusion matrix...");
        try {
            //формируем столбцы из вручную размеченных тематик
            for (int i = 0; i < true_subject.size(); i++) {
                Cell cell = row1.createCell(i + 1);
                cell.setCellValue(true_subject.get(i));
                workbook.write(new FileOutputStream(excel_matrix_write));
            }
            Cell cell = row1.createCell(sheet.getRow(0).getPhysicalNumberOfCells() + 1);
            cell.setCellValue("precision");
            workbook.write(new FileOutputStream(excel_matrix_write));
            Cell celll1 = row1.createCell(sheet.getRow(0).getPhysicalNumberOfCells() + 1);
            celll1.setCellValue("recall");
            workbook.write(new FileOutputStream(excel_matrix_write));
            //формируем строки из размеченных тематик классификатором и заполняем матрицу нулями
            for (int i = 0; i < pred_subject.size(); i++) {
                row1 = sheet.createRow(i + 1);
                for (int j = 0; j < true_subject.size(); j++) {
                    Cell cell1 = row1.createCell(0);
                    cell1.setCellValue(pred_subject.get(i));
                    Cell cell2 = row1.createCell(j + 1);
                    cell2.setCellValue("0");
                    workbook.write(new FileOutputStream(excel_matrix_write));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillingMatrix(String excel_matrix_write, ArrayList<String> true_subject, ArrayList<String> pred_subject, ArrayList<String> list_presubj, ArrayList<String> list_first_subject) {
        log.debug("filling matrix...");
        Map map = new LinkedHashMap<String, String>();

        //считаем кол-во успешно отнесенных фраз по каждой тематике
        try {
            for (int i = 0; i < list_presubj.size(); i++) {
                int count = 0;
                for (int j = 0; j < true_subject.size(); j++) {
                    if (true_subject.get(j).equals(list_presubj.get(i)) && true_subject.get(j).equals(pred_subject.get(j))) {
                        count += 1;
                    }
                }
                map.put(list_presubj.get(i), count);
            }

            // заносим информацию в матрицу по кол-во успешно отнесенных фраз по каждой тематике
            for (int i = 0; i < map.size(); i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getStringCellValue().equals(new ArrayList<String>(map.keySet()).get(i))) {
                            list_index.add(cell.getRowIndex());
                            list_index.add(cell.getColumnIndex());
                            if (list_index.size() > 3) {
                                Cell cell1 = sheet.getRow(list_index.get(2)).getCell(list_index.get(1));
                                cell1.setCellValue(String.valueOf(new ArrayList<Integer>(map.values()).get(i)));
                                workbook.write(new FileOutputStream(excel_matrix_write));
                            }
                        }
                    }
                }
            }

            //Заносим информацию в матрицу по кол-ву не успешно отнесенных фраз по каждой тематики
            for (int i = 0; i < true_subject.size(); i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                if (!true_subject.get(i).equals(pred_subject.get(i))) {
                    for (int j = 1; j < sheet.getRow(0).getPhysicalNumberOfCells(); j++) {
                        Cell cell1 = sheet.getRow(0).getCell(j);
                        if (true_subject.get(i).equals(cell1.toString())) {
                            for (int k = 1; k < sheet.getPhysicalNumberOfRows(); k++) {
                                Cell cell2 = sheet.getRow(k).getCell(0);
                                if (pred_subject.get(i).equals(cell2.toString())) {
                                    list_index.add(cell1.getRowIndex());
                                    list_index.add(cell1.getColumnIndex());
                                    list_index.add(cell2.getRowIndex());
                                    list_index.add(cell2.getColumnIndex());
                                    if (list_index.size() > 3) {
                                        Cell cell3 = sheet.getRow(list_index.get(2)).getCell(list_index.get(1));
                                        cell3.setCellValue(String.valueOf(Integer.parseInt(cell3.toString()) + 1));
                                        workbook.write(new FileOutputStream(excel_matrix_write));
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
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                int sum = 0;
                for (int j = 1; j < sheet.getRow(0).getPhysicalNumberOfCells() - 1; j++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    sum += Integer.parseInt(cell.toString());
                }
                sum_of_all_phrase_in_pred_subject.add(sum);
            }

            for (int i = 1; i < sheet.getRow(0).getPhysicalNumberOfCells() - 1; i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                double count = 0.0;
                Cell cell_true_subject = sheet.getRow(0).getCell(i);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                    Cell cell_pred_subject = sheet.getRow(j).getCell(0);
                    if (cell_true_subject.toString().equals(cell_pred_subject.toString())) {
                        list_index.add(cell_true_subject.getRowIndex());
                        list_index.add(cell_true_subject.getColumnIndex());
                        list_index.add(cell_pred_subject.getRowIndex());
                        list_index.add(cell_pred_subject.getColumnIndex());
                        if (list_index.size() == 4) {
                            Cell cell_count = sheet.getRow(list_index.get(2)).getCell(list_index.get(1));
                            count = Double.valueOf(cell_count.toString()) / (double) sum_of_all_phrase_in_pred_subject.get(j - 1);
                            Cell cell_count_write = sheet.getRow(list_index.get(2)).createCell((sheet.getRow(0).getPhysicalNumberOfCells()) - 1);
                            cell_count_write.setCellValue(String.valueOf(new DecimalFormat("#0.00").format(count)));
                            workbook.write(new FileOutputStream(excel_matrix_write));
                        }
                    }
                }
            }

            //считаем recall по каждой тематике
            log.debug("count recall");
            ArrayList<Integer> sum_of_all_phrase_in_true_subject = new ArrayList<Integer>();
            for (int i = 1; i < sheet.getRow(0).getPhysicalNumberOfCells() - 1; i++) {
                int sum = 0;
                Cell cell_true_subject = sheet.getRow(0).getCell(i);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                    Cell cell_pred_subject = sheet.getRow(j).getCell(0);
                    if (cell_true_subject.toString().equals(cell_pred_subject.toString())) {
                        for (int k = 1; k < sheet.getPhysicalNumberOfRows(); k++) {
                            Cell cell = sheet.getRow(k).getCell(i);
                            sum += Integer.parseInt(cell.toString());
                        }
                        sum_of_all_phrase_in_true_subject.add(sum);
                    }
                }
            }
            for (int i = 1; i < sheet.getRow(0).getPhysicalNumberOfCells() - 1; i++) {
                ArrayList<Integer> list_index = new ArrayList<Integer>();
                double count;
                Cell cell_true_subject = sheet.getRow(0).getCell(i);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                    Cell cell_pred_subject = sheet.getRow(j).getCell(0);
                    if (cell_true_subject.toString().equals(cell_pred_subject.toString())) {
                        list_index.add(cell_true_subject.getRowIndex());
                        list_index.add(cell_true_subject.getColumnIndex());
                        list_index.add(cell_pred_subject.getRowIndex());
                        list_index.add(cell_pred_subject.getColumnIndex());
                        if (list_index.size() == 4) {
                            Cell cell_count = sheet.getRow(list_index.get(2)).getCell(list_index.get(1));
                            count = Double.valueOf(cell_count.toString()) / (double) sum_of_all_phrase_in_true_subject.get(q);
                            Cell cell_count_write = sheet.getRow(list_index.get(2)).createCell((sheet.getRow(0).getPhysicalNumberOfCells()));
                            cell_count_write.setCellValue(String.valueOf(new DecimalFormat("#0.00").format(count)));
                            workbook.write(new FileOutputStream(excel_matrix_write));
                            this.q++;
                        }
                    }
                }
            }
            log.debug("all data is successfully generated");

            workbook.close();

        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
}
