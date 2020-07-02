package com.royalmail.StepDefinition;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import com.royalmail.Helpers.PageInstance;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;
import org.testng.Assert;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.royalmail.Helpers.FileManipulation.updateJsonFile;
import static io.restassured.RestAssured.given;

public class ConsumerCollection extends PageInstance {
    private Response response;
    private ValidatableResponse json;

    @And("^I validate the response has status \"([^\"]*)\" and contains \"([^\"]*)\"$")
    public void iValidateTheResponseHasStatusAndContains(String status, String Message) throws Throwable {
        json = response.then().statusCode(Integer.parseInt(status));
        if(!response.getBody().asString().trim().contains(Message)){
            Assert.assertEquals(true,false);
        }
    }

    @Given("^I open the get slots api for the \"([^\"]*)\" and \"([^\"]*)\"$")
    public void iOpenTheGetSlotsApiForTheAnd(String DPS, String ItemCount) throws Throwable {
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020").when().get("https://test.api.royalmail.net/collectionorders/v1/slots?dps="+DPS+"&itemCount="+ItemCount);
        System.out.println("response "+response.getBody().asString());
    }

    @Given("^I open the get slots api for the \"([^\"]*)\" and \"([^\"]*)\" with invalid \"([^\"]*)\"$")
    public void iOpenTheGetSlotsApiForTheAndWithInvalid(String DPS, String ItemCount, String Header) throws Throwable {
        String ClientID="11673592-68c4-413b-bdec-a84b2bfe27f3";
        String ClientSecret="pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA";
        String Accept="application/json";
        String DateTime="12/06/2020";
        if(Header.contains("clientid")){
            ClientID=StringUtils.substringAfterLast(Header, ":");
        }else if(Header.contains("clientsecret")){
            ClientSecret=StringUtils.substringAfterLast(Header, ":");
        }else if(Header.contains("accept")){
            Accept=StringUtils.substringAfterLast(Header, ":");
        }else if(Header.contains("dateTime")){
            DateTime=StringUtils.substringAfterLast(Header, ":");
        }
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", ClientID, "x-ibm-client-secret",ClientSecret,"Accept", Accept,"X-RMG-Date-Time",DateTime).when().get("https://test.api.royalmail.net/collectionorders/v1/slots?dps="+DPS+"&itemCount="+ItemCount);
        System.out.println("response "+response.getBody().asString());
    }

    @Then("^I copy timeslot for the available date from get slots api$")
    public void iCopyTimeslotForTheAvialableDateFromGetSlotsApi() {
        DateForOrder = response.jsonPath().getString("taskSlots.datewiseSlots.slotDate[0]");
        TIMESLOTS = response.jsonPath().getString("taskSlots.slotDetails.tokenId");
        DOID=response.jsonPath().getString("taskSlots.slotDetails.deliveryOfficeId");
        System.out.println(" Date for Order "+DateForOrder+" slotfororder "+TIMESLOTS+" DOID "+DOID);
    }

