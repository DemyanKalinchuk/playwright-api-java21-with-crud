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
    utils.base.BaseApi.java
src/
  test/java/
    ApiSmokeTest.java
  test/resources/
    application.properties
```


## CRUD Suites Added
- `ReqResCrudTest` (users)
- `JsonPlaceholderCrudTest` (posts)

Run all tests:
```bash
mvn -q test
```
