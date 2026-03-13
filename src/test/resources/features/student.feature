Feature: Student Management API
  As a user of the Student API
  I want to be able to manage students
  So that I can keep track of enrolled users

  Scenario: Create and retrieve a student successfully
    Given the system has no students
    When I create a student with name "David" and email "david@test.com"
    Then the response status should be 201
    And the returned student should have the name "David"

  Scenario: Get a non-existent student
    Given the system has no students
    When I request a student with ID 999
    Then the response status should be 404
