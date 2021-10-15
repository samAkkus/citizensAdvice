package com.citizensadvice.automation.driver;

import com.citizensadvice.automation.helper.ElementHelper;
import com.citizensadvice.automation.helper.StoreHelper;
import com.citizensadvice.automation.methods.Methods;
import com.citizensadvice.automation.step.StepImplementation;
import com.citizensadvice.automation.utils.ReadProperties;
import com.thoughtworks.gauge.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.log4j.Logger.getLogger;
import static org.apache.log4j.Logger.getRootLogger;

public class Driver {

    private static final Logger logger = LoggerFactory.getLogger(Driver.class);
    public static String browserName;
    public static boolean isFullScreen;
    public static WebDriver driver;
    public static String baseUrl;
    public static String osName = FindOS.getOperationSystemName();
    public static ResourceBundle ConfigurationProp = ReadProperties.readProp("Configuration.properties");
    public static String platformName;
    public static ConcurrentHashMap<String,Object> TestMap;
    public static String slash = osName.equals("WINDOWS") ? "\\": "/";
    public static String TestCaseName = "";
    public static String TestClassName = "";
    public static String userDir = System.getProperty("user.dir");
    public static boolean chromeZoomCondition = false;
    public static boolean firefoxZoomCondition = false;
    public static boolean isSafari = false;
    public static boolean zoomCondition = false;

    @BeforeSuite
    public void beforeSuite(ExecutionContext executionContext) {

        logger.info("*************************************************************************");
        logger.info("------------------------TEST PLAN-------------------------");
        System.out.println("\r\n");
        beforePlan();
    }

    @BeforeSpec
    public void beforeSpec(ExecutionContext executionContext) {

        logger.info("=========================================================================");
        logger.info("------------------------SPEC-------------------------");
        String fileName = executionContext.getCurrentSpecification().getFileName();
        TestClassName = fileName.replace(userDir,"");
        logger.info("SPEC FILE NAME: " + fileName);
        logger.info("SPEC NAME: " + executionContext.getCurrentSpecification().getName());
        logger.info("SPEC TAGS: " + executionContext.getCurrentSpecification().getTags());
        System.out.println("\r\n");
    }

    @BeforeScenario
    public void beforeScenario(ExecutionContext executionContext) throws MalformedURLException, Exception {

        logger.info("_________________________________________________________________________");
        logger.info("------------------------SCENARIO-------------------------");
        TestCaseName = executionContext.getCurrentScenario().getName();
        logger.info("SCENARIO NAME: " + TestCaseName);
        logger.info("SCENARIO TAG: " + executionContext.getCurrentScenario().getTags().toString());
        System.out.println("\r\n");
        beforeTest();
    }

    @BeforeStep
    public void beforeStep(ExecutionContext executionContext) {

        logger.info("═════════  " + executionContext.getCurrentStep().getDynamicText() + "  ═════════");
    }

    @AfterStep
    public void afterStep(ExecutionContext executionContext) throws IOException {

        if (executionContext.getCurrentStep().getIsFailing()) {

            logger.error(executionContext.getCurrentSpecification().getFileName());
            //logger.error(executionContext.getCurrentStep().getStackTrace()); // 0.6.5
            logger.error("Message: " + executionContext.getCurrentStep().getErrorMessage() + "\r\n"
                    + executionContext.getCurrentStep().getStackTrace());
        }
        logger.info("══════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("\r\n");
    }

    @AfterScenario
    public void afterScenario(ExecutionContext executionContext) {

        afterTest();
        if (executionContext.getCurrentScenario().getIsFailing()) {

            logger.info("TEST FAILED");
        } else {

            logger.info("TEST SUCCESSFUL");
        }

        logger.info("_________________________________________________________________________");
        System.out.println("\r\n");
    }

    @AfterSpec
    public void afterSpec(ExecutionContext executionContext) {

        logger.info("=========================================================================");
        System.out.println("\r\n");
    }

    @AfterSuite
    public void afterSuite(ExecutionContext executionContext) {

        afterPlan();
        logger.info("*************************************************************************");
        System.out.println("\r\n");
    }

    public void beforePlan(){

        String dir = "/src/test/resources/log4j.properties";
        if(!slash.equals("/")) {
            dir = dir.replace("/", "\\");
        }
        PropertyConfigurator.configure(userDir + dir);
        String logLevel = ConfigurationProp.getString("logLevel");
        getRootLogger().setLevel(Level.toLevel(logLevel));

        if(!logLevel.equals("ALL")) {
            String methodsClassLogLevel = ConfigurationProp.getString("methodsClassLogLevel");
            String elementHelperLogLevel = ConfigurationProp.getString("elementHelperLogLevel");
            getLogger(Driver.class).setLevel(Level.ALL);
            getLogger(LocalBrowserExec.class).setLevel(Level.ALL);
            getLogger(FindOS.class).setLevel(Level.ALL);
            getLogger(StepImplementation.class).setLevel(Level.ALL);
            getLogger(StoreHelper.class).setLevel(Level.toLevel(elementHelperLogLevel));
            getLogger(ElementHelper.class).setLevel(Level.toLevel(elementHelperLogLevel));
            getLogger(Methods.class).setLevel(Level.toLevel(methodsClassLogLevel));
        }
    }

    public void beforeTest(){

        TestMap = new ConcurrentHashMap<String, Object>();

        try {
            createDriver();
        }catch (Throwable e) {

            StackTraceElement[] stackTraceElements = e.getStackTrace();
            String error = e.toString() + "\r\n";
            for (int i = 0; i < stackTraceElements.length; i++) {

                error = error + "\r\n" + stackTraceElements[i].toString();
            }
            throw new SessionNotCreatedException(error);
        }
    }

    public void afterTest() {

        if (Boolean.parseBoolean(ConfigurationProp.getString("localQuitDriverActive"))) {
            //quitDriver();
        }
    }


    public void afterPlan(){

        System.out.println("");
    }

    public void createDriver() throws Exception {

        String key = System.getenv("key");
        browserName = ConfigurationProp.getString("browserName");
        baseUrl = ConfigurationProp.getString("baseUrl");

        isFullScreen = Boolean.parseBoolean(ConfigurationProp.getString("isFullScreen"));
        if(StringUtils.isEmpty(key)) {
            platformName = FindOS.getOperationSystemNameExpanded();
            driver = LocalBrowserExec.LocalExec(browserName);
        }
        else {
            logger.info("Local browser is not available to run");
        }

        logger.info("Driver is up and running");
        isSafari = browserName.equalsIgnoreCase("safari");
        zoomCondition = (browserName.equalsIgnoreCase("chrome") && chromeZoomCondition)
                || (browserName.equalsIgnoreCase("firefox") && firefoxZoomCondition);
        driver.get(baseUrl);
    }

    public void quitDriver() {

        if(driver != null){
            driver.quit();
            logger.info("Quit driver");
        }
    }


}
