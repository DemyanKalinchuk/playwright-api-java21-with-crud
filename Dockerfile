# Java 21 + Maven with caching
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

# 1) Prime dependency cache (faster builds)
COPY pom.xml .
RUN mvn -B -V -U -DskipTests dependency:go-offline

# 2) Bring in sources
COPY src ./src

# 3) Defaults that you can override via `docker run -e ...`
ENV BASE_URL="https://reqres.in" \
    ACCEPT_LANGUAGE="en-US" \
    REQRES_READ_API_KEY="reqres-free-v1" \
    API_CONSOLE_LOG="false" \
    RETRY_MAX="2" \
    TESTS="smokeTests.ApiSmokeTests,smokeTests.AuthRegisterTests,smokeTests.AuthSmokeTests"

# 4) Run tests at container runtime (not during build) so you can re-run without rebuilding
#    NOTE: our POM copies aspectjweaver.jar to target/ at `validate`, and Surefire reads it from there.
CMD bash -lc 'mvn -B -V -U validate test -Dtest="${TESTS}"'
