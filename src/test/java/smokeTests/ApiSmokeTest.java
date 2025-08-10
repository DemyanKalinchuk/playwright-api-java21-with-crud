package smokeTests;

import api.steps.UseApiSteps;
import org.testng.annotations.Test;
import utils.base.BaseApi;

import static org.testng.Assert.*;

public class ApiSmokeTest extends BaseApi{
  private final UseApiSteps steps = new UseApiSteps();

    @Test
    public void userCrudTest() {

      // Create user
      String createdUser = steps.createUserAndGetId("Morpheus", "Smith", "morph@example.com", "leader");
      assertNotNull(createdUser, "Created user must have id");

      // Update user
      String updated = steps.updateUser(createdUser, "Morpheus", "Smith", "morph@example.com", "zion resident");
      assertTrue(updated.contains("updatedAt"));

      // Get user
      String fetched = steps.getUser(createdUser, token());
      assertTrue(fetched.contains(createdUser));

      // Delete user
      String deleted = steps.deleteUser(createdUser);
      assertNotNull(deleted);
    }
  }