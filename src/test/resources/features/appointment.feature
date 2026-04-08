Feature: Appointment booking

  Scenario: Client cannot book an appointment in the past
    Given a registered client
    When the client books an appointment in the past
    Then the booking should be rejected
