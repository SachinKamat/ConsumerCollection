@Feature_GetSlotsForOrder
Feature: Get Slots for Order

  @GetSlotsForOrder_001 @separate_flow
  Scenario Outline: Validate the get slots available for the DPS
    Given I open the get slots api for the "<DPS>" and "<ItemCount>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Examples:
      | DPS |Status|Message|ItemCount|
      |UB11NW1U|200|datewiseSlots |1 |
      |UB11NW1U|200|datewiseSlots |2 |
      |UB11NW1U|200|datewiseSlots |3 |
      |UB11NW1U|200|datewiseSlots |4 |
      |UB11NW1U|200|datewiseSlots |5 |
      |UB11NW1U|400|Maximum item count breached |6 |
      |UB11NW1U|400|Reservation item count value is Zero |0 |
      |UB11NW1U|400|itemCount is required and must not be empty | |
      |UB11NW1UU|404|Delivery postcode UB11NW1UU is not configured for collection |1 |
      ||400|dps is required and must not be empty |1 |


  @GetSlotsForOrder_002 @separate_flow
  Scenario Outline: Validate the get slots available for the DPS
    Given I open the get slots api for the "<DPS>" and "<ItemCount>" with invalid "<Header>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Examples:
      | DPS |Status|Message|ItemCount|Header|
      |UB11NW1U|401|Invalid client id or secret |1 |clientid:123455|
      |UB11NW1U|401|Invalid client id or secret |1 |clientsecret:123455|
      |UB11NW1U|400|X-RMG-Date-Time is required and must not be empty |1 |dateTime:|
