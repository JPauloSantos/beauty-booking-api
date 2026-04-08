package com.beautyscheduler.bdd.steps;

import com.beautyscheduler.application.port.in.appointment.CreateAppointmentUseCase;
import com.beautyscheduler.application.port.in.auth.RegisterUserUseCase;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class AppointmentSteps {

    @Autowired
    private CreateAppointmentUseCase createAppointmentUseCase;

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    private Appointment lastCreatedAppointment;
    private Exception lastException;
    private UUID clientId;

    @Given("a registered client")
    public void aRegisteredClient() {
        User client = registerUserUseCase.execute(new RegisterUserUseCase.Command(
                "BDD Client", "bdd-client-" + UUID.randomUUID() + "@test.com",
                "password123", User.UserRole.CLIENT, "11999999999"));
        this.clientId = client.getId();
    }

    @When("the client books an appointment in the past")
    public void clientBooksInPast() {
        try {
            createAppointmentUseCase.execute(new CreateAppointmentUseCase.Command(
                    clientId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    LocalDateTime.now().minusDays(1), null));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the booking should be rejected")
    public void bookingShouldBeRejected() {
        assertThat(lastException).isNotNull();
    }
}
