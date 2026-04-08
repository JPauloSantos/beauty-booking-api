package com.beautyscheduler.bdd.steps;

import com.beautyscheduler.application.port.in.establishment.CreateEstablishmentUseCase;
import com.beautyscheduler.application.port.in.establishment.SearchEstablishmentUseCase;
import com.beautyscheduler.domain.entity.Establishment;
import com.beautyscheduler.domain.valueobject.Address;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class EstablishmentSteps {

    @Autowired private CreateEstablishmentUseCase createEstablishmentUseCase;
    @Autowired private SearchEstablishmentUseCase searchEstablishmentUseCase;

    private List<Establishment> searchResults;

    @Given("establishments exist in {string}")
    public void establishmentsExistIn(String city) {
        Address address = new Address("Rua das Flores", "100", null,
                "Centro", city, "SP", "01310-100", null, null);
        createEstablishmentUseCase.execute(
                new CreateEstablishmentUseCase.Command("Salão BDD", "Descrição", address, UUID.randomUUID()));
    }

    @When("I search for establishments in {string}")
    public void iSearchEstablishmentsIn(String city) {
        searchResults = searchEstablishmentUseCase.search(
                new SearchEstablishmentUseCase.Filters(null, city, null, null, null, null));
    }

    @Then("I should receive a list of establishments")
    public void shouldReceiveList() {
        assertThat(searchResults).isNotEmpty();
    }
}
