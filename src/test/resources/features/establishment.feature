Feature: Establishment management

  Scenario: Search establishments by city
    Given establishments exist in "São Paulo"
    When I search for establishments in "São Paulo"
    Then I should receive a list of establishments
