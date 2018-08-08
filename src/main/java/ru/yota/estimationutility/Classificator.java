package ru.yota.estimationutility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yota.estimationutility.utils.Configuration;
import ru.yota.estimationutility.utils.HttpRequester;
import ru.yota.estimationutility.utils.JsonToStringParser;

import java.io.BufferedReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Classificator {
    private String httpresponse = null;

    ArrayList<String> true_subject = new ArrayList();
    ArrayList<String> pred_subject = new ArrayList();
    private final Logger log = LogManager.getLogger(Classificator.class);
    private HttpRequester httpRequester = null;
    private JsonToStringParser jsonParser = null;
    private Configuration config = new Configuration();

    private ArrayList<String> list_first_subject = new ArrayList();
    private ArrayList<String> list_first_confidence = new ArrayList();
    private ArrayList<String> list_second_subject = new ArrayList();
    private ArrayList<String> list_second_confidence = new ArrayList();
    private ArrayList<String> list_third_subject = new ArrayList();
    private ArrayList<String> list_third_confidence = new ArrayList();

    private Set<String> setpredsubjects = null;
    private Set<String> setturesubjects = null;

    private ArrayList<String> sortlistuniquetruesubjects = null;
    private ArrayList<String> sortlistuniquepredsubjects = null;


    public void classifyPhrase(ArrayList<String> truelistsubject, ArrayList<String> truelistphrase) {
        log.trace("classify phrases...");

        try {
            if (truelistphrase.size() == truelistsubject.size()) {
                httpRequester = new HttpRequester(
                        config.getClassificatorProtocol(),
                        config.getClassificatorHost(),
                        config.getClassificatorPort(),
                        config.getClassificatorURL(),
                        config.getClassificatorQueryStringParameter());

                for (String truephrase : truelistphrase) {
                    httpresponse = httpRequester.HTTPGetClassificator(truephrase);
                    jsonParser = new JsonToStringParser();
                    list_first_subject.add(jsonParser.parsePredSubjects(httpresponse, 0));
                    list_first_confidence.add(jsonParser.parsePredConfidence(httpresponse, 0));
                    list_second_subject.add(jsonParser.parsePredSubjects(httpresponse, 1));
                    list_second_confidence.add(jsonParser.parsePredConfidence(httpresponse, 1));
                    list_third_subject.add(jsonParser.parsePredSubjects(httpresponse, 2));
                    list_third_confidence.add(jsonParser.parsePredConfidence(httpresponse, 2));
                }
            } else {
                throw new NullPointerException("True Subject not equal True Phrase");
            }
        } catch (Exception e) {
            log.error("Error classify ", e);
            System.exit(0);
        }
    }

    public ArrayList<String> getPredFirstSubjectList() throws Exception {
        if (!list_first_subject.equals(null)) {
            return list_first_subject;
        } else {
            throw new NullPointerException("list of prediction first subjects are null");
        }
    }

    public ArrayList<String> getPredFirstConfidenceList() throws Exception {
        if (!list_first_confidence.equals(null)) {
            return list_first_confidence;
        } else {
            throw new NullPointerException("list of prediction first confidence are null");
        }
    }

    public ArrayList<String> getPredSecondSubjectList() throws Exception {
        if (!list_second_confidence.equals(null)) {
            return list_second_subject;
        } else {
            throw new NullPointerException("list of prediction second subjects are null");
        }
    }

    public ArrayList<String> getPredSecondConfidenceList() throws Exception {
        if (!list_second_confidence.equals(null)) {
            return list_second_confidence;
        } else {
            throw new NullPointerException("list of prediction second confidence are null");
        }
    }

    public ArrayList<String> getPredThirdSubject() throws Exception {
        if (!list_third_subject.equals(null)) {
            return list_third_subject;
        } else {
            throw new NullPointerException("list of prediction third subjects are null");
        }
    }

    public ArrayList<String> getPredThirdConfidenceList() throws Exception {
        if (!list_third_confidence.equals(null)) {
            return list_third_confidence;
        } else {
            throw new NullPointerException("list of prediction third confidence are null");
        }
    }

    public ArrayList<String> getuSortUiqueTrueSubjects(ArrayList<String> truelistsubject) {
        setturesubjects = new HashSet<String>(truelistsubject);
        sortlistuniquetruesubjects = new ArrayList<String>(setturesubjects);
        Collections.sort(sortlistuniquetruesubjects);
        return sortlistuniquetruesubjects;
    }

    public ArrayList<String> getSortUniquePredSubjects(ArrayList<String> predlistsubjects) {
        setpredsubjects = new HashSet<String>(predlistsubjects);
        sortlistuniquepredsubjects = new ArrayList<String>(setpredsubjects);
        Collections.sort(sortlistuniquepredsubjects);
        return sortlistuniquepredsubjects;
    }
}
