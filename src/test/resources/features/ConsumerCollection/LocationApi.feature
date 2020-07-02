@Feature_Location_Api
Feature: Location_Api

  @location_001 @separate_flow
  Scenario Outline: Validate the get Task Functionality from Location Api
    Given I open the Location api uri with following details to get available tasks
          |DOId|<doid>|
          |Type|<type>|
          |StartDate|<startdate>|
          |EndDate  |<enddate>  |
          |RouteId  |<routeid>  |
          |ActionMode|<actionmode>|
    And   I validate the response has status "<Status>" and contains "<Message>"
    Examples:
      |doid  |Status|Message|type|startdate|enddate|routeid|actionmode|
      |3985  |200   |deliveryOffice|CC  |2020-06-15|2020-06-15|3789314034|CC |
      |3985  |404   |E1475|Cc  |2020-06-15|2020-06-15|3789314034|CC |
      |3312111412412  |404   |E1475|CC  |2020-06-15|2020-06-15|3789314034|CC |
      |3985  |400   |Mandatory parameters are not provided in request|CC  |2020-06-15|2020-06-15|3789314034| |
      |3985  |400   |Mandatory parameters are not provided in request|  |2020-06-15|2020-06-15|3789314034| CC|
      |3985  |400   |Date range is not valid| CC |2020-06-20|2020-06-15|3789314034| CC|



  @location_002 @separate_flow
  Scenario: Validate the Error message for authorisation failure
    Given I open the Location api uri with following details to get available tasks for authorisation
      |DOId|<doid>|
      |Type|<type>|
      |StartDate|<startdate>|
      |EndDate  |<enddate>  |
      |RouteId  |<routeid>  |
      |ActionMode|<actionmode>|
    And   I validate the response has status "<Status>" and contains "<Message>"