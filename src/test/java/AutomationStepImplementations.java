import com.thoughtworks.gauge.Step;
import com.citizensadvice.automation.driver.Driver;
import com.citizensadvice.automation.methods.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomationStepImplementations extends Driver {

    private Logger logger = LoggerFactory.getLogger(getClass());

    Methods methods = new Methods();


    @Step("Send <text> to <input_Email> element")
    public void sendKeysToElement(String text, String key) {

        methods.waitElementIsVisible(key);
        methods.sendKeys(key, text);

    }

    @Step("Send <text> to <input_Email> element and click ENTER")
    public void sendKeysToElementAndEnter(String text, String key) {

        methods.sendKeysAndEnter(key, text);

    }

    @Step("Click on <btn_Login> element")
    public void clickToElement(String key) {

        methods.waitUntilPresenceOfElement(key);
        methods.clickElement(key);

    }




}
