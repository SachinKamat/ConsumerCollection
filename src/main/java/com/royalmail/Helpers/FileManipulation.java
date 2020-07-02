package com.royalmail.Helpers;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.royalmail.Helpers.Helper.createBat;
import static com.royalmail.Helpers.Helper.runBatch;


public class FileManipulation extends PageInstance{

    static final Path CSV_TEMPLATE_PATH = Paths.get("src/test/resources/Scripts/_CR595_PDA_MPER_0308151107.csv");

    public static String readFile(int index){
        try
        {
            FileInputStream file = new FileInputStream(new File("Barcode_List.xlsx"));
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(index);
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            for(int i=0;i<1;i++){
              Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    switch (cell.getCellType())
                    {
                        case Cell.CELL_TYPE_NUMERIC:
                           return String.valueOf(cell.getNumericCellValue());
                        case Cell.CELL_TYPE_STRING:
                           return cell.getStringCellValue();

                    }
                }
            }
            file.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void removeRow(int index) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new FileInputStream("Barcode_List.xlsx"));
        Sheet worksheet = workbook.getSheetAt(index);
        worksheet.shiftRows(1, worksheet.getLastRowNum(), -1);
        workbook.write(new FileOutputStream("Barcode_List.xlsx"));
        workbook.close();
    }
    public static Boolean createCSV() throws FileNotFoundException {

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter PreAdvice_Date = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        File csvOutputFile = new File("COSS01G4_PreAdvice3_"+ZonedDateTime.now().format(PreAdvice_Date).replaceAll("\\.","").replaceAll(":","")+".csv");
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]
                { "\"00\"","\"03\"","\"RMBS\"","\"0368482000\"","\"MULTIPLE\"","\"451\"","\"\"","\"\"","\"\"","\"LIVE\"","\""+ ZonedDateTime.now().format(FORMATTER)+"+00:00\"","\"01G4\"","\"\"","\"09\"" });
        dataLines.add(new String[]
                { "\"01\"","\"03\"","\"HEARTBEAT\"","\"34 PARK ROAD\"","\"\"","\"\"","\"\"","\"\"","\"CHELMSFORD\"","\"CM1 2DW\"","\"PHASE 3 F POSTER\"","\"123456789012\"","\"\"","\"\"","\"\"","\"royalmail.support@neopost.co.uk\"","\"002599\"","\"9000240524\"" });
        dataLines.add(new String[]
                { "\"02\"","\"03\"","\"\"","\"TPN01\"","\"\"","\"\"","\"\"","\"HEARTBEAT\"","\"\"","\"\"","\"100\"","\"1\"","\"\"","\"HEARTBEAT\"","\"9\"","\"SPRINGFIELD PARK ROAD\"","\"\"","\"Enniskillen\"","\"BT744AE1A\"","\"Inflight_TESTDATA2\"","\"5\"","\"\"","\"\"","\"\"","\"GB\"","\"\"","\""+UID+"\"","\"100\"","\"1\"","\"\"","\"9\"","\"\"","\"3\"","\"\"","\"\"","\"\"","\"\"","\""+BARCODE+"\"","\"0\"","\""+ZonedDateTime.now().format(DATE)+"\"","\""+ZonedDateTime.now().format(DATE)+"\"","\"\"","\"\"","\"GBR\"","\"461765TN\"","\"HEARTBEAT\"","\"\"","\"\"","\"\"" });
        dataLines.add(new String[]
                { "\"03\"","\"03\"","\""+UID+"\"","\"98\"","\"07448017658\"" });
        dataLines.add(new String[]
                { "\"03\"","\"03\"","\""+UID+"\"","\"97\"","\"sachin.kamat@royalmail.com\"" });
        dataLines.add(new String[]
                { "\"09\"","\"03\"","\"06\"" });
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(FileManipulation::convertToCSV)
                    .forEach(pw::println);
        }
        return true;
    }

    public static Boolean processCSV() throws IOException, InterruptedException {
        createBat(BAT_Preadvice,Bat_Preadvice_list);
        return true;
    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(FileManipulation::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }
    private static String escapeSpecialCharacters(String data) {
        return data.replaceAll("\\R", " ");
    }

    public static Boolean readAndValidateLog() throws IOException {
        String content = new String(Files.readAllBytes(WINSCP_LOG), "UTF-8");
        byte[] empty = new byte[0];
        File file = new File(String.valueOf(WINSCP_LOG.toAbsolutePath()));
        com.google.common.io.Files.write(empty, file);
        return content.contains("transferred");
    }

    public static Boolean createJsonFile() throws IOException, ParseException, InvalidFormatException {
        JSONParser jsonParser = new JSONParser();
        File newFile = new File(String.valueOf(Json_Event_file.toAbsolutePath()));
        if(newFile.length() != 0) {
            Object obj = jsonParser.parse(new FileReader(String.valueOf(Json_Event_file.toAbsolutePath())));
            BarcodeList = (JSONArray) obj;
            System.out.println(BarcodeList);
        }
        JSONObject BarcodeDetails = new JSONObject();
        BarcodeDetails.put("barcode",BARCODE);
        BarcodeDetails.put("Event","");

        BarcodeList.add(BarcodeDetails);
        try (FileWriter file = new FileWriter(String.valueOf(Json_Event_file.toAbsolutePath()))) {
            file.write(String.valueOf(BarcodeList));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        removeRow(0);
        removeRow(1);
        return true;
    }
    public static Boolean updateJsonFile(String Barcode,String Event) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(new FileReader(String.valueOf(Json_Event_file.toAbsolutePath())));
        BarcodeList = (JSONArray) obj;
        for (Object o : BarcodeList) {
            JSONObject itemArr = (JSONObject) o;
            if (itemArr.get("barcode").equals(Barcode)) {
                itemArr.put("Event", Event);
            }
        }
        try (FileWriter file = new FileWriter(String.valueOf(Json_Event_file.toAbsolutePath()))) {
            file.write(String.valueOf(BarcodeList));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static HashMap<String,String> readExcel(String fileName,int rownum) throws IOException {
        HashMap<String,String>getMap= new HashMap<String,String>();
        File src=new File(fileName);
        FileInputStream fis=new FileInputStream(src);
        XSSFWorkbook srcBook= new XSSFWorkbook(fis);
        XSSFSheet sourceSheet = srcBook.getSheetAt(2);
        XSSFRow sourceRow = sourceSheet.getRow(rownum);
        System.out.println("source row "+sourceRow);
        getMap.put("Option",sourceRow.getCell(0).getStringCellValue());
        getMap.put("Barcode",sourceRow.getCell(1).getStringCellValue());
        getMap.put("1DBarcode",sourceRow.getCell(2).getStringCellValue());
        getMap.put("PostCode",sourceRow.getCell(3).getStringCellValue());
        getMap.put("ActionDate",sourceRow.getCell(4).getStringCellValue());
        System.out.println("getMap "+getMap);
        //2019-11-28
        return getMap;
    }
    public static Boolean PerformScan(String Event){
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String generateMPER = "cmd.exe /C " + GAWK_PATH + " -v TESTBARCODE=" + BARCODE + " -v TESTEVENT=" + Event + " -v TESTDATE=" + ZonedDateTime.now().format(FORMATTER)+"Z" + " -v LOCATIONID=002599 -v XMLPATH=" + XML_PATH + " -f " + PTP_PATH + " <" + CSV_TEMPLATE_PATH;
        try {
            runBatch(generateMPER);
            updateInputFile();
            createBat(BAT_FILE,Bat_list);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
       return true;
    }
    public static Boolean PerformScan(HashMap<String,String> SCANDETAILS){
        System.out.println("Scan details "+SCANDETAILS);
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String generateMPER = "cmd.exe /C " + GAWK_PATH + " -v TESTBARCODE=" + SCANDETAILS.get("BARCODE") + " -v TESTEVENT=" + SCANDETAILS.get("EVENT") + " -v TESTDATE=" + ZonedDateTime.now().format(FORMATTER)+"Z" + " -v LOCATIONID="+SCANDETAILS.get("LOCATION")+" -v XMLPATH=" + XML_PATH + " -f " + PTP_PATH + " <" + CSV_TEMPLATE_PATH;
        try {
            runBatch(generateMPER);
            updateInputFile();
            createBat(BAT_FILE,Bat_list);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public static String getBarcodeFromJSON() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(new FileReader(String.valueOf(Json_Event_file.toAbsolutePath())));
        BarcodeList = (JSONArray) obj;
        JSONObject itemArr = (JSONObject)BarcodeList.get(0);
        return itemArr.get("barcode").toString();
    }

    public static String getEventFromJSON(){
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try {
            obj = jsonParser.parse(new FileReader(String.valueOf(Json_Event_file.toAbsolutePath())));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        BarcodeList = (JSONArray) obj;
        System.out.println("Barcode List before processing event "+BarcodeList);
        JSONObject itemArr = (JSONObject)BarcodeList.get(0);
        return itemArr.get("Event").toString();
    }

    public static void removeJsonObject() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(new FileReader(String.valueOf(Json_Event_file.toAbsolutePath())));
        BarcodeList = (JSONArray) obj;
        System.out.println("Barcodelist "+BarcodeList);
        BarcodeList.remove(0);
        try (FileWriter file = new FileWriter(String.valueOf(Json_Event_file.toAbsolutePath()))) {
            file.write(String.valueOf(BarcodeList));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createJsonForReports(String scenario_name, String status) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        File newFile = new File(String.valueOf(Json_Report_file.toAbsolutePath()));
        if(newFile.length() != 0) {
            Object obj = jsonParser.parse(new FileReader(String.valueOf(Json_Report_file.toAbsolutePath())));
            ScenarioList = (JSONArray) obj;
            System.out.println(ScenarioList);
        }
        JSONObject ScenarioDetails = new JSONObject();
        ScenarioDetails.put(scenario_name,status);
        ScenarioList.add(ScenarioDetails);
        try (FileWriter file = new FileWriter(String.valueOf(Json_Report_file.toAbsolutePath()))) {
            file.write(String.valueOf(ScenarioList));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getReportName(){
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try {
            obj = jsonParser.parse(new FileReader(String.valueOf(Json_Report_file.toAbsolutePath())));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        ScenarioList = (JSONArray) obj;
        String argument[]=null;
        for(int i=0;i<ScenarioList.size();i++){
            System.out.println(ScenarioList.get(i));
            argument=ScenarioList.get(i).toString().split(":");
            getReports.put(argument[0].replace("{","").replace("\"",""),argument[1].replace("\"","").replace("}",""));
        }
    }

}
