package ru.yota.estimationutility.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configuration {
    private Config config = ConfigFactory.load("app.conf");

    public String getClassificatorProtocol() throws Exception {
        return config.getString("chatbot-estimation-utility-config.classificator.protocol");
    }

    public String getClassificatorHost() throws Exception {
        return config.getString("chatbot-estimation-utility-config.classificator.host");
    }

    public int getClassificatorPort() throws Exception {
        return config.getInt("chatbot-estimation-utility-config.classificator.port");
    }

    public String getClassificatorURL() throws Exception {
        return config.getString("chatbot-estimation-utility-config.classificator.url");
    }

    public String getClassificatorQueryStringParameter() throws Exception {
        return config.getString("chatbot-estimation-utility-config.classificator.querystringparameter");
    }

    public String getExcelInputTrueFileReadLocation() throws Exception {
        return config.getString("chatbot-estimation-utility-config.excelfile.inputtruefilereadlocation");
    }

    public String getExcelOutputReportFileLocation() throws Exception {
        return config.getString("chatbot-estimation-utility-config.excelfile.outputreportfilelocation");
    }
    public String getExcelInputTrueSheetName() throws Exception{
        return config.getString("chatbot-estimation-utility-config.excelfile.inputtruesheetname");
    }

}