    @When("^I open the Order Api with valid body for the following fields$")
    public void iOpenTheOrderApiWithApropriateBodyForTheFollowingFields(Map<String,String> RequestBody) {
        String payload = "{\n" +
                "  \"timeslotReservationId\": \""+TIMESLOTS+"\",\n" +
                "  \"senderDetails\": {\n" +
                "    \"senderName\": \"Sachin\",\n" +
                "    \"senderEmail\": \"troyalmail@gmail.com\"\n" +
                "  },\n" +
                "  \"accountDetails\": {\n" +
                "    \"partnerId\": \"\",\n" +
                "     \"retailerAccountNumber\": \"0368482000\"\n" +
                "  },\n" +
                "  \"address\": {\n" +
                "    \"addressLine1\": \"Southend Road\",\n" +
                "    \"addressLine2\": \"EAST HAM\",\n" +
                "    \"addressLine3\": \"addressLine3\",\n" +
                "    \"postTown\": \"London\",\n" +
                "    \"postcode\": \"UB25QE\",\n" +
                "    \"DPS\": \"2L\"\n" +
                "  },\n" +
                "  \"safePlaceDetails\": {\n" +
                "    \"locationText\": \"behind the bin\",\n" +
                "    \"locationCode\": \"BHDBIN\"\n" +
                "  },\n" +
                "  \"animalHazardDetails\": \"Dogs - not dangerous\",\n" +
                "\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"itemBarcodeId\":\""+RequestBody.get("Barcode")+"\",\n" +
                "      \"weightInGrams\": 100,\n" +
                "      \"itemServiceName\": \"Tracked Returns 24 (T24) Enhanced\",\n" +
                "      \"itemStatus\": \"AwaitingCollection\",\n" +
                "      \"dimensions\": {\n" +
                "        \"height\": 45,\n" +
                "        \"width\": 35,\n" +
                "        \"depth\": 16\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"collectionDate\":\""+DateForOrder+"\"\n" +
                "}";
        System.out.println("Payload "+payload);
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020","X-RMG-Language","en").contentType(ContentType.JSON).body(payload).post("https://test.api.royalmail.net/orders/v1/collectionOrder");
        if(response.getBody().asString().contains("Order created successfully")){
            ORDERID=response.jsonPath().getString("collectionOrderId");
        }
        System.out.println("OrderID "+ORDERID);
        System.out.println("Order response "+response.getBody().asString());
    }

    @And("^I get the Task Created in the Location API$")
    public void iGetTheTaskCreatedInTheLocationAPI() {
        System.out.println("DOID "+DOID+" collectiondate "+DateForOrder+" ROUTE "+ROUTE);
        response = given().headers("X-RMG-Client-Id","123").when().get("http://tmloc-sita.rmgn.royalmailgroup.net/locations/"+DOID+"/tasks/v1?type=CC&actionMode=CC&skipEntityData=false&startDate="+DateForOrder+"&endDate="+DateForOrder+"&routeBarcodeId="+ROUTE);
        JsonPath jsonPath = response.jsonPath();
        json = response.then().statusCode(Integer.parseInt("200"));
        //System.out.println("deliveryOffice.route" +jsonPath.getString("deliveryOffice"));
        System.out.println("deliveryOffice.route" +jsonPath.getString("deliveryOffice.route.dp.task.entityId"));
        String[] arr =jsonPath.getString("deliveryOffice.route.dp.task.entityId").split("]]");
        int loc=0;
        for(int i=0;i<arr.length;i++){
            System.out.println(arr[i]);
            if(arr[i].contains(ORDERID)){
                loc=i;
            }
        }
        //System.out.println("Location "+jsonPath.getString("deliveryOffice.route.dp["+loc+"].task[0].entityId"));
        String[] arr2 =jsonPath.getString("deliveryOffice.route.dp["+loc+"].task[0].entityId").split(",");
        int loc2=0;
        for(int i=0;i<arr2.length;i++){
            System.out.println(arr2[i]);
            if(arr2[i].contains(ORDERID)){
                loc2=i;
            }
        }
        System.out.println("TaskID "+jsonPath.getString("deliveryOffice.route.dp["+loc+"].task[0].taskId["+loc2+"]"));
        TaskID=jsonPath.getString("deliveryOffice.route.dp["+loc+"].task[0].taskId["+loc2+"]");
        System.out.println("TaskID "+TaskID);
    }

    @Given("^I open the Location api uri with following details to get available tasks$")
    public void iOpenTheLocationApiUriWithFollowingDetailsToGetAvailableTasks(Map<String,String>Fields) {
        System.out.println("Fields "+Fields);
        response = given().headers("X-RMG-Client-Id","123").when().get("http://tmloc-sita.rmgn.royalmailgroup.net/locations/"+Fields.get("DOId")+"/tasks/v1?type="+Fields.get("Type")+"&actionMode="+Fields.get("ActionMode")+"&skipEntityData=false&startDate="+Fields.get("StartDate")+"&endDate="+Fields.get("EndDate"));
    }

