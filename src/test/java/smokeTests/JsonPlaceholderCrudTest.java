package smokeTests;

import api.steps.UseApiSteps;
import utils.base.BaseApi;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

public class JsonPlaceholderCrudTest extends BaseApi {

  private final UseApiSteps steps = new UseApiSteps();

  @Test
  public void postsCrudTest() {
    // Create
    String created = steps.createPostAndGetId(1, "Hello world", "My first post", token());
    assertNotNull(created, "Created post must return id");

    // Read
    String fetched = steps.getPost(created);
    assertTrue(fetched.contains(created), "Fetched post must contain id");

    // Update (PUT)
    String put = steps.updatePostPut(created, 1, "Updated title", "Updated body");
    assertTrue(put.contains("Updated title"));

    // Patch (simulated via PUT in helper; swap to PATCH once added)
    String patched = steps.updatePostPatch(created, "Patched title");
    assertTrue(patched.contains("Patched title"));

    // Delete
    String deleted = steps.deletePost(created);
    assertNotNull(deleted);
  }
}
