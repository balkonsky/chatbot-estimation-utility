package ru.yota.estimationutility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class StartApp {
    private static String excel_file_read;
    private static String excel_file_write;
    private static String excel_matrix_write;
    private static String host;
    private static String url;
    private static int port;
    private final static Logger log = LogManager.getLogger(StartApp.class);

    public static void main(String[] args) {
        excel_file_read = args[0];
        excel_file_write = args[1];
        excel_matrix_write = args[2];
        host = args[3];
        url = args[4];
        port = Integer.parseInt(args[5]);
        log.trace("Application is started");
        log.debug("Parameters received: test set read location = {}, reporting file location = {}, matrix file location = {}, host = {}, url = {}, port = {},",
                excel_file_read, excel_file_write, excel_matrix_write, host, url, port);

        Classificator classificator = new Classificator();
        ArrayList<String> list_pred_phrase = classificator.readXlsx_phrase(excel_file_read);
        ArrayList<String> list_pred_subject = classificator.readXlsx_subject(excel_file_read);
        ArrayList<String> list_nbest_first_subject = classificator.classifyPhrase(host, port, url, excel_file_write, list_pred_subject, list_pred_phrase);

               /* будущий задел под реализацию многопоточности
            int part = list_pred_phrase.size() / 4;
            final List<String> list_pred_phrase_first_part = list_pred_phrase.subList(0, part);
            final List<String> list_pred_phrase_second_part = list_pred_phrase.subList(part, part + part);
            final List<String> list_pred_phrase_third_part = list_pred_phrase.subList(part + part, part + part + part);
            final List<String> list_pred_phrase_fourth_part = list_pred_phrase.subList(part + part + part, list_pred_phrase.size());

            final List<String> list_pred_subject_first_part = list_pred_subject.subList(0, part);
            final List<String> list_pred_subject_second_part = list_pred_subject.subList(part, part + part);
            final List<String> list_pred_subject_third_part = list_pred_subject.subList(part + part, part + part + part);
            final List<String> list_pred_subject_fourth_part = list_pred_subject.subList(part + part + part, list_pred_phrase.size());

            Thread firtst_thread = new Threads() {
                @Override
                public void run() {
                    ArrayList<String> list_nbest_first_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_first_part), new ArrayList<String>(list_pred_phrase_first_part));
                    list_nbest_first_subject.addAll(list_nbest_first_part_subject);
                }
            };
            firtst_thread.start();

            Thread second_thread = new Threads() {
                @Override
                public void run() {
                    ArrayList<String> list_nbest_second_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_second_part), new ArrayList<String>(list_pred_phrase_second_part));
                    list_nbest_first_subject.addAll(list_nbest_second_part_subject);
                }
            };
            second_thread.start();

            Thread third_thread = new Threads() {
                @Override
                public void run() {
                    ArrayList<String> list_nbest_third_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_third_part), new ArrayList<String>(list_pred_phrase_third_part));
                    list_nbest_first_subject.addAll(list_nbest_third_part_subject);
                }
            };
            third_thread.start();

            ArrayList<String> list_nbest_fourth_part_subject = classificator.classifyPhrase(host, port, url, excel_file_write, new ArrayList<String>(list_pred_subject_fourth_part), new ArrayList<String>(list_pred_phrase_fourth_part));
            list_nbest_first_subject.addAll(list_nbest_fourth_part_subject);

        }

        for (String s :list_nbest_first_subject) {
            System.out.println(s);
        }

        //classificator.writeXlsx(excel_file_write, list_subject, list_phrase, list_first_subject, list_first_confidence, list_second_subject, list_second_confidence, list_third_subject, list_third_confidence);
*/


        classificator.reporting(list_pred_subject, list_nbest_first_subject);

        ArrayList<String> true_subject = classificator.getTrue_subject();
        ArrayList<String> pred_subject = classificator.getPred_subject();

        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
        confusionMatrix.initMatrix(excel_matrix_write, true_subject, pred_subject);
        confusionMatrix.fillingMatrix(excel_matrix_write, list_pred_subject, new ArrayList<String>(list_nbest_first_subject), true_subject, pred_subject);
    }
}

