@Feature_Royal_Heart_Beat
  Feature: Monitor Heartbeat flow by simulating Parcel Journey

    @generate_preadvice_001
    Scenario: Create and verify Preadvice generation
      Given I read the barcode from the TestDataSheet
      And   I process the barcode in BIG Queue via WINSCP
      When  COSS file is successfully processed from the BIG Queue

    @process_Scans_002
      Scenario:Generate MPER file and Perform Acceptance Scan on the Barcode
        Given I read the barcode from the Json Test Data
        And   I validate the event generation for the barcode in EPS
              |mailPieces.events|EVAIP|
        And   I validate the event generation for the barcode in TAPI
              |mailPieces.events|EVAIP|
        When  I perform the Acceptance Scan on the Barcode
        And   I wait for the scan to be performed on the barcode
        Then  I validate the event generation for the barcode in EPS
              |mailPieces.events|EVDAC|
        Then  I validate the event generation for the barcode in TAPI
              |mailPieces.events|EVDAC|

    @process_Scans_003
    Scenario:Update MPER file and perform  Scans on the Barcode
      Given I read the barcode from the Json Test Data
      And   I check for the scans performed on the barcode
      When  I Update and perform the scans on the barcode
      And   I wait for the scan to be performed on the barcode
      Then  I validate the event generation for the barcode in EPS for the scans performed


    @generate_preadvice_002
    Scenario: Create Preadvice batch Files
      Given I read and assign and process the preadvice from the TestDataSheet

    @generate_preadvice_003
    Scenario: Process batch files from spreadsheet
      Given I read and assign and process the Scans from the TestDataSheet

    @generate_preadvice_004
    Scenario: Test the Reporting for Heartbeat
      Given I test the reporting for the Heartbeat

    @validate_Redland_Reports_001
    Scenario: Validate the redland reports for the barcode
      Given I read the barcode from the Json Test Data for REDLAND Verification
      And   I open Redland site in the browser
      Then  I validate the barcode details in the Redland reports for "EVDAC"

    @validate_Redland_Reports_002
    Scenario: Validate the redland reports for the barcode from the spreadsheet
      Given I read the barcode from the Json Test Data for REDLAND Verification
      And   I open Redland site in the browser
      Then  I validate the barcode details in the Redland reports for Multiple scans

    @validate_Redland_Reports_003
    Scenario Outline: Validate the redland reports for the barcode from the EPS response
      Given I read the event from the EPS response for the "<Barcode>"
      When  I read the barcode from the Json Test Data for REDLAND Verification
      And   I open Redland site in the browser
      Then  I validate the barcode details in the Redland reports for Multiple scans
      Examples:
    |Barcode      |
    |TJ200014518GB|
    |TJ200014521GB|
    |TJ200014535GB|
    |TJ200014549GB|
    |TJ200014552GB|

