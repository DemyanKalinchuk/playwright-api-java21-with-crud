package api.pojo.users;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NonNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Name {
    private String first;
    private String last;
}
