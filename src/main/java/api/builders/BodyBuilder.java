package api.builders;

import api.pojo.post.Comment;
import api.pojo.post.Post;
import api.pojo.users.Address;
import api.pojo.users.Name;
import api.pojo.users.User;

import java.time.Instant;
import java.util.UUID;

public class BodyBuilder {

    // ===== Users =====
    public Name buildName(String first, String last) {
        return Name.builder()
                .first(first)
                .last(last)
                .build();
    }

    public Address buildAddress(String street, String suite, String city, String zipcode) {
        return Address.builder().street(street).suite(suite).city(city).zipcode(zipcode).build();
    }

    /** Minimal “create user” payload often used in ReqRes-like APIs */
    public User buildNewUser(String first, String last, String email, String job) {
        return User.builder()
                .name(buildName(first, last))
                .email(email)
                .job(job)
                .createdAt(String.valueOf(Instant.now()))
                .build();
    }

    /** Example of a full user (e.g., for PUT/GET roundtrip assertions) */
    public User buildFullUser(String id, String first, String last, String email, String job, Address address) {
        return User.builder()
                .id(id)
                .name(buildName(first, last))
                .email(email)
                .job(job)
                .address(address)
                .createdAt(String.valueOf(Instant.now()))
                .updatedAt(String.valueOf(Instant.now()))
                .build();
    }

    /** Convenience with generated ID */
    public User buildFullUser(String first, String last, String email, String job, Address address) {
        return buildFullUser(UUID.randomUUID().toString(), first, last, email, job, address);
    }


    // ===== Posts =====
    public Post buildPost(Integer userId, String title, String body) {
        return Post.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .build();
    }

    public Comment buildComment(Integer postId, String name, String email, String body) {
        return Comment.builder().postId(postId).name(name).email(email).body(body).build();
    }
}
