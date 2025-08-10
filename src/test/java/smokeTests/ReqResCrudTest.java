package smokeTests;

import utils.base.BaseApi;
import utils.json.JsonUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import static utils.base.Base.testCred;
import static utils.helpers.PathsHelper.*;
import static utils.helpers.RequestsHelper.*;

import java.util.Map;

public class ReqResCrudTest extends BaseApi {

  @Override
  protected String baseUrl() {
    return System.getProperty("REQRES_BASE_URL",
            System.getenv().getOrDefault("REQRES_BASE_URL", testCred.baseApiUrl()));
  }


  @Test
  public void createUserTest() throws Exception {
    var res = postJson(api, ReqRes.users(), Map.of("name", "neo", "job", "the one"));
    Assertions.assertThat(res.status()).isEqualTo(201);
    var json = JsonUtils.readJson(res.body());
    Assertions.assertThat(json).containsEntry("name", "neo").containsKey("id");
  }

  @Test
  public void readUserTest() throws Exception {
    var res = get(api, ReqRes.user(2));
    Assertions.assertThat(res.ok()).isTrue();
    var json = JsonUtils.readJson(res.body());
    Assertions.assertThat(json).containsKey("data");
  }

  @Test
  public void updateUserTest() throws Exception {
    var res = putJson(api, ReqRes.user(2), Map.of("name", "trinity", "job", "hacker"));
    Assertions.assertThat(res.status()).isIn(200, 201);
  }

  @Test
  public void partialUpdateUserTest() throws Exception {
    var res = patchJson(api, ReqRes.user(2), Map.of("job", "zion-ops"));
    Assertions.assertThat(res.status()).isIn(200, 201);
  }

  @Test
  public void deleteUserTest() {
    var res = delete(api, ReqRes.user(2));
    Assertions.assertThat(res.status()).isIn(204, 200);
  }
}
