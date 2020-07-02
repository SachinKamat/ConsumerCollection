@Feature_Process_Inflight
Feature: Processing Preadvice and updating scan sheet for Inflight

  @ProcessPreadvice_Inflight_001
  Scenario:Generate MPER file and Perform Acceptance Scan on the Barcode
  Given I read and assign data from the TestDataSheet for the inflight options and scans
   And I process the preadvice for the barcode in the Inflight data sheet

  @ProcessScans_Inflight_002
  Scenario:Perform Acceptance Scan on the Barcode
    Given I read the barcode and process the Scans from inflight data sheet

  @ProcessDeliveryChangeOptions_003
  Scenario: Perform the delivery change option for the barcode from the Inflight Data sheet
    Given I read the barcode and process the Inflight options from inflight data sheet

