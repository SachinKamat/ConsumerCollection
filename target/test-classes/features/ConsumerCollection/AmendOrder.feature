@Feature_AmendOrder
Feature: Amend_Order

  @AmendOrder_001 @separate_flow
  Scenario Outline: Validate the Amend Order Functionality with valid Time slots
    Given I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I copy timeslot for the available date from get slots api
    When  I open the Order Api with valid body for the following fields
          |Barcode|TP200008676GB|
    Then  I validate the response has status "<OrderStatus>" and contains "<OrderMessage>"
    And   I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I copy timeslot for the available date from get slots api
    When  I open the Order Amend Api with valid body for the following fields
    Then  I validate the response has status "<UpdateOrderStatus>" and contains "<OrderUpdateMessage>"

    Examples:
      | DPS |Status|Message|ItemCount|OrderStatus|OrderMessage              |OrderUpdateMessage           |UpdateOrderStatus|
      |UB11NW1U|200|datewiseSlots |1 |  201      |Order created successfully|   Order updated successfully|200              |