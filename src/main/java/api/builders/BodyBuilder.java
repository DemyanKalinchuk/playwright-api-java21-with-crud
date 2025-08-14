package api.builders;

import api.pojo.auth.dto.AuthRequest;
import api.pojo.post.Post;
import api.pojo.users.Address;
import api.pojo.users.Name;
import api.pojo.users.User;

import java.time.Instant;

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

    public User buildNewUser(String first, String last, String email, String job) {
        return User.builder()
                .name(buildName(first, last))
                .email(email)
                .job(job)
                .createdAt(String.valueOf(Instant.now()))
                .build();
    }

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


    // ===== Posts =====
    public Post buildPost(Integer postId,  String title, String body) {
        return Post.builder()
                .id(postId)
                .title(title)
                .body(body)
                .build();
    }


    // ====== Auth
    public AuthRequest buildAuthRequest(String email, String password) {
        return AuthRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
