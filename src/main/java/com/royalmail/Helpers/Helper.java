package com.royalmail.Helpers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helper extends PageInstance {

   public static void runBatch(String Command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("cmd /c start /B "+Command);
        p.waitFor();
    }
    public static void runBatch(Path Command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("cmd /c start /B "+Command);
        p.waitFor();
    }
    public static void createBat(Path Path , List<String> Command) {
      try {
            Files.write(Path.toAbsolutePath(), Command);
            System.out.println("path "+Path.toAbsolutePath());
            runBatch(Path.toAbsolutePath());
        }catch (IOException | InterruptedException ignored){
        }
    }

    public static void createHtmlReport() throws IOException {
        int i=0;
        StringBuilder htmlStringBuilder=new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        htmlStringBuilder.append("<html><head><title>Environment HeartBeat Hourly Report</title></head>");
        htmlStringBuilder.append("<h1 align=\"center\">Environment Heartbeat Report</h1>");
        htmlStringBuilder.append("<body bgcolor=\"white\">");
        htmlStringBuilder.append("<table border=\"1\" align=\"center\">");
        htmlStringBuilder.append("<tr>");
        htmlStringBuilder.append("<td>Scenarios</td>");
        htmlStringBuilder.append("<td> Date : ");
        htmlStringBuilder.append(formatter.format(date));
        htmlStringBuilder.append("</td>");
        htmlStringBuilder.append("</tr>");

        htmlStringBuilder.append("<tr>");
        htmlStringBuilder.append("<td>");
        htmlStringBuilder.append("Preadvice Generation");
        htmlStringBuilder.append("</td>");
        update_HtmlReport(htmlStringBuilder,"Preadvice Generation");
        htmlStringBuilder.append("</tr>");

        htmlStringBuilder.append("<tr>");
        htmlStringBuilder.append("<td>");
        htmlStringBuilder.append("Acceptance Scan");
        htmlStringBuilder.append("</td>");
        update_HtmlReport(htmlStringBuilder,"Acceptance Scan");
        htmlStringBuilder.append("</tr>");


        htmlStringBuilder.append("<tr>");
        htmlStringBuilder.append("<td>");
        htmlStringBuilder.append("Other Scan");
        htmlStringBuilder.append("</td>");
        update_HtmlReport(htmlStringBuilder,"Other Scan");
        htmlStringBuilder.append("</tr>");

        htmlStringBuilder.append("<tr>");
        htmlStringBuilder.append("<td>");
        htmlStringBuilder.append("Validate Redland");
        htmlStringBuilder.append("</td>");
        update_HtmlReport(htmlStringBuilder,"Validate Redland");
        htmlStringBuilder.append("</tr>");

        System.out.println("getReports "+getReports);


        htmlStringBuilder.append("</td>");
        htmlStringBuilder.append("</tr>");
//append row
        htmlStringBuilder.append("");
//close html file
        htmlStringBuilder.append(" </table></body></html>");
//write html string content to a file
        i++;
        String a="t.html";

        WriteToFile(htmlStringBuilder.toString(),a);
    }
    public static void WriteToFile(String fileContent, String fileName) throws IOException {

        String projectPath = System.getProperty("user.dir");

        String tempFile = projectPath + File.separator+fileName;

        System.out.println(tempFile);
        File file = new File(tempFile);
// if file does exists, then delete and create a new file
        if (file.exists()) {
            try {
                File newFileName = new File(projectPath + File.separator+ "backup_"+fileName);
                file.renameTo(newFileName);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//write to file with OutputStreamWriter
        OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
        Writer writer=new OutputStreamWriter(outputStream);
        writer.write(fileContent);
        writer.close();

    }

    public static void update_HtmlReport(StringBuilder htmlStringBuilder, String jobDetails) {
        htmlStringBuilder.append("<td>");
        htmlStringBuilder.append("<table border=\"1\">");
        Iterator it = getReports.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            String Time=pair.getKey().toString().split("(?<=\\D)(?=\\d)")[1];

            if(pair.getKey().toString().contains("Create and") && jobDetails.contains("Preadvice Generation")){
                htmlStringBuilder.append("<tr>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(pair.getKey());
                htmlStringBuilder.append("</td>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(Time);
                htmlStringBuilder.append("</td>");
//                htmlStringBuilder.append("<td>");
//                htmlStringBuilder.append("<object width=\"200\" height=\"200\" data=\"cucumber-report/").append(pair.getKey().toString()).append("\"> \n").append("</object>");
//                htmlStringBuilder.append("</td>");
                if (pair.getValue().toString().equals("passed")) {
                    htmlStringBuilder.append("<td bgcolor=\"green\">");
                }else{
                    htmlStringBuilder.append("<td bgcolor=\"red\">");
                }
                htmlStringBuilder.append(pair.getValue().toString().toUpperCase());

            }else  if(pair.getKey().toString().contains("Generate MPER") && jobDetails.contains("Acceptance Scan")){
                htmlStringBuilder.append("<tr>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(pair.getKey());
                htmlStringBuilder.append("</td>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(Time);
                htmlStringBuilder.append("</td>");
//                htmlStringBuilder.append("<td>");
//                htmlStringBuilder.append("<object width=\"200\" height=\"200\" data=\"cucumber-report/").append(pair.getKey().toString()).append("\"> \n").append("</object>");
//                htmlStringBuilder.append("</td>");
                if (pair.getValue().toString().equals("passed")) {
                    htmlStringBuilder.append("<td bgcolor=\"green\">");
                }else{
                    htmlStringBuilder.append("<td bgcolor=\"red\">");
                }
                htmlStringBuilder.append(pair.getValue().toString().toUpperCase());

            }else  if(pair.getKey().toString().contains("Update MPER") && jobDetails.contains("Other Scan")){
                htmlStringBuilder.append("<tr>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(pair.getKey());
                htmlStringBuilder.append("</td>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(Time);
                htmlStringBuilder.append("</td>");
//                htmlStringBuilder.append("<td>");
//                htmlStringBuilder.append("<object width=\"200\" height=\"200\" data=\"cucumber-report/").append(pair.getKey().toString()).append("\"> \n").append("</object>");
//                htmlStringBuilder.append("</td>");
                if (pair.getValue().toString().equals("passed")) {
                    htmlStringBuilder.append("<td bgcolor=\"green\">");
                }else{
                    htmlStringBuilder.append("<td bgcolor=\"red\">");
                }
                htmlStringBuilder.append(pair.getValue().toString().toUpperCase());
            }else  if(pair.getKey().toString().contains("Validate ") && jobDetails.contains("Validate Redland")){
                htmlStringBuilder.append("<tr>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(pair.getKey());
                htmlStringBuilder.append("</td>");
                htmlStringBuilder.append("<td>");
                htmlStringBuilder.append(Time);
                htmlStringBuilder.append("</td>");
//                htmlStringBuilder.append("<td>");
//                htmlStringBuilder.append("<object width=\"200\" height=\"200\" data=\"cucumber-report/").append(pair.getKey().toString()).append("\"> \n").append("</object>");
//                htmlStringBuilder.append("</td>");
                if (pair.getValue().toString().equals("passed")) {
                    htmlStringBuilder.append("<td bgcolor=\"green\">");
                }else{
                    htmlStringBuilder.append("<td bgcolor=\"red\">");
                }
                htmlStringBuilder.append(pair.getValue().toString().toUpperCase());
            }
           // it.remove();
        }
        htmlStringBuilder.append("</td></table>");
    }

}
