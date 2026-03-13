Feature: Student Registration API
  As an admin
  I want to register students via the API
  So that I can manage the student roster

  # ── Scenario 1: Successful registration (201) ─────────────────────────────
  Scenario: Successful student registration
    Given no students exist in the system
    When I register a student with name "Alice" email "alice@uni.com" age 22 course "SE3010"
    Then the registration response status should be 201
    And the registered student name should be "Alice"

  # ── Scenario 2: Duplicate email (409) ─────────────────────────────────────
  Scenario: Duplicate email returns conflict error
    Given a student with email "bob@uni.com" already exists
    When I register a student with name "Bob2" email "bob@uni.com" age 24 course "SE3020"
    Then the registration response status should be 409

  # ── Scenario 3: Missing required field (400) ──────────────────────────────
  Scenario: Missing required field returns bad request error
    Given no students exist in the system
    When I send a registration request with malformed JSON body
    Then the registration response status should be 400

