package utils.base;

import core.cred.TestInitValues;
import core.TestProperties;
import core.cred.ProdStageProperties;
import core.cred.TestStageProperties;
import lombok.Getter;
import lombok.Setter;
import org.aeonbits.owner.ConfigFactory;
import org.testng.IConfigurable;
import org.testng.IConfigureCallBack;
import org.testng.ITestResult;
import utils.helpers.retryHelper.Retriable;

import static core.TestStepLogger.log;

@Getter
@Setter

public class Base implements IConfigurable {

    public static final TestProperties testProperties = ConfigFactory.create(TestProperties.class);
    public static final TestInitValues testCred;


    static {
        testCred = selectStaging();
    }

    public static TestInitValues selectStaging() {
        TestInitValues t;
        switch (testProperties.staging()) {
            case "test":
                t = ConfigFactory.create(TestStageProperties.class);
                return t;
            case "prod":
                t = ConfigFactory.create(ProdStageProperties.class);
                return t;
            default:
                log("CURRENT ENVIRONMENT IS NOT SET" + " " + "This value is wrong" + " - " + testProperties.staging());
                System.exit(1);
                t = null;
                return t;
        }
    }

    @Override
    public void run(IConfigureCallBack callBack, ITestResult testResult) {
        Retriable retriable = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Retriable.class);
        int attempts = 1;
        if (retriable != null) {
            attempts = retriable.attempts();
        }
        for (int attempt = 1; attempt <= attempts; attempt++) {
            callBack.runConfigurationMethod(testResult);
            if (testResult.getThrowable() == null) {
                break;
            }
        }
    }

}