    @Given("^I open the Location api uri with following details to get available tasks for authorisation$")
    public void iOpenTheLocationApiUriWithFollowingDetailsToGetAvailableTasksForAuthorisation(Map<String,String>Fields) {
        response = given().headers("X-RMG-Client-Id","").when().get("http://tmloc-sita.rmgn.royalmailgroup.net/locations/"+Fields.get("DOId")+"/tasks/v1?type="+Fields.get("Type")+"&actionMode="+Fields.get("ActionMode")+"&skipEntityData=false&startDate="+Fields.get("StartDate")+"&endDate="+Fields.get("EndDate"));
    }


    @Then("^I validate the GetOrder details for the Order created and the Collection Order status is \"([^\"]*)\" and mailpiece status is \"([^\"]*)\"$")
    public void iValidateTheGetOrderDetailsForTheOrderCreatedAndTheCollectionOrderStatusIsAndMailpieceStatusIs(String OrderCollectionStatus, String MailPieceStatus) throws Throwable {
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020","X-RMG-Language","en").get("https://test.api.royalmail.net/orders/v1/collectionOrder/"+ORDERID);
        json = response.then().statusCode(200);
        System.out.println("Test "+response.getBody().asString());
        if(!(response.getBody().asString().contains(OrderCollectionStatus)) && !(response.getBody().asString().contains(MailPieceStatus)))
            Assert.assertEquals(true,false);
    }

    @When("^I open the Booking Event API to place the order$")
    public void iOpenTheBookingEventAPIToPlaceTheOrder() {
         Response response2;
         ValidatableResponse json;
        String payload="{\n" +
                "\n" +
                "  \"status\": \"CollectionOrderPlaced\"\n" +
                "\n" +
                "}";
        response2 = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020","X-RMG-Language","en","X-Access-Token","0f362e05-7cea-451e-9613-0fd7697d43ec","Content-Type","application/json").body(payload).put("https://rm-consumer-collection-gateway-uat.storefeeder.com/api/v1/collections/"+ORDERID+"/status");
        System.out.println("response "+response2.getBody().asString());
        response2.then().statusCode(200);

    }

    @And("^I validate the event generation for the barcode in EPS for Order booked$")
    public void iValidateTheEventGenerationForTheBarcodeInEPSForOrderBooked(Map<String,String>Fields) {
        response = given().headers("X-RMG-Client-ID", "UAT", "Content-Type", "application/x-www-form-urlencoded").when().get("http://psmsg-sit.rmgn.royalmailgroup.net/mailpieces/" + Fields.get("Barcode") + "/details");
        json = response.then().statusCode(200);
        if(!response.getBody().asString().contains(Fields.get("Event"))){
            Assert.assertEquals(true,false);
        }

    }

    @And("^I validate the event generation for the barcode in TAPI for Order booked$")
    public void iValidateTheEventGenerationForTheBarcodeInTAPIForOrderBooked(Map<String,String>Fields) {
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json").when().get("https://test.api.royalmail.net/mailpieces/v2/"+Fields.get("Barcode")+"/events");
        json = response.then().statusCode(200);
        if(!response.getBody().asString().contains(Fields.get("Event"))){
            Assert.assertTrue(false);
        }
    }

    @Given("^I open the Validate api for the \"([^\"]*)\"$")
    public void iOpenTheValidateApiForThe(String DPS) throws Throwable {
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020").when().get("https://test.api.royalmail.net/dps/v1/"+DPS+"/validate");
        ROUTE=response.jsonPath().getString("dpsInfo.routeBarcodeId");
        System.out.println(" ROUTE "+ROUTE);
    }

    @Then("^I validate CC-eligibility for the validateDPS$")
    public void iValidateCCEligibilityForTheValidateDPS() {
        JsonPath jsonPath = response.jsonPath();
        json = response.then().statusCode(Integer.parseInt("200"));
        String[] arr=jsonPath.getString("dpsInfo.doInfo.doConfig").split("\\[");
        for (String s : arr) {
            System.out.println(s);
            if(s.contains("name:CCEnabledFlag") && s.contains("value:true")){
                Assert.assertTrue(true);
                break;
            }
        }
    }

