@Feature_ValidateDPS_Api
Feature: ValidateDPS_Api

  @ValidateDPS_001 @separate_flow
  Scenario Outline: Validate the ValidateDPS functionality
    Given I open the Validate api for the "<DPS>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Examples:
      | DPS |Status|Message|
      |UB100AD1P|200|doId  |
      |UB13424200AD1P|404|Invalid delivery postcode provided|

  @ValidateDPS_002 @separate_flow
  Scenario Outline: Validate the ValidateDPS functionality for CC eligibility
    Given I open the Validate api for the "<DPS>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Then  I validate CC-eligibility for the validateDPS
    Examples:
      | DPS |Status|Message|
      |UB100AD1P|200|doId  |

  @ValidateDPS_003 @separate_flow
  Scenario Outline: Validate the get slots available for the DPS
    Given I open the Validate api for the "<DPS>" with invalid "<Header>"
    Given I open the get slots api for the "<DPS>" and "<ItemCount>" with invalid "<Header>"
    And   I validate the response has status "<Status>" and contains "<Message>"
    Examples:
      | DPS |Status|Message|ItemCount|Header|
      |UB11NW1U|401|Invalid client id or secret |1 |clientid:123455|
      |UB11NW1U|401|Invalid client id or secret |1 |clientsecret:123455|
      |UB11NW1U|400|X-RMG-Date-Time is required and must not be empty |1 |dateTime:|