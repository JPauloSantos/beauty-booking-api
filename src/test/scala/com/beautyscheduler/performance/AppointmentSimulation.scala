package com.beautyscheduler.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AppointmentSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val loginBody =
    """{"email":"loadtest@example.com","password":"password123"}"""

  val authFeeder = Iterator.continually(Map(
    "email" -> s"user${scala.util.Random.nextInt(1000)}@test.com"
  ))

  val searchScenario = scenario("Search Establishments")
    .exec(
      http("Search by city")
        .get("/api/v1/establishments/search")
        .queryParam("city", "São Paulo")
        .check(status.is(200))
    )

  val authScenario = scenario("Login")
    .exec(
      http("Register user")
        .post("/api/v1/auth/register")
        .body(StringBody(
          """{"name":"Load User","email":"loadtest@example.com",
            |"password":"password123","role":"CLIENT","phone":"11999999999"}""".stripMargin
        ))
        .check(status.in(201, 422))
    )
    .exec(
      http("Authenticate")
        .post("/api/v1/auth/login")
        .body(StringBody(loginBody))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("authToken"))
    )

  setUp(
    searchScenario.inject(
      rampUsers(50).during(10.seconds),
      constantUsersPerSec(20).during(30.seconds)
    ),
    authScenario.inject(
      rampUsers(20).during(15.seconds)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.max.lt(2000),
     global.successfulRequests.percent.gt(95)
   )
}
