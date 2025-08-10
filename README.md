# Playwright API (Java 21)

A minimal Java 21 + Maven + Playwright project focused on **API testing** with TestNG.

## Quick start

```bash
# 1) Run API tests
mvn -q -Dtest=ApiSmokeTest test
```

You can override config via environment variables:

- `BASE_URL` — API base URL (default: `https://reqres.in`)
- `API_TOKEN` — optional bearer token

## Structure

```
src/
  main/java/
    - utils.base.BaseApi.java
    - utils.helpers.RequestsHelper.java
                   .PathsHelper
                   .HttpHelper           
    - utils.helpers.retryHelpers.RetryListener.java
                                .Retry.java
                                .Retriable.java
    - utils.json.JsonUtils.java
    - utils.AllureUtils.java
    
  main/resources/
    - log4j.properties
    
src/
  test/java/
    - ApiSmokeTest.java
    - ReqResCrudTest.java
    - JsonPlaceholderCrudTest.java
  test/resources/
    - application.properties
    - log4j2.xml
    
    src
 └─ main
    └─ java
       └─ api
          ├─ pojo
          │  ├─ users
          │  │  ├─ Name.java
          │  │  ├─ Address.java
          │  │  └─ User.java
          │  └─ posts
          │     ├─ Post.java
          │     └─ Comment.java
          └─ builders
             └─ PojoBuilders.java
```


## CRUD Suites Added
- `ReqResCrudTest` (users)
- `JsonPlaceholderCrudTest` (posts)

Run all tests:
```bash
mvn -q test
```
