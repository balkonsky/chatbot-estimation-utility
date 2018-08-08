import ru.yota.estimationutility.utils.Configuration;

public class TestConfiguration {

    public static void main(String[] args) {
    try {
        System.out.println("Test Configuration Class...");
        System.out.println("Classification Protocol: " + new Configuration().getClassificatorProtocol());
        System.out.println("Classification Host: " + new Configuration().getClassificatorHost());
        System.out.println("Classification Port:  " + new Configuration().getClassificatorPort());
        System.out.println("Classification URL: " + new Configuration().getClassificatorURL());
        System.out.println("Classification Query String Parameter: " + new Configuration().getClassificatorQueryStringParameter());

        System.out.println("Excel Input True File Read Location: " + new Configuration().getExcelInputTrueFileReadLocation());
        System.out.println("Excel Input True Sheet Name: " + new Configuration().getExcelInputTrueSheetName());
        System.out.println("Excel Output Report File Location: " + new Configuration().getExcelOutputReportFileLocation());
    }
    catch (Exception e ){
        e.printStackTrace();
    }
}
}
