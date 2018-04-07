package ru.yota.estimationutility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Classificator {
    ArrayList<String> true_subject = new ArrayList();
    ArrayList<String> pred_subject = new ArrayList();
    private static final Logger log = LogManager.getLogger(Classificator.class);

    public static void writeXlsx(String excel_file_write, ArrayList<String> list_presubj, ArrayList<String> list_phrase, ArrayList<String> list_first_subject, ArrayList<String> list_first_confidence, ArrayList<String> list_second_subject, ArrayList<String> list_second_confidence, ArrayList<String> list_third_subject, ArrayList<String> list_third_confidence) {
        log.debug(" writing to report file...");
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("classification");
            Row row1 = sheet.createRow(0);
            String[] arr = {"Pre-Subject", "Phrase", "First_Classification_subject", "First_Classification_confidence", "Second_Classification_subject", "Second_Classification_confidence", "Third_Classification_subject", "Third_Classification_confidence"};
            for (int i = 0; i < arr.length; i++) {
                Cell cell = row1.createCell(i);
                cell.setCellValue(arr[i]);
            }
            workbook.write(new FileOutputStream(excel_file_write));


            for (int i = 0; i < list_presubj.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Cell cell1 = row.createCell(0);
                Cell cell2 = row.createCell(1);
                Cell cell3 = row.createCell(2);
                Cell cell4 = row.createCell(3);
                Cell cell5 = row.createCell(4);
                Cell cell6 = row.createCell(5);
                Cell cell7 = row.createCell(6);
                Cell cell8 = row.createCell(7);
                cell1.setCellValue(list_presubj.get(i));
                cell2.setCellValue(list_phrase.get(i));
                cell3.setCellValue(list_first_subject.get(i));
                cell4.setCellValue(list_first_confidence.get(i));
                cell5.setCellValue(list_second_subject.get(i));
                cell6.setCellValue(list_second_confidence.get(i));
                cell7.setCellValue(list_third_subject.get(i));
                cell8.setCellValue(list_third_confidence.get(i));
                workbook.write(new FileOutputStream(excel_file_write));
            }
            workbook.close();
        } catch (Exception e) {
            log.error("Error writing file: ", e);
            System.exit(0);
        }
    }

    public ArrayList<String> readXlsx_phrase(String excel_file_read) {
        log.debug("reading phrase... ");

        ArrayList<String> list_phrase = new ArrayList();
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new java.io.FileInputStream(excel_file_read));
            XSSFSheet xssfSheet = xssfWorkbook.getSheet("Лист1");
            for (int i = 0; i < xssfSheet.getPhysicalNumberOfRows(); i++) {
                DataFormatter formatter = new DataFormatter();
                String line = formatter.formatCellValue(xssfSheet.getRow(i).getCell(1));
                list_phrase.add(line);
            }
        } catch (Exception e) {
            log.error("Error reading phrase: ", e);
            System.exit(0);
        }
        return list_phrase;
    }

    public ArrayList<String> readXlsx_subject(String excel_file_read) {
        log.debug("reading subject");
        ArrayList<String> list_subject = new ArrayList();
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new java.io.FileInputStream(excel_file_read));
            XSSFSheet xssfSheet = xssfWorkbook.getSheet("Лист1");
            for (int i = 0; i < xssfSheet.getPhysicalNumberOfRows(); i++) {
                list_subject.add(xssfSheet.getRow(i).getCell(0).getStringCellValue());
            }
        } catch (Exception e) {
            log.error("Error reading subject", e);
            System.exit(0);
        }
        return list_subject;
    }

    public ArrayList<String> classifyPhrase(String host, int port, String url, String excel_file_write, ArrayList<String> list_subject, ArrayList<String> list_phrase) {
        ArrayList<String> list_first_subject = new ArrayList();
        ArrayList<String> list_first_confidence = new ArrayList();
        ArrayList<String> list_second_subject = new ArrayList();
        ArrayList<String> list_second_confidence = new ArrayList();
        ArrayList<String> list_third_subject = new ArrayList();
        ArrayList<String> list_third_confidence = new ArrayList();
        log.debug("classify...");

        try {
            org.apache.http.client.config.RequestConfig config = org.apache.http.client.config.RequestConfig.custom().setConnectTimeout(50000).setConnectTimeout(50000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).build();
            for (String s : list_phrase) {
                StringBuilder sb = new StringBuilder();
                org.apache.http.impl.client.CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                URIBuilder builder = new URIBuilder();
                builder.setScheme("http").setHost(host).setPort(port).setPath(url)
                        .setParameter("utterance", s);
                java.net.URI uri = builder.build();
                org.apache.http.client.methods.HttpGet httpGet = new org.apache.http.client.methods.HttpGet(uri);
                CloseableHttpResponse response = client.execute(httpGet);
                org.apache.http.HttpEntity entity = response.getEntity();
                log.debug("get result for phrase '" + s + "' " + response.getStatusLine());

                if (response.getStatusLine().getStatusCode() == 200) {
                    if (entity != null) {
                        BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(response.getEntity().getContent()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }
                    }
                    list_first_subject.add(parsePhraseFirstSubjects(sb));
                    list_first_confidence.add(parsePhraseFirstConfidence(sb));
                    list_second_subject.add(parsePhraseSecondSubjects(sb));
                    list_second_confidence.add(parsePhraseSecondConfidence(sb));
                    list_third_subject.add(parsePhraseThirdSubjects(sb));
                    list_third_confidence.add(parsePhraseThirdConfidence(sb));
                } else {
                    log.error("Faile connection: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                    log.debug("retry request...");
                    StringBuilder sb2 = new StringBuilder();
                    try {
                        org.apache.http.client.methods.HttpGet httpGet_re = httpGet;
                        CloseableHttpResponse response_re = client.execute(httpGet_re);
                        org.apache.http.HttpEntity entity_re = response.getEntity();
                        log.debug("get result for phrase " + s + " " + response_re.getStatusLine());
                        if (response_re.getStatusLine().getStatusCode() == 200) {
                            if (entity_re != null) {
                                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(response_re.getEntity().getContent()));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb2.append(line);
                                    sb2.append("\n");
                                }
                                list_first_subject.add(parsePhraseFirstSubjects(sb2));
                                list_first_confidence.add(parsePhraseFirstConfidence(sb2));
                                list_second_subject.add(parsePhraseSecondSubjects(sb2));
                                list_second_confidence.add(parsePhraseSecondConfidence(sb2));
                                list_third_subject.add(parsePhraseThirdSubjects(sb2));
                                list_third_confidence.add(parsePhraseThirdConfidence(sb2));
                            }
                        }
                    } catch (Exception e) {
                        log.error("Network Error", e);
                    }
                }
            }
            writeXlsx(excel_file_write, list_subject, list_phrase, list_first_subject, list_first_confidence, list_second_subject, list_second_confidence, list_third_subject, list_third_confidence);
        } catch (Throwable cause) {
            log.error("Network Error", cause);
        }
        return list_first_subject;
    }

    private static String parsePhraseFirstSubjects(StringBuilder sb) {
        JsonArray jsonArray = new JsonParser().parse(new String(sb)).getAsJsonArray();
        JsonObject jsonObject = (JsonObject) jsonArray.get(0);
        if (jsonObject != null) {
            String subject = jsonObject.get("subject").getAsString();
            return subject;
        }
        return null;
    }

    private static String parsePhraseFirstConfidence(StringBuilder sb) {
        JsonArray jsonArray = new JsonParser().parse(new String(sb)).getAsJsonArray();
        JsonObject jsonObject = (JsonObject) jsonArray.get(0);
        if (jsonObject != null) {
            String confidence = jsonObject.get("confidence").getAsString();
            return confidence;
        }
        return null;
    }

    private static String parsePhraseSecondSubjects(StringBuilder sb) {
        JsonArray jsonArray = new JsonParser().parse(new String(sb)).getAsJsonArray();
        if (jsonArray.size() > 1) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(1);
            if (jsonObject != null) {
                String subject = jsonObject.get("subject").getAsString();
                return subject;
            }
            return null;
        }
        return null;
    }

    private static String parsePhraseSecondConfidence(StringBuilder sb) {
        JsonArray jsonArray = new JsonParser().parse(new String(sb)).getAsJsonArray();
        if (jsonArray.size() > 1) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(1);
            if (jsonObject != null) {
                String confidence = jsonObject.get("confidence").getAsString();
                return confidence;
            }
            return null;
        }
        return null;
    }

    private static String parsePhraseThirdSubjects(StringBuilder sb) {
        JsonArray jsonArray = new JsonParser().parse(new String(sb)).getAsJsonArray();
        if (jsonArray.size() > 2) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(2);
            if (jsonObject != null) {
                String subject = jsonObject.get("subject").getAsString();
                return subject;
            }
            return null;
        }
        return null;
    }

    private static String parsePhraseThirdConfidence(StringBuilder sb) {
        JsonArray jsonArray = new JsonParser().parse(new String(sb)).getAsJsonArray();
        if (jsonArray.size() > 2) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(2);
            if (jsonObject != null) {
                String confidence = jsonObject.get("confidence").getAsString();
                return confidence;
            }
            return null;
        }
        return null;
    }

    public void reporting(ArrayList<String> list_presubj, ArrayList<String> list_first_subject) {
        log.debug("reporting...");
        int count_good = 0;

        for (int i = 0; i < list_first_subject.size(); i++) {
            if ((list_presubj.get(i)).equals(list_first_subject.get(i))) {
                count_good++;
            }
        }
        log.trace("accuracy: " + new DecimalFormat("#0.00").format(((double) count_good / (double) list_first_subject.size()) * 100) + "%");

        Set<String> set1 = new HashSet<String>(list_presubj);
        Set<String> set2 = new HashSet<String>(list_first_subject);
        ArrayList<String> true_subject = new ArrayList(set1);
        ArrayList<String> pred_subject = new ArrayList(set2);
        Collections.sort(true_subject);
        Collections.sort(pred_subject);
        this.true_subject = true_subject;
        this.pred_subject = pred_subject;
    }

    public ArrayList<String> getTrue_subject() {
        return true_subject;
    }

    public ArrayList<String> getPred_subject() {
        return pred_subject;
    }
}
