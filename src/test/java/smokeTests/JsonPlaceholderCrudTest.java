package smokeTests;

import utils.base.BaseApi;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import static utils.helpers.PathsHelper.*;
import static utils.helpers.RequestsHelper.*;

import java.util.Map;

public class JsonPlaceholderCrudTest extends BaseApi {

  @Override
  protected String baseUrl() {
    return System.getProperty("JSONPH_BASE_URL",
            System.getenv().getOrDefault("JSONPH_BASE_URL", "https://jsonplaceholder.typicode.com"));
  }

  @Test
  public void createPost_POST_201() throws Exception {
    var res = postJson(api, JsonPh.posts(), Map.of("title", "foo", "body", "bar", "userId", 1));
    Assertions.assertThat(res.status()).isEqualTo(201);
  }

  @Test
  public void readPost_GET_200() throws Exception {
    var res = get(api, JsonPh.post(1));
    Assertions.assertThat(res.ok()).isTrue();
  }

  @Test
  public void updatePost_PUT_200() throws Exception {
    var res = putJson(api, JsonPh.post(1), Map.of("id", 1, "title", "baz", "body", "updated", "userId", 1));
    Assertions.assertThat(res.status()).isEqualTo(200);
  }

  @Test
  public void partialUpdatePost_PATCH_200() throws Exception {
    var res = patchJson(api, JsonPh.post(1), Map.of("title", "patched"));
    Assertions.assertThat(res.status()).isEqualTo(200);
  }

  @Test
  public void deletePost_DELETE_200() {
    var res = delete(api, JsonPh.post(1));
    Assertions.assertThat(res.status()).isIn(200, 204);
  }
}
