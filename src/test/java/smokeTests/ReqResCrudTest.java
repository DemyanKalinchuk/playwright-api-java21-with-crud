package smokeTests;

import api.steps.UseApiSteps;
import utils.base.BaseApi;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

public class ReqResCrudTest extends BaseApi {

  private final UseApiSteps steps = new UseApiSteps();

  @Test
  public void userCrudTest() {
    var created = steps.createUser("Morpheus", "Smith", "morph@example.com", "leader");
    var id = io.restassured.path.json.JsonPath.from(created).getString("id");
    assertNotNull(id);

    var updated = steps.updateUser(id, "Morpheus", "Smith", "morph@example.com", "tester");
    assertTrue(updated.contains("updatedAt"));

    var fetched = steps.getUser(id, token());
    assertTrue(fetched.contains(id));

    var deleted = steps.deleteUser(id);
    assertNotNull(deleted); // some APIs return empty body with 204
  }

  @Test
  public void postsCrudTest() {
    String created = steps.createPost(2, "A title", "Body", token());
    String id = io.restassured.path.json.JsonPath.from(created).getString("id");
    assertNotNull(id);

    String fetched = steps.getPost(id);
    assertTrue(fetched.contains(id));
  }
}