    @Given("^I open the Validate api for the \"([^\"]*)\" with invalid \"([^\"]*)\"$")
    public void iOpenTheValidateApiForTheWithInvalid(String Dps, String Header) throws Throwable {
        String ClientID="11673592-68c4-413b-bdec-a84b2bfe27f3";
        String ClientSecret="pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA";
        String Accept="application/json";
        String DateTime="12/06/2020";
        if(Header.contains("clientid")){
            ClientID=StringUtils.substringAfterLast(Header, ":");
        }else if(Header.contains("clientsecret")){
            ClientSecret=StringUtils.substringAfterLast(Header, ":");
        }else if(Header.contains("accept")){
            Accept=StringUtils.substringAfterLast(Header, ":");
        }else if(Header.contains("dateTime")){
            DateTime=StringUtils.substringAfterLast(Header, ":");
        }
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", ClientID, "x-ibm-client-secret",ClientSecret,"Accept", Accept,"X-RMG-Date-Time",DateTime).when().get("https://test.api.royalmail.net/dps/v1/"+Dps+"/validate");
        System.out.println("response "+response.getBody().asString());
    }

    @When("^I open the Order Amend Api with valid body for the following fields$")
    public void iOpenTheOrderAmendApiWithValidBodyForTheFollowingFields() {
        String payload="{\n" +
                "    \"timeslotReservationId\": \""+TIMESLOTS+"\",\n" +
                "    \"senderDetails\": {\n" +
                "        \"senderName\": \"Sachin Kamat rocks\",\n" +
                "        \"senderEmail\": \"troyalmail@gmail.com\"\n" +
                "    },\n" +
                "    \"address\": {\n" +
                "        \"addressLine1\": \"Southend Road\",\n" +
                "        \"addressLine2\": \"EAST HAM\",\n" +
                "        \"addressLine3\": \"addressLine3\",\n" +
                "        \"postTown\": \"London\",\n" +
                "        \"postcode\": \"UB11NW\",\n" +
                "    \t\"DPS\": \"1U\",\n" +
                "        \"County\": \"Norfolk\"\n" +
                "    },\n" +
                "    \"safePlaceDetails\": {\n" +
                "        \"locationText\": \"behind the bin\",\n" +
                "        \"locationCode\": \"BHDBIN\"\n" +
                "    },\n" +
                "    \"animalHazardDetails\": \"Dogs - not dangerous\",\n" +
                "    \"collectionDate\": \""+DateForOrder+"\"\n" +
                "}";
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020","X-RMG-Language","en").contentType(ContentType.JSON).body(payload).put("https://test.api.royalmail.net/orders/v1/collectionOrder/"+ORDERID);
    }

    @When("^I open the Order Cancel Api with valid body for the following fields$")
    public void iOpenTheOrderCancelApiWithValidBodyForTheFollowingFields() {
        String payload="{\n" +
                "    \"status\": \"Cancelled\"\n" +
                "}";
        response = given().proxy("10.223.0.50",8080).headers("x-ibm-client-id", "11673592-68c4-413b-bdec-a84b2bfe27f3", "x-ibm-client-secret","pK8xI3aY1yS6uS0uS7oG1pW8vT5iB7iI4nV3mL4nK3cA6rT7wA","Accept", "application/json","X-RMG-Date-Time","12/06/2020","X-RMG-Language","en").contentType(ContentType.JSON).body(payload).put("https://test.api.royalmail.net/orders/v1/collectionOrder/"+ORDERID+"/status");
    }

    public static class StringUtils {
        public static final String EMPTY = "";

        public static String substringAfterLast(String str, String separator) {
            if (isEmpty(str)) {
                return str;
            }
            if (isEmpty(separator)) {
                return EMPTY;
            }
            int pos = str.lastIndexOf(separator);
            if (pos == -1 || pos == (str.length() - separator.length())) {
                return EMPTY;
            }
            return str.substring(pos + separator.length());
        }

        public static boolean isEmpty(String str) {
            return str == null || str.length() == 0;
        }
    }
}
