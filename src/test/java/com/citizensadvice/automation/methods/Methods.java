package com.citizensadvice.automation.methods;

import com.citizensadvice.automation.driver.Driver;
import com.citizensadvice.automation.helper.ElementHelper;
import com.citizensadvice.automation.helper.StoreHelper;
import com.citizensadvice.automation.model.ElementInfo;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class Methods extends Driver {

    private static Log4jLoggerAdapter logger = (Log4jLoggerAdapter) LoggerFactory
            .getLogger(Methods.class);

    private static String SAVED_ATTRIBUTE;
    WebDriver driver;
    FluentWait<WebDriver> wait;
    long waitElementTimeout;
    long pollingEveryValue;

    public Methods(){

        this.driver = Driver.driver;
        wait = setFluentWait(waitElementTimeout);

    }

    public FluentWait<WebDriver> setFluentWait(long timeout){

        FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver);
        fluentWait.withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(pollingEveryValue))
                .ignoring(NoSuchElementException.class);
        return fluentWait;
    }

    public ElementInfo getElementInfo(String key) {

        return StoreHelper.INSTANCE.findElementInfoByKey(key);
    }

    public By getBy(String key) {

        logger.info("Element is stored in " + key + " value");
        return ElementHelper.getElementInfoToBy(getElementInfo(key));
    }

    public List<String> getByValueAndSelectorType(By by){

        List<String> list = new ArrayList<String>();
        String[] values = by.toString().split(": ",2);
        list.add(values[1].trim());
        list.add(getSelectorTypeName(values[0].replace("By.","").trim()));
        return list;
    }

    public WebElement findElement(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    public List<WebElement> findElements(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        return driver.findElements(infoParam);
    }


    public void clickElement(String key) {

        findElement(key).click();
        logger.info("Click is completed");
    }


    public void waitElementIsVisible(String key) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(key)));
        logger.info("Element is visible");
    }


    public void sendKeys(String key, String text) {

        findElement(key).sendKeys(text);
        logger.info(text + " was sent to the element");
    }

    public void sendKeysAndEnter(String key, String text) {


        findElement(key).sendKeys(text,Keys.ENTER);
        logger.info(text + " was sent to the element and clicked ENTER");
    }

    public String getText(String key) {

        return findElement(key).getText();
    }

    public String getAttribute(String key, String attribute) {

        return findElement(key).getAttribute(attribute);
    }

    public String getPageSource() {

        return driver.getPageSource();
    }

    public String getCurrentUrl() {

        return driver.getCurrentUrl();
    }

    public List<String> listTabs() {
        List<String> list = new ArrayList<String>();
        for (String window : driver.getWindowHandles()) {
            list.add(window);
        }
        return list;
    }


    public void navigateTo(String url) {

        driver.navigate().to(url);
    }

    public void refreshPage(){
        driver.navigate().refresh();
    }

    public void scrollToElement(String key) {

        Actions actions = new Actions(driver);
        WebElement webElement = findElement(key);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
        actions.moveToElement(webElement).build().perform();
    }


    public void jsExecutor(String script, Object... args) {

        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript(script, args);
    }

    public void jsExecutorWithKey(String script, String key) {

        jsExecutor(script, findElement(key));
    }

    public void waitByMilliSeconds(long milliSeconds) {

        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitBySeconds(long seconds) {

        logger.info(seconds + " saniye bekleniyor...");
        waitByMilliSeconds(seconds * 1000);
    }

    public boolean isElementVisible(String key) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(key)));
            logger.info(key+" element is visible.");
            return true;
        } catch (Exception e) {
            logger.info("Element is not visible");
            return false;
        }

    }

    public boolean isElementVisible(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            logger.info("Web element is visible");
            return true;
        } catch (Exception e) {
            logger.info("Web element is not visible");
            return false;
        }

    }

    public boolean isElementInVisible(String key) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(getBy(key)));
            return true;
        } catch (Exception e) {
            logger.info("Element is visible");
            return false;
        }
    }

    public boolean isElementPresent(String key){
        WebDriverWait wait = new WebDriverWait(driver,10);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(getBy(key)));
            return true;
        } catch (Exception e) {
            logger.info("Element is presence");
            return false;
        }
    }

    public boolean isElementClickable(String key) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(getBy(key)));
            return true;
        } catch (Exception e) {
            logger.info("Element is not clickable");
            return false;
        }

    }
    public boolean isElementClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver,30);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return true;
        } catch (Exception e) {
            logger.info("Element is not clickable");
            return false;
        }

    }

    public WebElement findElement(By by){

        logger.info("Element " + by.toString() + " has by value");
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement findElementWithoutWait(By by){

        logger.info("Element " + by.toString() + " has by value");
        return driver.findElement(by);
    }



    private String getSelectorTypeName(String type){

        String selectorType = "";
        switch (type) {

            case "id":
                selectorType = "id";
                break;

            case "name":
                selectorType = "name";
                break;

            case "className":
                selectorType = "class";
                break;

            case "cssSelector":
                selectorType = "css";
                break;

            case "xpath":
                selectorType = "xpath";
                break;

            default:
                Assert.fail("HATA");
                break;
        }
        return selectorType;
    }

    public void waitUntilPresenceOfElement(String key){
        WebDriverWait wait = new WebDriverWait(driver,30);

        logger.info(key+" presence is waiting");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key))));

        }
        catch(WebDriverException ex)
        {
            wait.until(ExpectedConditions.presenceOfElementLocated(ElementHelper.getElementInfoToBy(StoreHelper.INSTANCE.findElementInfoByKey(key))));
        }

    }


}

