package com.royalmail.StepDefinition;


import com.royalmail.Helpers.EmailVerification;
import com.royalmail.Helpers.PageInstance;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import java.io.FileNotFoundException;

public class EmailVerificationStepDef  extends PageInstance{

    public EmailVerificationStepDef() throws FileNotFoundException {
    }

    @Before()
    public void beforeScenario(Scenario scenario) {
        PageInstance.scenario =scenario;
    }

    public EmailVerification emailVerification= new EmailVerification();

    @Given("^I logged in to email account with userId \"([^\"]*)\" and password \"([^\"]*)\"$")
    public void I_logged_in_to_email_account_with_userId_and_password(String emailId, String password) throws Throwable {
        boolean result = emailVerification.loginEmail(emailId, password);
    }

    @Then("^I verify mail count \"([^\"]*)\" with subject \"([^\"]*)\" is present in folder \"([^\"]*)\" from email \"([^\"]*)\"$")
    public void iVerifyMailCountWithSubjectIsPresentInFolderFromEmail(String count, String Subject, String folder, String fromEmail) throws Throwable {
        String mailFound = emailVerification.searchEmailSubject(Subject, folder, fromEmail );
    }

    @Then("^I verify mail count \"([^\"]*)\" with subject \"([^\"]*)\" is in folder \"([^\"]*)\" from email \"([^\"]*)\" in last \"([^\"]*)\" hr$")
    public void iVerifyMailCountWithSubjectIsInFolderFromEmailInLastHr(String count, String Subject, String folder, String fromEmail, int time) throws Throwable {
        String mailFound = emailVerification.totalEmailDuringParticularTime(Subject, folder, fromEmail, time);
    }

    @Then("^I verify mail count \"([^\"]*)\" with subject \"([^\"]*)\" and content \"([^\"]*)\" is in folder \"([^\"]*)\" from email \"([^\"]*)\"$")
    public void iVerifyMailCountWithSubjectAndContentIsInFolderFromEmail(String count, String Subject, String bodySearchText, String folder, String fromEmail) throws Throwable {
        String mailFound = emailVerification.searchEmailContent(Subject, bodySearchText, folder, fromEmail);
    }

    @Then("^I verify mail count \"([^\"]*)\" with subject \"([^\"]*)\" and content \"([^\"]*)\" is in folder \"([^\"]*)\" from email \"([^\"]*)\" in last \"([^\"]*)\" hr$")
    public void iVerifyMailCountWithSubjectAndContentIsInFolderFromEmailInLastHr(String count, String Subject, String bodySearchText, String folder, String fromEmail, int time) throws Throwable {
        String mailFound = emailVerification.totalEmailDuringParticularTime(Subject, bodySearchText, folder, fromEmail, time);
    }



}