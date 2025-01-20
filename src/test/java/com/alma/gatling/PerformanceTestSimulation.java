package com.alma.gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PerformanceTestSimulation extends Simulation {

    // Parametri dinamici
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("users", "10"));
    private static final int RAMP_UP_TIME = Integer.parseInt(System.getProperty("rampUp", "10"));
    private static final int DURATION = Integer.parseInt(System.getProperty("duration", "30"));

    // Protocol HTTP
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://jsonplaceholder.typicode.com")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Scenariul de test
    ScenarioBuilder scn = scenario("API Performance Test")
            .exec(http("GET Request")
                    .get("/posts/1")
                    .header("Custom-Header", "Gatling-Test"))
            .pause(Duration.ofSeconds(1))
            .exec(http("POST Request")
                    .post("/posts")
                    .body(StringBody("{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }"))
                    .asJson());

    {
        setUp(
                scn.injectOpen(
                        rampUsers(USER_COUNT).during(Duration.ofSeconds(RAMP_UP_TIME)),
                        constantUsersPerSec(USER_COUNT).during(Duration.ofSeconds(DURATION))
                )
        ).protocols(httpProtocol);
    }
}
