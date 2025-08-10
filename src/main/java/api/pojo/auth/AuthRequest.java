package api.pojo.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@NonNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequest {
    private String email;
    private String password;
}
