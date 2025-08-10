package api.pojo.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@NonNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {
    private Integer userId;
    private Integer id;
    private String title;
    private Object body;
}
