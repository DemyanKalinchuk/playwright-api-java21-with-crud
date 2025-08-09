package utils.base;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseApi {
  protected static Playwright playwright;   // shared per class
  protected APIRequestContext api;          // fresh per test

  /** Override in subclasses when you need a different host. */
  protected String baseUrl() {
    String env = System.getProperty("BASE_URL", System.getenv("BASE_URL"));
    if (env != null && !env.isBlank()) return env;
    try (var in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      if (in != null) {
        Properties p = new Properties();
        p.load(in);
        String fromProps = p.getProperty("BASE_URL");
        if (fromProps != null && !fromProps.isBlank()) return fromProps;
      }
    } catch (Exception ignored) {}
    return "https://reqres.in"; // sane default
  }

  /** Optional bearer token from env/props. */
  protected String token() {
    String t = System.getProperty("API_TOKEN", System.getenv("API_TOKEN"));
    return t == null ? "" : t;
  }

  @BeforeClass
  public void beforeClass() { playwright = Playwright.create(); }

  @AfterClass(alwaysRun = true)
  public void afterClass() { if (playwright != null) playwright.close(); }

  @BeforeMethod
  public void setUpApi() {
    var opts = new APIRequest.NewContextOptions().setBaseURL(baseUrl());
    String token = token();
    if (!token.isBlank()) {
      Map<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + token);
      opts.setExtraHTTPHeaders(headers);
    }
    api = playwright.request().newContext(opts);
  }

  @AfterMethod(alwaysRun = true)
  public void tearDownApi() { if (api != null) api.dispose(); }
}
