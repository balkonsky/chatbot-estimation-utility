package ru.yota.estimationutility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yota.estimationutility.utils.Configuration;
import ru.yota.estimationutility.utils.ExcelReader;
import ru.yota.estimationutility.utils.ExcelWriter;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Map;

public class StartApp {
    private final static Logger log = LogManager.getLogger(StartApp.class);
    private static ExcelReader excelReader = null;
    private static Classificator classificator = new Classificator();
    private static Configuration config = new Configuration();
    private static ExcelWriter excelWriter = null;
    private static ConfusionMatrix confusionMatrix = null;
    private static Reporting reporting = null;

    private static ArrayList<String> truelistsubject = new ArrayList<String>();
    private static ArrayList<String> truelistphrase = new ArrayList<String>();

    private static ArrayList<String> predlistfirstsubject = new ArrayList<String>();
    private static ArrayList<String> predlistfirstconfidence = new ArrayList<String>();
    private static ArrayList<String> predlistsecondsubject = new ArrayList<String>();
    private static ArrayList<String> predlistsecondconfidence = new ArrayList<String>();
    private static ArrayList<String> predlistthirdsubject = new ArrayList<String>();
    private static ArrayList<String> predlistthirdconfidence = new ArrayList<String>();

    private static ArrayList<String> sortlistuniquetruesubjects = null;
    private static ArrayList<String> sortlistuniquepredsubjects = null;

    public static void main(String[] args) {
        try {
            excelReader = new ExcelReader(config.getExcelInputTrueFileReadLocation(), config.getExcelInputTrueSheetName());
            excelReader.readPhraseFromExcel();
            excelReader.readSubjectFromExcel();

            truelistsubject = excelReader.getTrueListSubject();
            truelistphrase = excelReader.getTrueListPhrase();

            classificator.classifyPhrase(truelistsubject, truelistphrase);

            predlistfirstsubject = classificator.getPredFirstSubjectList();
            predlistfirstconfidence = classificator.getPredFirstConfidenceList();

            predlistsecondsubject = classificator.getPredSecondSubjectList();
            predlistsecondconfidence = classificator.getPredSecondConfidenceList();

            predlistthirdsubject = classificator.getPredThirdSubject();
            predlistthirdconfidence = classificator.getPredThirdConfidenceList();

            excelWriter = new ExcelWriter(config.getExcelOutputReportFileLocation());
            excelWriter.writeToExcelFile(
                    truelistsubject,
                    truelistphrase,
                    predlistfirstsubject,
                    predlistfirstconfidence,
                    predlistsecondsubject,
                    predlistsecondconfidence,
                    predlistthirdsubject,
                    predlistthirdconfidence);

            sortlistuniquetruesubjects = classificator.getuSortUiqueTrueSubjects(truelistsubject);
            sortlistuniquepredsubjects = classificator.getSortUniquePredSubjects(predlistfirstsubject);

            confusionMatrix = new ConfusionMatrix(config.getExcelOutputReportFileLocation());
            confusionMatrix.initMatrix(sortlistuniquetruesubjects,sortlistuniquepredsubjects,excelWriter.getCurrentWorkbook());
            confusionMatrix.fillingMatrix(sortlistuniquetruesubjects,predlistfirstsubject,truelistsubject);

            reporting = new Reporting();
            excelWriter.writeSummaryStatistics(confusionMatrix.getSuccessPredPhraseMap(),reporting.countAccuracy(truelistsubject,predlistfirstsubject));



        } catch (Exception e) {

        }

    }
//        ArrayList<String> list_pred_phrase = excelReader.readPhraseFromExcel();
//        ArrayList<String> list_pred_subject = excelReader.readSubjectFromExcel();
//        ArrayList<String> list_nbest_first_subject = classificator.classifyPhrase(host, port, url, excel_file_write, list_pred_subject, list_pred_phrase);
//
//               /* будущий задел под реализацию многопоточности
//            int part = list_pred_phrase.size() / 4;
//            final List<String> list_pred_phrase_first_part = list_pred_phrase.subList(0, part);
//            final List<String> list_pred_phrase_second_part = list_pred_phrase.subList(part, part + part);
//            final List<String> list_pred_phrase_third_part = list_pred_phrase.subList(part + part, part + part + part);
//            final List<String> list_pred_phrase_fourth_part = list_pred_phrase.subList(part + part + part, list_pred_phrase.size());
//
//            final List<String> list_pred_subject_first_part = list_pred_subject.subList(0, part);
//            final List<String> list_pred_subject_second_part = list_pred_subject.subList(part, part + part);
//            final List<String> list_pred_subject_third_part = list_pred_subject.subList(part + part, part + part + part);
//            final List<String> list_pred_subject_fourth_part = list_pred_subject.subList(part + part + part, list_pred_phrase.size());
//
//            Thread firtst_thread = new Threads() {
//                @Override
//                public void run() {
//                    ArrayList<String> list_nbest_first_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_first_part), new ArrayList<String>(list_pred_phrase_first_part));
//                    list_nbest_first_subject.addAll(list_nbest_first_part_subject);
//                }
//            };
//            firtst_thread.start();
//
//            Thread second_thread = new Threads() {
//                @Override
//                public void run() {
//                    ArrayList<String> list_nbest_second_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_second_part), new ArrayList<String>(list_pred_phrase_second_part));
//                    list_nbest_first_subject.addAll(list_nbest_second_part_subject);
//                }
//            };
//            second_thread.start();
//
//            Thread third_thread = new Threads() {
//                @Override
//                public void run() {
//                    ArrayList<String> list_nbest_third_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_third_part), new ArrayList<String>(list_pred_phrase_third_part));
//                    list_nbest_first_subject.addAll(list_nbest_third_part_subject);
//                }
//            };
//            third_thread.start();
//
//            ArrayList<String> list_nbest_fourth_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_fourth_part), new ArrayList<String>(list_pred_phrase_fourth_part));
//            list_nbest_first_subject.addAll(list_nbest_fourth_part_subject);
//
//        }
//
//        for (String s :list_nbest_first_subject) {
//            System.out.println(s);
//        }
//
//        //classificator.writeXlsx(excel_file_write, list_subject, list_phrase, list_first_subject, list_first_confidence, list_second_subject, list_second_confidence, list_third_subject, list_third_confidence);
//*/
//
//
//        classificator.reporting(list_pred_subject, list_nbest_first_subject);
//
//        ArrayList<String> true_subject = classificator.getTrue_subject();
//        ArrayList<String> pred_subject = classificator.getPred_subject();
//
//        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
//        confusionMatrix.initMatrix(excel_matrix_write, true_subject, pred_subject);
//        confusionMatrix.fillingMatrix(excel_matrix_write, list_pred_subject, new ArrayList<String>(list_nbest_first_subject), true_subject, pred_subject);
//    }
}

