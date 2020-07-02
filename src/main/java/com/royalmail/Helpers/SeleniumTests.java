package com.royalmail.Helpers;

import com.cucumber.listener.Reporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumTests extends PageInstance {
    String driverPath = "Tools\\chromedriver.exe";

    @FindBy(id = "dnn_ctr448_ViewQuickModule_QuickModule448_report_txtBarcode")
    private WebElement BARCODETEXTBOX;

    @FindBy(id = "dnn_ctr448_ViewQuickModule_QuickModule448_report_btnViewReport")
    private WebElement BARCODESUBMITBUTTON;

    @FindBy(xpath="//*[contains(@id, 'ReportCell')]/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr[10]/td/table")
    private WebElement mytable;

    @BeforeTest
    public void setup(){
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://www.trackedpreproduction.co.uk/trackedplusuat/barcode-details");
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("Barcode Detail"));
    }
    public void inputBarcode(String Barcode) throws InterruptedException {
        PageFactory.initElements(driver,this);
        BARCODETEXTBOX.clear();
        BARCODETEXTBOX.sendKeys(Barcode);
        BARCODESUBMITBUTTON.click();
    }

    public void validateBarcode(String Scan) throws IOException {
        try {
            boolean validateScan = false;
            PageFactory.initElements(driver, this);
            //To locate rows of table.
            List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));
            //To calculate no of rows In table.
            int rows_count = rows_table.size();
            //Loop will execute till the last row of table.
            for (WebElement element : rows_table) {
                //To locate columns(cells) of that specific row.
                List<WebElement> Columns_row = element.findElements(By.tagName("td"));
                //To calculate no of columns (cells). In that specific row.
                int columns_count = Columns_row.size();
                //Loop will execute till the last cell of that specific row.
                for (WebElement webElement : Columns_row) {
                    // To retrieve text from that specific cell.
                    String celtext = webElement.getText();
                    if (celtext.contains(Scan)) {
                        validateScan = true;
                    }
                }
            }
            Assert.assertTrue(validateScan);
            Reporter.addStepLog("Passed Scan "+Scan);
        }catch (AssertionError ae){
            ae.printStackTrace();
            Reporter.addStepLog("Failed Scan "+Scan);
        }

    }
    public static String capture(WebDriver driver, String screenShotName) throws IOException
    {
        TakesScreenshot ts = (TakesScreenshot)driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String dest = System.getProperty("user.dir") +"\\Screenshots\\"+screenShotName+".png";
        File destination = new File(dest);
        FileUtils.copyFile(source, destination);

        return dest;
    }
    public void ExitSession(){
        driver.quit();
    }
}
