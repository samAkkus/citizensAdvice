package com.citizensadvice.automation.step;

import com.citizensadvice.automation.driver.Driver;
import com.citizensadvice.automation.methods.Methods;
import com.thoughtworks.gauge.Step;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StepImplementation extends Driver {

    private Logger logger = LoggerFactory.getLogger(getClass());

    Methods methods = new Methods();


    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }


    @Step("Click <key> element with js")
    public void clickByElementWithJS(String key) {
        WebElement element = methods.findElement(key);
        getJSExecutor().executeScript("arguments[0].click();", element);
    }


    @Step("Check <by> element if it is visible")
    public void controlIsElementVisible(String key) {

        Assert.assertTrue("Element görünür değil", methods.isElementVisible(key));

    }

    @Step("Check <by> element if it is present")
    public void controlIsElementPresent(String key) {

        Assert.assertTrue("Element görünür değil", methods.isElementPresent(key));

    }



    @Step("See and verify the list of items on the main navigation header")
    public void listCheck(){
        List<WebElement> webElementList = methods.findElements("text_List_Of_Items");

        for (WebElement webElement : webElementList) {

            methods.isElementVisible(webElement);
            System.out.println(webElement.getText()+" is visible");
        }
    }

}


