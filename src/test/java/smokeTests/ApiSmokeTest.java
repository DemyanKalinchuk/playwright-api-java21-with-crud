package smokeTests;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import utils.base.BaseApi;
import utils.json.JsonUtils;

import static utils.helpers.RequestsHelper.*;
import java.util.Map;

import static utils.helpers.RequestsHelper.postJson;

public class ApiSmokeTest extends BaseApi {

  @Test
  public void getUserLIstTest() throws Exception {
    var res = get(api, PathsHelper.ReqRes.users(), Map.of("page", 2));
    Assertions.assertThat(res.ok()).isTrue();

    var json = JsonUtils.readJson(res.body());
    Assertions.assertThat(json).containsKey("data");
    Assertions.assertThat(JsonUtils.jsonPath(json, "data", java.util.List.class)).isNotNull();
  }

  @Test
  public void createUserAndCheckTheBodyTest() throws Exception {
    var res = postJson(api, PathsHelper.ReqRes.users(), Map.of("name", "morpheus", "job", "leader"));
    Assertions.assertThat(res.status()).isEqualTo(201);

    var json = JsonUtils.readJson(res.body());
    Assertions.assertThat(json).containsEntry("name", "morpheus");
  }
}