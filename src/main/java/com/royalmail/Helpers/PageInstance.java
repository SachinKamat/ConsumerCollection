package com.royalmail.Helpers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.cucumber.listener.ExtentProperties;
import cucumber.api.Scenario;
import org.json.simple.JSONArray;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class PageInstance {
     protected static String BARCODE = null;
     protected static String UID=null;
    protected static String Event=null;
    public static ExtentHtmlReporter htmlreporter;
    public static ExtentProperties extentProperties;
    public static ExtentReports extentreports;
     static final Path PTP_PATH = Paths.get("src/test/resources/Scripts/generatePTPMessage15SK.awk");
     static final Path GAWK_PATH = Paths.get("src/test/resources/Scripts/gawk.exe");
     static final Path XML_PATH_for_Input = Paths.get("src\\test\\resources\\Scripts\\TC99_4_PDA-071C-JC092GB.xml");
     static final String XML_PATH = new File("src/test/resources/Scripts/").getAbsolutePath().replace("\\","\\\\");


    static final Path RFHUTIL_PATH=Paths.get("src/test/resources/Scripts/RFHUtil/mqputsc.exe");
    static final Path INPUT_FILE=Paths.get("src\\test\\resources\\Scripts\\Paramfile_MQPUT_PDA_PTY1.txt");
    static final Path Result_Directory_path = Paths.get("src/test/resources/Scripts/Results");
    static final Path BAT_FILE = Paths.get("src\\test\\resources\\Scripts\\run.bat");
    public Properties prop = new Properties();
    public String propFileName = "deliverychange.properties";
    public InputStream inputStream;

    public static final Path BAT_Preadvice = Paths.get("run_preadvice.bat");
    public static final Path WINSCP_Install_DIR = Paths.get("C:\\Program Files (x86)\\WinSCP");
    public static final Path WINSCP_LOG=Paths.get("Winscp_output.log");
    public static final Path WinSCR_upload_file = Paths.get("winSCP_uploadScript.txt");
    public static final Path Json_Event_file = Paths.get("Json_event.json");
    public static final Path Json_Report_file = Paths.get("Json_report.json");
    public static final Path Json_redland_file = Paths.get("Json_redland.json");

    public String TIMESLOTS="";
    public String DateForOrder="";
    public String TaskID="";
    public String ORDERID="";
    public String DOID="";
    public String ROUTE="";

   // public static final Path
    static final String[] LocationName={"GreenFord(002626)","Chelmsford (002609)","Inverness (002629)","Southampton (002653)","Glasgow (002624)","Bristol (002604)","South Midlands (004554)","Northern Ireland Mail Centre(002599)"};
    static final String[] DOName={"Northolt (001355)","Hayes (001354)","Sudbury (000274)","Thurso (000654)","Winchester (001192)","Willesden (000914)","Wishaw (000808)","Thornbury (000135)","Atherstone (000300)"};
    static final String[] RDcName={"Princess Royal RDC (002673)","Scottish RDC (002677)","South West RDC (002675)","Atherstone Xmas RDC (0018769)","Atherstone RDC (0018815)"};
    static final String[] selectOption={"DO","IMC Mail Centre","RDC"};


    public static JSONArray BarcodeList = new JSONArray();
    public static JSONArray BarcodeRedlandList = new JSONArray();
    public static JSONArray ScenarioList = new JSONArray();
    protected  static Scenario scenario;
    public static HashMap<String,String>getReports=new HashMap<String,String>();

    public static WebDriver driver;
    static List<String> Bat_list = Arrays.asList("@echo off"," TITLE=MQPUT - PDA_PTY 1","REM","set ResultsDirectory=%1\\MQputs",
            "if \"%1\"==\"\" set ResultsDirectory="+Result_Directory_path.toAbsolutePath().toString(),"set datetime=","for /f \"skip=1 delims=\" %%x in ('wmic os get localdatetime') do if not defined datetime set datetime=%%x",
            "set outputLog=%ResultsDirectory%\\PDA_PTY_1_%datetime:~0,8%_%datetime:~8,6%_mqputs.txt","set MQSERVER=MQSUPPORT.SVRCONN/TCP/10.106.85.21(1420)",
       RFHUTIL_PATH.toAbsolutePath().toString()+" -f "+INPUT_FILE.toAbsolutePath().toString()+" > %outputLog%","exit");
    static List<String> Bat_Preadvice_list = Arrays.asList("@echo off","REM","CD \""+WINSCP_Install_DIR+"\"","\""+WINSCP_Install_DIR+"\\WinSCP.exe"+"\" /log=\""+WINSCP_LOG.toAbsolutePath()+"\" /ini=nul /script=\""+WinSCR_upload_file.toAbsolutePath()+"\"");



    public static void updateInputFile(){
        try {
            List<String> list = Arrays.asList("[header]", "* Input parameters for MQPut2 program *","** name of the queue and queue manager","* to write messages to","qname=PDAST.BIG.PTYSCAN.IN.03","qmgr=MQSUPPORT.SVRCONN/TCP/10.106.85.21(1420)",
                    "* total number of messages to be written","* the program will stop after this number of","* messages has been written","msgcount=1",
                    "qdepth=500","qmax=5000","sleeptime=10","thinktime=1000","tune=0","batchsize=1","format= \"MQHRF2 \"","priority=2","persist=1",
                    "msgtype=8","encoding=546","codepage=1208","delimiter=\"#@%@#\"","rfh=N","RFH_CCSID=1208","RFH_ENCODING=546","RFH_NAME_CCSID=1208",
                    "[filelist]", XML_PATH_for_Input.toAbsolutePath().toString());
            System.out.println("list "+list);
            Files.write(INPUT_FILE.toAbsolutePath(), list);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
