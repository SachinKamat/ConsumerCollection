package com.royalmail.StepDefinition;

import com.google.gson.GsonBuilder;
import com.royalmail.Helpers.PageInstance;
import com.royalmail.Helpers.SeleniumTests;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.mapper.factory.GsonObjectMapperFactory;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import com.royalmail.Helpers.FileManipulation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.royalmail.Helpers.FileManipulation.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class MyStepdefs extends PageInstance {
    private Response response;
    private ValidatableResponse json;

    private SeleniumTests seleniumTests=new SeleniumTests();

    public MyStepdefs() throws FileNotFoundException {
    }

    @Before()
    public void beforeScenario(Scenario scenario) {
         PageInstance.scenario =scenario;
    }

    @Given("^I read the barcode from the TestDataSheet$")
    public void iReadTheBarcodeFromTheTestDataSheet() throws IOException, InvalidFormatException {
        Boolean isValidBarcode = ValidateBarcode();
        Assert.assertEquals(true, isValidBarcode);
        Boolean isValidUID = ValidateUID();
        Assert.assertEquals(true, isValidUID);
    }

    @And("^I process the barcode in BIG Queue via WINSCP$")
    public void iProcessTheBarcodeInBIGQueueViaWINSCP() throws IOException, InterruptedException {
        Boolean isCSVCreatedSuccess = createCSV();
        Assert.assertEquals(true, isCSVCreatedSuccess);
        Boolean isCSVFileProcessedSuccess = processCSV();
        Assert.assertEquals(true, isCSVFileProcessedSuccess);
    }

    @When("^COSS file is successfully processed from the BIG Queue$")
    public void csvFileIsSuccessfullyProcessedFromTheBIGQueue() throws IOException, ParseException, InvalidFormatException {
        Boolean isLogFileValidated = readAndValidateLog();
        //Assert.assertEquals(true,isLogFileValidated);
        Boolean isBarcodeUpdatedInJson = createJsonFile();

    }

    @Then("^I validate the preadvice generation of Barcode in EPS$")
    public void iValidateTheeventGenerationOfBarcodeInEPS(Map<String, String> responseFields, String Barcode) {
        response = given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded").when().get("http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + Barcode + "/details");
        json = response.then().statusCode(200);
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            System.out.println("Key " + field.getKey() + " value " + field.getValue());
            if (StringUtils.isNumeric(field.getValue())) {
                json.body(field.getKey(), equalTo(Integer.parseInt(field.getValue())));
            } else {
                json.body(field.getKey(), equalTo(field.getValue()));
            }
        }
    }
    public Boolean ValidateBarcode() throws IOException, InvalidFormatException {
        try {
            BARCODE = FileManipulation.readFile(0);
            RestAssured.baseURI = "http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + BARCODE + "/details";
            RequestSpecification httpRequest = RestAssured.given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded");
            Response response = httpRequest.get();
            int statusCode = response.getStatusCode();
            if (statusCode == 404) {
                return true;
            } else {
                while (statusCode != 404) {
                    removeRow(0);
                    BARCODE = FileManipulation.readFile(0);
                    RestAssured.baseURI = "http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + BARCODE + "/details";
                    httpRequest = RestAssured.given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded");
                    response = httpRequest.get();
                    statusCode = response.getStatusCode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    @Then("^I validate the event generation for the barcode in EPS$")
    public void iValidateTheEventGenerationForTheBarcodeInEPS(Map<String, String> responseFields) throws IOException, ParseException {
        response = given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded").when().get("http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + BARCODE + "/details");
        json = response.then().statusCode(200);
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            ArrayList<Map<String, ?>> jsonAsArrayList = json.extract()
                    .jsonPath().get(field.getKey());
            Optional<Map<String, ?>> filtered = jsonAsArrayList.stream()
                    .filter(m -> m.get("eventCode").equals(field.getValue()))
                    .findFirst();
            updateJsonFile(BARCODE, field.getValue());
            Assert.assertTrue("Event generation is failed in EPS", filtered.isPresent());
        }
    }

    @Given("^I read the barcode from the Json Test Data$")
    public void iReadTheBarcodeFromTheJsonTestData() throws IOException, ParseException {
        BARCODE = getBarcodeFromJSON();
        System.out.println("Barcode "+BARCODE);
    }

    @And("^I wait for the scan to be performed on the barcode$")
    public void iWaitForTheScanToBePerformedOnTheBarcode() throws InterruptedException {
        Thread.sleep(12000);
    }

    @And("^I check for the scans performed on the barcode$")
    public void iCheckForTheScansPerformedOnTheBarcode() {
        Event=getEventFromJSON();
    }

    @When("^I Update and perform the scans on the barcode$")
    public void iUpdateAndPerformTheScansOnTheBarcode() {
        Boolean isScanPerformed=false;
        if(Event.equalsIgnoreCase("EVDAC")){
            isScanPerformed=PerformScan("EVGPD");
            Event="EVGPD";
        }else if(Event.equalsIgnoreCase("EVGPD")){
            isScanPerformed=PerformScan("EVKNA");
            Event="EVKNA";
        }else if(Event.equalsIgnoreCase("EVKNA")){
            isScanPerformed=PerformScan("EVNCE");
            Event="EVNCE";
        }else if(Event.equalsIgnoreCase("EVNCE")){
            isScanPerformed=PerformScan("EVKSP");
            Event="EVKSP";
        }
        Assert.assertEquals(true, isScanPerformed);
    }

    @Then("^I validate the event generation for the barcode in EPS for the scans performed$")
    public void iValidateTheEventGenerationForTheBarcodeInEPSForTheScansPerformed() throws IOException, ParseException {
        HashMap<String,String>MapToValidate=new HashMap<String,String>();
        MapToValidate.put("mailPieces.events",Event);
        iValidateTheEventGenerationForTheBarcodeInEPS(MapToValidate);
        iValidateTheEventGenerationForTheBarcodeInTAPI(MapToValidate);
    }

    @And("^I validate the event details for the Barcode in EPS$")
    public void iValidateTheEventDetailsForTheBarcodeInEPS(Map<String, String> responseFields) {
        response = given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded").when().get("http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + responseFields.get("mailPieces.mailPieceId") + "/details");
        json = response.then().statusCode(200);
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            System.out.println("Key " + field.getKey() + " value " + field.getValue());
            if (StringUtils.isNumeric(field.getValue())) {
                json.body(field.getKey(), equalTo(Integer.parseInt(field.getValue())));
            } else {
                json.body(field.getKey(), equalTo(field.getValue()));
            }
        }
    }

    @Given("^I read and assign the variables from the TestDataSheet to generate the coss file for preadvice$")
    public void iReadAndAssignTheVariablesFromTheTestDataSheetToGenerateTheCossFileForPreadvice() throws IOException, InvalidFormatException {

        Boolean isValidBarcode = ValidateBarcode();
        Assert.assertEquals(true, isValidBarcode);
        Boolean isValidUID = ValidateUID();
        Assert.assertEquals(true, isValidUID);

    }

    @Given("^I read and assign and process the preadvice from the TestDataSheet$")
    public void iReadAndAssignAndProcessThePreadviceFromTheTestDataSheet() throws IOException, InterruptedException {
        Boolean isPreadviceFileProcessed=readAndProcess("MultiplePreAdviceTestData.xlsx");
    }

    @Given("^I read and assign and process the Scans from the TestDataSheet$")
    public void iReadAndAssignAndProcessTheScansFromTheTestDataSheet() throws IOException, InterruptedException {
        processScansfromSheet("MultiplePreAdviceTestData.xlsx");
    }

    @Given("^I read and assign data from the TestDataSheet for the inflight options and scans$")
    public void iReadAndAssignDataFromTheTestDataSheetForTheInflightOptionsAndScans() throws IOException {
        inputStream = new FileInputStream(propFileName);
        prop.load(inputStream);
        HashMap<String,String>getMap= new HashMap<String,String>();
        File src=new File("InflightScan.xlsx");
        FileInputStream fis=new FileInputStream(src);
        XSSFWorkbook srcBook= new XSSFWorkbook(fis);
        XSSFSheet sourceSheet = srcBook.getSheetAt(0);

        XSSFSheet ScanSheet=srcBook.getSheetAt(1);
        XSSFRow FirstRow_scan =ScanSheet.getRow(0);

        XSSFSheet DeliveryChangeSheet=srcBook.getSheetAt(2);
        XSSFRow sourceRow = null;
        DataFormatter formatter = new DataFormatter();
        XSSFRow FirstRow =sourceSheet.getRow(0);
        int rowNum=0;
        DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MM/d/yy");
        for(int i=0;i<FirstRow_scan.getLastCellNum();i++){
            System.out.println("Date Value "+formatter.formatCellValue(FirstRow_scan.getCell(i)) + " equals "+ZonedDateTime.now().format(DATE));
            if(formatter.formatCellValue(FirstRow_scan.getCell(i)).equals(ZonedDateTime.now().format(DATE))){
                rowNum=i;
                System.out.println("RowNum "+rowNum);
                break;
            }
        }
        int rowCount_scan = ScanSheet.getLastRowNum();
        int rowCount_deliveryChange = DeliveryChangeSheet.getLastRowNum();
        for(int i=1;i<=sourceSheet.getLastRowNum();i++) {
            for(int j=0;j<FirstRow.getLastCellNum();j++){
                sourceRow = sourceSheet.getRow(i);
                getMap.put(formatter.formatCellValue(FirstRow.getCell(j)),formatter.formatCellValue(sourceRow.getCell(j)));
            }
            if(getMap.get("AcceptanceScan")!=null){
                System.out.println("getMap : "+getMap);
                Row row = ScanSheet.createRow(++rowCount_scan);
                int columnCount = 0;
                String Barcode = getMap.get("BARCODE");
                String LOCATIONID=getMap.get("LOCATIONID");
                String Scantype=getMap.get("AcceptanceScan");

                System.out.println(Barcode+":"+Scantype);
                Cell barcode=row.createCell(columnCount);
                barcode.setCellValue(Barcode);
                Cell locationID=row.createCell(++columnCount);
                locationID.setCellValue(LOCATIONID);
                Cell cell = row.createCell(rowNum);
                cell.setCellValue(Scantype);
            }
            if(getMap.get("OtherScan")!=null){
                Object Scantype = getMap.get("OtherScan");
                int rowNum_otherScan=0;
                if(!Scantype.toString().isEmpty()) {
                    String[] arr = Scantype.toString().split(";");
                    for (String s : arr) {
                        String []arrdate=s.split("\\(");
                        for(int k=0;k<FirstRow_scan.getLastCellNum();k++){
                            System.out.println("Date Value "+formatter.formatCellValue(FirstRow_scan.getCell(k)) + " equals "+arrdate[1].replace(")",""));
                            if(formatter.formatCellValue(FirstRow_scan.getCell(k)).equals(arrdate[1].replace(")",""))){
                                rowNum_otherScan=k;
                                System.out.println("RowNum "+rowNum_otherScan);
                                break;
                            }
                        }
                        Row row = ScanSheet.createRow(++rowCount_scan);
                        int columnCount = 0;
                        String Barcode = getMap.get("BARCODE");
                        String LOCATIONID = getMap.get("DESTINATIONPOSTTOWN");
                        System.out.println(Barcode + ":" + Scantype);
                        Cell barcode = row.createCell(columnCount);
                        barcode.setCellValue(Barcode);
                        Cell locationID = row.createCell(++columnCount);
                        locationID.setCellValue(prop.getProperty(LOCATIONID.toLowerCase()));
                        Cell cell = row.createCell(rowNum_otherScan);
                        cell.setCellValue(arrdate[0]);
                    }
                }
            }

            if(getMap.get("InflightOptions")!=null) {
                Row row = DeliveryChangeSheet.createRow(++rowCount_scan);
                int columnCount = 0;

                Object Scantype=getMap.get("InflightOptions");
                Cell cell = row.createCell(columnCount);
                cell.setCellValue((String)Scantype);

                Object uid = getMap.get("UID");
                Cell uid_cell=row.createCell(++columnCount);
                uid_cell.setCellValue((String)uid);

                Object Barcode = getMap.get("BARCODE");
                Cell barcode=row.createCell(++columnCount);
                barcode.setCellValue((String)Barcode);

                Object postcode = getMap.get("DESTINATIONPO");
                Cell postcode_cell=row.createCell(++columnCount);
                postcode_cell.setCellValue((String)postcode);

                Cell ActionDate_cell=row.createCell(++columnCount);
                ActionDate_cell.setCellValue((String)ZonedDateTime.now().format(DATE));

                Object InflightDetails = getMap.get("InflightDetails");
                Cell InflightDetails_cell=row.createCell(++columnCount);
                InflightDetails_cell.setCellValue((String)InflightDetails);

            }
        }
        fis.close();
        FileOutputStream outputStream = new FileOutputStream("InflightScan.xlsx");
        srcBook.write(outputStream);
        srcBook.close();
        outputStream.close();
    }

    @And("^I process the preadvice for the barcode in the Inflight data sheet$")
    public void iProcessThePreadviceForTheBarcodeInTheInflightDataSheet() throws IOException, InterruptedException {
        Boolean isPreadviceFileProcessed=readAndProcess("InflightScan.xlsx");
    }

    @Given("^I read the barcode and process the Scans from inflight data sheet$")
    public void iReadTheBarcodeAndProcessTheScansFromInflightDataSheet() throws IOException, InterruptedException {
        processScansfromSheet("InflightScan.xlsx");
    }

    @Given("^I read the barcode and process the Inflight options from inflight data sheet$")
    public void iReadTheBarcodeAndProcessTheInflightOptionsFromInflightDataSheet() throws IOException, JSONException {
       readAndProcessInflightScan();
    }



    @And("^I validate the event generation for the barcode in TAPI$")
    public void iValidateTheEventGenerationForTheBarcodeInTAPI(Map<String, String> responseFields) throws IOException, ParseException {
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json").when().get("https://test.api.royalmail.net/mailpieces/v2/"+BARCODE+"/events");
        json = response.then().statusCode(200);
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            ArrayList<Map<String, ?>> jsonAsArrayList = json.extract()
                    .jsonPath().get(field.getKey());
            Optional<Map<String, ?>> filtered = jsonAsArrayList.stream()
                    .filter(m -> m.get("eventCode").equals(field.getValue()))
                    .findFirst();
            updateJsonFile(BARCODE, Event);
            Assert.assertTrue("Event generation is failed in TAPI", filtered.isPresent());
        }
    }



    @Given("^I test the reporting for the Heartbeat$")
    public void iTestTheReportingForTheHeartbeat() {
        getReportName();
    }

    @And("^I open Redland site in the browser$")
    public void iOpenRedlandSiteInTheBrowser() {
          seleniumTests.setup();
    }

    @Then("^I validate the barcode details in the Redland reports for \"([^\"]*)\"$")
    public void iValidateTheBarcodeDetailsInTheRedlandReportsFor(String Scan) throws Throwable {
       for (Object o : BarcodeRedlandList) {
           JSONObject itemArr = (JSONObject) o;
           seleniumTests.inputBarcode(itemArr.get("barcode").toString());
           seleniumTests.validateBarcode(Scan);
       }
        seleniumTests.ExitSession();
    }

    @Given("^I read the barcode from the Json Test Data for REDLAND Verification$")
    public void iReadTheBarcodeFromTheJsonTestDataForREDLANDVerification() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(new FileReader(String.valueOf(Json_redland_file.toAbsolutePath())));
        BarcodeRedlandList = (JSONArray) obj;
    }

    @When("^I perform the Acceptance Scan on the Barcode$")
    public void iPerformTheAcceptanceScanOnTheBarcode() throws IOException, ParseException, InterruptedException {
        Thread.sleep(10000);
        Boolean isScanPerformed =false;
        Event=getEventFromJSON();
        if(!Event.equalsIgnoreCase("EVDAC")) {
            if (Event.equalsIgnoreCase("EVAIP") || Event.equalsIgnoreCase("")) {
                 Event = "EVDAC";
            }
            isScanPerformed = PerformScan(Event);
        }else {
            System.out.println("Acceptance scan already performed "+Event);
        }
    }
    private void readAndProcessInflightScan() throws JSONException, IOException {
        File src=new File("InflightScan.xlsx");
        FileInputStream fis=new FileInputStream(src);
        XSSFWorkbook srcBook= new XSSFWorkbook(fis);
        XSSFSheet sourceSheet = srcBook.getSheetAt(2);
        XSSFRow sourceRow = null;

        for(int i=1;i<=sourceSheet.getLastRowNum();i++) {
            sourceRow = sourceSheet.getRow(i);
            try {
                String Barcode=sourceRow.getCell(2).getStringCellValue();
                String Postcode=sourceRow.getCell(3).getStringCellValue();
                System.out.println(Barcode+" : "+Postcode);
                RequestSpecification request = RestAssured.given().proxy("10.223.0.50", 8080).headers("Origin", "*.royalmail.com", "X-IBM-Client-Id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "X-IBM-Client-Secret", "pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA", "Content-Type", "application/json");
                response = request.when().get("https://test.api.royalmail.net/delivery-change/v1/mailpieces/"+Barcode+"/options?postCode="+Postcode);
                json = response.then().statusCode(200);
                DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                org.apache.wink.json4j.JSONObject safeplacedetails = new OrderedJSONObject();
               Map<String,String> responseFields =readExcel("InflightScan.xlsx",i);
               System.out.println(" responseFields "+responseFields);
                safeplacedetails.put("locationCode","20006");
                safeplacedetails.put("locationText","Shed");
                safeplacedetails.put("additionalDetails","Next to Garage");


                org.apache.wink.json4j.JSONObject address = new OrderedJSONObject();
                address.put("buildingName","Building 1");
                //address.put("buildingNumber","1");
                address.put("addressLine1","addressLine1");
                address.put("addressLine2","addressLine2");
                address.put("addressLine3","addressLine3");
                //address.put("addressLine4","addressLine4");
                //address.put("addressLine5","addressLine5");
                //address.put("stateOrProvince","stateOrProvince");
                address.put("postTown","Harrow");
                address.put("county","Middlesex");
                address.put("postcode",responseFields.get("PostCode"));
                address.put("country","United Kingdom");


                org.apache.wink.json4j.JSONObject optionDetails = new OrderedJSONObject();
                optionDetails.put("requestedDateTime", ZonedDateTime.now().format(FORMATTER)+"T09:15:00Z");
                optionDetails.put("requestedActionDateTime",responseFields.get("ActionDate")+"T00:00:00Z");
                optionDetails.put("optionType",responseFields.get("Option"));
                optionDetails.put("updatedNotificationEmail","sapna.negi@royalmail.com");


                org.apache.wink.json4j.JSONObject optionRequest = new OrderedJSONObject();
                optionRequest.put("requestingChannel","EBIZ");
                optionRequest.put("optionDetails",optionDetails);
                optionRequest.put("address",address);
                optionRequest.put("safePlaceDetails",safeplacedetails);


                org.apache.wink.json4j.JSONObject jsonObjectToPost = new OrderedJSONObject();
                jsonObjectToPost.put("mailPieceId",responseFields.get("Barcode"));
                jsonObjectToPost.put("destinationPostCode",responseFields.get("PostCode"));
                jsonObjectToPost.put("optionRequest",optionRequest);


                org.apache.wink.json4j.JSONObject Final_jsonObjectToPost = new OrderedJSONObject();
                Final_jsonObjectToPost.put("mailPieces",jsonObjectToPost);
                System.out.println("Response of Inflight event"+Final_jsonObjectToPost);
                response = request.body(Final_jsonObjectToPost.toString()).when().post("https://test.api.royalmail.net/delivery-change/v1/mailpieces/"+responseFields.get("1DBarcode")+"/options?postCode="+responseFields.get("PostCode"));
                json = response.then().statusCode(200);
                System.out.println(response.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    private Boolean ValidateBarcode(String BARCODE){
        try {
            RestAssured.baseURI = "http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + BARCODE + "/details";
            RequestSpecification httpRequest = RestAssured.given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded");
            Response response = httpRequest.get();
            int statusCode = response.getStatusCode();
            System.out.println("status code "+statusCode+RestAssured.baseURI);
            return statusCode == 404;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Boolean ValidateUID(String UID) {
        try {
            RestAssured.baseURI = "http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + UID + "/details";
            RequestSpecification httpRequest = RestAssured.given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded");
            Response response = httpRequest.get();
            int statusCode = response.getStatusCode();
            return statusCode == 404;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void createCSVfromSheet(HashMap<String, String> Map) throws FileNotFoundException {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter PreAdvice_Date = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String BARCODE=Map.get("BARCODE");
        String UID=Map.get("UID");
        String EMAIL=Map.get("EMAIL");
        String MOBILE=Map.get("MOBILE");
        String PRODUCT=Map.get("PRODUCT");
        String WIRENUMBER = Map.get("WIRENUMBER");
        String CUSTOMERACCOUNTNUMBER = Map.get("CUSTOMERACCOUNTNUMBER");
        String CONTRACTNUMBER=Map.get("CONTRACTNUMBER");
        String POSTINGLOCATIONNUMBER=Map.get("POSTINGLOCATIONNUMBER");
        String FILETYPE=Map.get("FILETYPE");
        String LOCATIONID=Map.get("LOCATIONID");

        String SOURCEPO=Map.get("SOURCEPO");
        String SOURCEAddress=Map.get("SOURCEAddress");
        String SOURCEPOSTOWN= Map.get("SOURCEPOSTOWN");

        String DESTINATIONPO=Map.get("DESTINATIONPO");
        String DESTINATIONADDRESS=Map.get("DESTINATIONADDRESS");
        String DESTINATIONPOSTTOWN=Map.get("DESTINATIONPOSTTOWN");
        String BUILDINGNUMBER = Map.get("BUILDINGNUMBER");

        String SMSID=Map.get("SMSID");
        String EMAILID=Map.get("EMAILID");
        File csvOutputFile = new File("COSS"+WIRENUMBER+"_PreAdvice3_"+ ZonedDateTime.now().format(PreAdvice_Date).replaceAll("\\.","").replaceAll(":","")+".csv");
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]
                { "\"00\"","\"03\"","\""+FILETYPE+"\"","\""+CUSTOMERACCOUNTNUMBER+"\"","\"MULTIPLE\"","\"1\"","\"\"","\"\"","\"\"","\"LIVE\"","\""+ ZonedDateTime.now().format(FORMATTER)+"+00:00\"","\""+WIRENUMBER+"\"","\"\"","\"09\"" });
        dataLines.add(new String[]
                { "\"01\"","\"03\"","\"HEARTBEAT\"","\""+SOURCEAddress+"\"","\"\"","\"\"","\"\"","\"\"","\""+SOURCEPOSTOWN+"\"","\""+SOURCEPO+"\"","\"PHASE 3 F POSTER\"","\"123456789012\"","\"\"","\"\"","\"\"","\"royalmail.support@neopost.co.uk\"","\""+LOCATIONID+"\"","\""+POSTINGLOCATIONNUMBER+"\"" });
        dataLines.add(new String[]
                { "\"02\"","\"03\"","\"\"","\""+PRODUCT+"\"","\"\"","\"\"","\"\"","\"RoyalMAil\"","\"\"","\"\"","\"500\"","\"1\"","\"\"","\"RoyalMail\"","\""+BUILDINGNUMBER+"\"","\""+DESTINATIONADDRESS+"\"","\"\"","\""+DESTINATIONPOSTTOWN+"\"","\""+DESTINATIONPO+"\"","\"Inflight_TESTDATA\"","\"5\"","\"\"","\"\"","\"\"","\"GB\"","\"\"","\""+UID+"\"","\"1000\"","\"1\"","\"\"","\"4\"","\"\"","\"3\"","\"\"","\"\"","\"\"","\"\"","\""+BARCODE+"\"","\"1\"","\""+ZonedDateTime.now().format(DATE)+"\"","\""+ZonedDateTime.now().format(DATE)+"\"","\"\"","\"\"","\"GBR\"","\""+CONTRACTNUMBER+"\"","\"RoyalMail\"","\"0\"","\"\"","\"\"" });
        dataLines.add(new String[]
                { "\"03\"","\"03\"","\""+UID+"\"","\""+SMSID+"\"","\""+MOBILE+"\"" });
        dataLines.add(new String[]
                { "\"03\"","\"03\"","\""+UID+"\"","\""+EMAILID+"\"","\""+EMAIL+"\"" });
        dataLines.add(new String[]
                { "\"09\"","\"03\"","\"13\"" });
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(FileManipulation::convertToCSV)
                    .forEach(pw::println);
        }
    }
    private Boolean ValidateUID() {
        try {
            UID = FileManipulation.readFile(1);
            RestAssured.baseURI = "http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + UID + "/details";
            RequestSpecification httpRequest = RestAssured.given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded");
            Response response = httpRequest.get();
            int statusCode = response.getStatusCode();
            if (statusCode == 404) {
                return true;
            } else {
                while (statusCode != 404) {
                    removeRow(1);
                    UID = FileManipulation.readFile(1);
                    RestAssured.baseURI = "http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + UID + "/details";
                    httpRequest = RestAssured.given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded");
                    response = httpRequest.get();
                    statusCode = response.getStatusCode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private Boolean readAndProcess(String fileName) throws IOException, InterruptedException {
        HashMap<String,String>getMap= new HashMap<String,String>();
        File src=new File(fileName);
        FileInputStream fis=new FileInputStream(src);
        XSSFWorkbook srcBook= new XSSFWorkbook(fis);
        XSSFSheet sourceSheet = srcBook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        XSSFRow sourceRow = null;
        for(int i=1;i<=sourceSheet.getLastRowNum();i++) {
            sourceRow = sourceSheet.getRow(i);
            getMap.put("BARCODE",sourceRow.getCell(0).getStringCellValue().trim());
            getMap.put("UID",sourceRow.getCell(1).getStringCellValue().trim());
            getMap.put("EMAIL",sourceRow.getCell(2).getStringCellValue().trim());
            getMap.put("MOBILE",formatter.formatCellValue(sourceRow.getCell(3)).trim());
            getMap.put("PRODUCT",sourceRow.getCell(4).getStringCellValue().trim());
            getMap.put("WIRENUMBER",sourceRow.getCell(5).getStringCellValue().trim());
            getMap.put("CUSTOMERACCOUNTNUMBER",formatter.formatCellValue(sourceRow.getCell(6)).trim());
            getMap.put("CONTRACTNUMBER",formatter.formatCellValue(sourceRow.getCell(7)).trim());
            getMap.put("POSTINGLOCATIONNUMBER",formatter.formatCellValue(sourceRow.getCell(8)).trim());
            getMap.put("FILETYPE",sourceRow.getCell(9).getStringCellValue().trim());
            getMap.put("LOCATIONID",formatter.formatCellValue(sourceRow.getCell(10)).trim());
            getMap.put("SOURCEPO",sourceRow.getCell(11).getStringCellValue().trim());
            getMap.put("SOURCEAddress",sourceRow.getCell(12).getStringCellValue().trim());
            getMap.put("SOURCEPOSTOWN",sourceRow.getCell(13).getStringCellValue().trim());
            getMap.put("DESTINATIONPO",formatter.formatCellValue(sourceRow.getCell(14)).trim());
            getMap.put("DESTINATIONADDRESS",formatter.formatCellValue(sourceRow.getCell(15)).trim());
            getMap.put("DESTINATIONPOSTTOWN",sourceRow.getCell(16).getStringCellValue().trim());
            getMap.put("BUILDINGNUMBER",formatter.formatCellValue(sourceRow.getCell(17)).trim());
            getMap.put("SMSID",formatter.formatCellValue(sourceRow.getCell(18)).trim());
            getMap.put("EMAILID",formatter.formatCellValue(sourceRow.getCell(19)).trim());
            System.out.println("getMap "+getMap);
            if(ValidateBarcode(getMap.get("BARCODE")) && ValidateUID(getMap.get("UID"))){
                createCSVfromSheet(getMap);
                processCSV();
            }else {
                System.out.println("Barcode or UID Already Processed "+getMap.get("BARCODE")+" : "+getMap.get("UID"));
            }
        }
        return true;
    }
    private static void processScansfromSheet(String fileName) throws IOException, InterruptedException {
        HashMap<String,String>getMap= new HashMap<String,String>();
        File src=new File(fileName);
        FileInputStream fis=new FileInputStream(src);
        XSSFWorkbook srcBook= new XSSFWorkbook(fis);
        XSSFSheet sourceSheet = srcBook.getSheetAt(1);
        XSSFRow sourceRow = null;
        DataFormatter formatter = new DataFormatter();
        XSSFRow FirstRow =sourceSheet.getRow(0);
        int rowNum=0;
        DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MM/d/yy");
        for(int i=0;i<FirstRow.getLastCellNum();i++){
            System.out.println("Date Value "+formatter.formatCellValue(FirstRow.getCell(i)) + " equals "+ZonedDateTime.now().format(DATE));
            if(formatter.formatCellValue(FirstRow.getCell(i)).equals(ZonedDateTime.now().format(DATE))){
                rowNum=i;
                break;
            }
        }
        for(int i=1;i<=sourceSheet.getLastRowNum();i++) {
            sourceRow = sourceSheet.getRow(i);
            try {
                if (sourceRow.getCell(rowNum).getStringCellValue() != null) {
                    getMap.put("BARCODE", sourceRow.getCell(0).getStringCellValue());
                    getMap.put("EVENT", sourceRow.getCell(rowNum).getStringCellValue());
                    getMap.put("LOCATION", formatter.formatCellValue(sourceRow.getCell(1)));
                    Thread.sleep(1000);
                    PerformScan(getMap);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }
    }

    @Then("^I validate the barcode details in the Redland reports for Multiple scans$")
    public void iValidateTheBarcodeDetailsInTheRedlandReportsForMultipleScans() throws InterruptedException, IOException {

        for (Object o : BarcodeRedlandList) {
            JSONObject itemArr = (JSONObject) o;
            seleniumTests.inputBarcode(itemArr.get("barcode").toString());

            String[] arrEvents =null;
            String Eventlist=itemArr.get("Event").toString();
            if(Eventlist.contains(";")){
                arrEvents=Eventlist.split(";");
                System.out.println("ArrayEvents "+ Arrays.toString(arrEvents));
                for (String arrEvent : arrEvents) {
                    seleniumTests.validateBarcode(arrEvent);
                    System.out.println("Validated Barcode "+itemArr.get("barcode").toString()+" with Event "+arrEvent);
                }
            }else{
                seleniumTests.validateBarcode(Eventlist);
            }

        }
        seleniumTests.ExitSession();
    }

    @Given("^I read the event from the EPS response for the \"([^\"]*)\"$")
    public void iReadTheEventFromTheEPSResponseForThe(String Barcode) throws IOException, ParseException {
        response = given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded").when().get("http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + Barcode + "/details");
        json = response.then().statusCode(200);
        List<String> jsonResponse = response.getBody().jsonPath().getList("mailPieces.events.eventCode");
        jsonResponse.forEach(System.out::println);
        JSONObject BarcodeDetails = new JSONObject();
        BarcodeDetails.put("barcode",Barcode);
        StringBuilder Events= new StringBuilder();
        for (String event : jsonResponse) {
            Events.append(event).append(";");
        }
        BarcodeDetails.put("Event", Events.toString());
        BarcodeRedlandList.add(BarcodeDetails);
        try (FileWriter file = new FileWriter(String.valueOf(Json_redland_file.toAbsolutePath()))) {
            file.write(String.valueOf(BarcodeRedlandList));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @When("^I perform the \"([^\"]*)\" Scan on the Barcode$")
    public void iPerformTheScanOnTheBarcode(String EventScan) throws Throwable {
        PerformScan(EventScan);
    }
}
