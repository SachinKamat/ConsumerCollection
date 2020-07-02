@Feature_EmailContentVerification
Feature: Email verification


  @EmailAlertContentVerification_001 @separate_flow
  Scenario Outline: Validating Alert Email based on Subject
    Given I logged in to email account with userId "troyalmail@gmail.com" and password "Test@123"
    Then I verify mail count "19" with subject "<subject>" is present in folder "Inbox" from email "<EmailID>"
    Examples:
      | subject                   |
      | Verification_unique_Drugs |

  @EmailAlertContentVerification_002 @separate_flow
  Scenario Outline: Validating Alert Email based on Subject and Content of Updated Section
    Given I logged in to email account with userId "troyalmail@gmail.com" and password "Test@123"
    Then I verify mail count "3" with subject "<subject>" and content "<content>" is in folder "Inbox" from email "<EmailID>"

    Examples:
      | content                                                                             | subject                   |
      | 1 Report was updated in this time period for the update types you are subscribed to | Verification 		|


  @EmailAlertContentVerification_003 @separate_flow
  Scenario Outline: Validating Drugs Alert Email based on Subject and Content of New Section
    Given I logged in to email account with userId "troyalmail@gmail.com" and password "Test@123"
    Then I verify mail count "9" with subject "<subject>" and content "<content>" is in folder "Inbox" from email "<EmailID>"

    Examples:
      | content                                                  | subject                   |
      | 1 Report was new to your results set in this time period | Verification		     |

  @EmailAlertContentVerification_004 
  Scenario Outline: Validating Alert Email based on Subject and Content of Removed Section
    Given I logged in to email account with userId "troyalmail@gmail.com" and password "Test@123"
    Then I verify mail count "2" with subject "<subject>" and content "<content>" is in folder "Inbox" from email "<EmailID>"
    Examples:
      | content                                                                            | subject      |
      | 1 Report was removed from your results because it does not match your search query | Verification |

  @EmailAlertContentVerification_005 
  Scenario Outline: Validating Alert Email based Only on Subject with Specific time duration
    Given I logged in to email account with userId "troyalmail@gmail.com" and password "Test@123"
    Then I verify mail count "1" with subject "<subject>" is in folder "Inbox" from email "<EmailID>" in last "8" hr
    Examples:
      |subject|
      |Verification_unique_Drugs|

  @EmailAlertContentVerification_006 
  Scenario Outline: Validating Alert Email based on both Subject and Content with Specific time duration
    Given I logged in to email account with userId "troyalmail@gmail.com" and password "Test@123"
    Then I verify mail count "1" with subject "<subject>" and content "<content>" is in folder "Inbox" from email "<EmailID>" in last "8" hr
    Examples:
      |content|subject|
      |1 Report was new to your results set in this time period|Verification_unique_Drugs|


  
