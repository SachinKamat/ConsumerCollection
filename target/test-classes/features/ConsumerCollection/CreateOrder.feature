@Feature_CreateOrder
Feature: CreateOrder

  @CreateOrder_001 @separate_flow
  Scenario Outline: Validate the Create Order Functionality with appropriate Time slots
    Given I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I copy timeslot for the available date from get slots api
    When  I open the Order Api with valid body for the following fields
          |Barcode|TP200008676GB|
    Then  I validate the response has status "<OrderStatus>" and contains "<OrderMessage>"

    Examples:
      | DPS |Status|Message|ItemCount|OrderStatus|OrderMessage              |
      |UB11NW1U|200|datewiseSlots |1 |  201      |Order created successfully|

  @CreateOrder_002 @negative_flow
  Scenario Outline: Validate the Create Order Functionality with appropriate Time slots
    Given I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I copy timeslot for the available date from get slots api
    When  I open the Order Api with valid body for the following fields
      |Barcode|TP200007738GB|
    Then  I validate the response has status "<OrderStatus>" and contains "<OrderMessage>"
    Examples:
      | DPS |Status|Message|ItemCount|OrderStatus|OrderMessage              |
      |UB54FD1A|200|datewiseSlots |1 |  201      |Order created successfully|
