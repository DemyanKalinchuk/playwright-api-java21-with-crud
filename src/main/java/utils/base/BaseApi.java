package utils.base;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.*;

import static core.TestStepLogger.logPreConditionStep;
import static core.TestStepLogger.resetCounters;
import static utils.base.Base.testCred;

public abstract class BaseApi {
  protected static Playwright playwright;
  protected APIRequestContext api;

  protected String getBaseUrl() {
    logPreConditionStep("Init data for BaseUrl");
    String fromProps = testCred.baseApiUrl();
    if (fromProps != null && !fromProps.isBlank()) return fromProps;

    return testCred.baseApiUrl();
  }

  public String getToken() {
    String token = testCred.baseApiToken();
    return token == null ? "" : token;
  }

  @BeforeClass(alwaysRun = true)
  public void beforeClass() {
    logPreConditionStep("Init Playwright");
    playwright = Playwright.create();
  }

  @AfterMethod(alwaysRun = true)
  public void resetCountersForSteps() {
    resetCounters();
  }
}