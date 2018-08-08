import ru.yota.estimationutility.Classificator;
import ru.yota.estimationutility.utils.Configuration;
import ru.yota.estimationutility.utils.ExcelWriter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestExcelWriter {
    private static Classificator classificator = new Classificator();
    private static ExcelWriter excelWriter = null;
    private static Configuration configuration = new Configuration();
    private static ArrayList<String> truelistsubject = new ArrayList<String>();
    private static ArrayList<String> truelistphrase = new ArrayList<String>();

    private static ArrayList<String> predlistfirstsubject = new ArrayList<String>();
    private static ArrayList<String> predlistfirstconfidence = new ArrayList<String>();
    private static ArrayList<String> predlistsecondsubject = new ArrayList<String>();
    private static ArrayList<String> predlistsecondconfidence = new ArrayList<String>();
    private static ArrayList<String> predlistthirdsubject = new ArrayList<String>();
    private static ArrayList<String> predlistthirdconfidence = new ArrayList<String>();


    public static void main(String[] args) {

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("C:/estimation-utility-main-master/src/test/resources/testset.txt"), "UTF-8"));

            //FileInputStream fis = new FileInputStream(new File("C:/estimation-utility-main-master/src/test/resources/testset.txt"));

            int l;
            StringBuilder sb = new StringBuilder();
            while ((l = reader.read()) != -1) {
                if (!(Character.toString((char) l)).equals("\n")) {
                    sb.append(Character.toString((char) l));
                }
                if ((Character.toString((char) l)).equals("\n")) {
                    Pattern p_subj = Pattern.compile("^\\S+");
                    Matcher m_subj = p_subj.matcher(new String(sb));
                    while (m_subj.find()) {
                        truelistsubject.add(m_subj.group());
                    }
                    truelistphrase.add(new String(sb).substring(7));
                    sb = new StringBuilder();
                }
            }

            classificator.classifyPhrase(truelistsubject, truelistphrase);

            predlistfirstsubject = classificator.getPredFirstSubjectList();
            predlistfirstconfidence = classificator.getPredFirstConfidenceList();

            predlistsecondsubject = classificator.getPredSecondSubjectList();
            predlistsecondconfidence = classificator.getPredSecondConfidenceList();

            predlistthirdsubject = classificator.getPredThirdSubject();
            predlistthirdconfidence = classificator.getPredThirdConfidenceList();

            excelWriter = new ExcelWriter(configuration.getExcelOutputReportFileLocation());

            excelWriter.writeToExcelFile(
                    truelistsubject,
                    truelistphrase,
                    predlistfirstsubject,
                    predlistfirstconfidence,
                    predlistsecondsubject,
                    predlistsecondconfidence,
                    predlistthirdsubject,
                    predlistthirdconfidence
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
