package com.royalmail.Helpers;

import java.io.*;
import java.util.Properties;

public class ConfigFileReader {
    Properties properties=new Properties();
    private final String propertyFilePath= "config.properties";
    public ConfigFileReader(){
        BufferedReader reader;
        try {
            InputStream input = new FileInputStream(propertyFilePath);
            properties = new Properties();
            try {
                properties.load(input);
                } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
        }
    }
    public String getReportConfigPath(){
        String reportConfigPath = properties.getProperty("reportConfigPath");
        if(reportConfigPath!= null) return reportConfigPath;
        else throw new RuntimeException("Report Config Path not specified in the Configuration.properties file for the Key:reportConfigPath");
    }
}
