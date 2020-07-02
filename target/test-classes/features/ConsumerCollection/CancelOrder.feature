@Feature_CancelOrder
Feature: CancelOrder

  @AmendOrder_001 @separate_flow
  Scenario Outline: Validate the Cancel Order Functionality with valid Time slots
    Given I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I copy timeslot for the available date from get slots api
    When  I open the Order Api with valid body for the following fields
      |Barcode|TP200008676GB|
    Then  I validate the response has status "<OrderStatus>" and contains "<OrderMessage>"
    When  I open the Order Cancel Api with valid body for the following fields
    Then  I validate the response has status "<CancelOrderStatus>" and contains "<CancelUpdateMessage>"
    Then  I validate the GetOrder details for the Order created and the Collection Order status is "Cancelled" and mailpiece status is "AwaitingCollection"

    Examples:
      | DPS |Status|Message|ItemCount|OrderStatus|OrderMessage              |CancelOrderStatus|CancelUpdateMessage|
      |UB11NW1U|200|datewiseSlots |1 |  201      |Order created successfully|   Order cancelled successfully               |200|