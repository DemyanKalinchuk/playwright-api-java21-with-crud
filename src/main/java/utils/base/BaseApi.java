package utils.base;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.*;

import static core.TestStepLogger.logPreconditionStep;
import static utils.base.Base.testCred;

public abstract class BaseApi {
  protected static Playwright playwright;
  protected APIRequestContext api;

  /**
   * Override in subclasses when you need a different host.
   */
  protected String baseUrl() {
    logPreconditionStep("Init data for BaseUrl");
    String fromProps = testCred.baseApiUrl();
    if (fromProps != null && !fromProps.isBlank()) return fromProps;

    return testCred.baseApiUrl();
  }

  /**
   * Optional bearer token from env/props (if some endpoints need it).
   */
  public String token() {
    String token = testCred.baseApiToken();
    return token == null ? "" : token;
  }

  @BeforeClass(alwaysRun = true)
  public void beforeClass() {
    logPreconditionStep("Init Playwright");
    playwright = Playwright.create();
  }
}