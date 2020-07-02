package com.royalmail.TestUtils;
import com.royalmail.Helpers.PageInstance;
import  com.royalmail.Helpers.Helper;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.royalmail.Helpers.FileManipulation.createJsonForReports;


@RunWith(Cucumber.class)
@CucumberOptions(
        glue =      {"com.royalmail"},
        features =  {"src/test/resources/features"},
        plugin =    {"com.cucumber.listener.ExtentCucumberFormatter:target/cucumber-reports/report.html"},
        tags =      {"@E2EScenario_001"})
public class TestRunner extends PageInstance {
    private static String timestamp = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

    @BeforeClass
    public static void setup() {
    }
    @AfterClass
    public static void writeExtentReport() throws IOException, ParseException {
        File source = new File("target/cucumber-reports/report.html");
        String fileName=scenario.getName()+timestamp+".html";
        File dest = new File("cucumber-report/"+fileName);
          if (scenario.getName().contains("Generate MPER")){
           try {
               FileUtils.copyFile(source,dest);
               createJsonForReports(fileName.replace(".html",""),scenario.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
            }
            }else if(scenario.getName().contains("Update MPER")){
            try {
                FileUtils.copyFile(source,dest);
                createJsonForReports(fileName.replace(".html",""),scenario.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(scenario.getName().contains("Create and")){
              try {
                FileUtils.copyFile(source,dest);
                  createJsonForReports(fileName.replace(".html",""),scenario.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Helper.createHtmlReport();
    }
}
