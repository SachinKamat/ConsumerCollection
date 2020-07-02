@Feature_CC_E2E_Scenario
Feature: Consumer_Collection_End_to_End_Scenarios

@E2EScenario_001 @separate_flow
Scenario Outline: Validate the Create Order Functionality with appropriate Time slots
    Given I open the Validate api for the "<DPS>"
    And   I validate the response has status "<Status>" and contains "<DPSMessage>"
    When  I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I copy timeslot for the available date from get slots api
    When  I open the Order Api with valid body for the following fields
          |Barcode|TP200028070GB|
    Then  I validate the response has status "<OrderStatus>" and contains "<OrderMessage>"
    Then  I validate the GetOrder details for the Order created and the Collection Order status is "Created" and mailpiece status is "AwaitingCollection"
    And   I get the Task Created in the Location API
    When  I open the Booking Event API to place the order
    Then  I validate the GetOrder details for the Order created and the Collection Order status is "CollectionOrderPlaced" and mailpiece status is "AwaitingCollection"
    And   I validate the event generation for the barcode in EPS for Order booked
          |Event|ECCSB|
          |Barcode|TP200028070GB|
  #Add book
#    When  I perform the Collection Scan "<collectionscan>" and MailpieceScan "<mailpiecescan>" for the Order created
#    Then  I validate the GetOrder details for the Order created and the message displayed is ""
#    And   I validate the event generation for the barcode in EPS
#          |mailPieces.events|EVCAD|
#          |Barcode|TP200007738GB|

Examples:
| DPS |Status|Message|ItemCount|OrderStatus|OrderMessage|DPSMessage|
|UB25QE2L|200|datewiseSlots |1 |  201      |   Order created successfully         |doId|