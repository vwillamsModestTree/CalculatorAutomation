# Title: Appium Test 1
# Author: Vistassja Williams
# Date: August 31, 2022

@AppiumTest1
Feature: Appium Test 1

@Addition
Scenario: Addition
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When I click on "one-button" windows element
And I click on "plus-button" windows element
And I click on "seven-button" windows element
And I click on "equals-button" windows element
Then I check that calculator result field is equal to "eight"
And I close windows application

@Subtraction
Scenario: Subtraction
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When I click on "nine-button" windows element
And I click on "minus-button" windows element
And I click on "one-button" windows element
And I click on "equals-button" windows element
And I check that calculator result field is equal to "eight"
And I close windows application

@Multiplication
Scenario: Multiplication
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When I click on "nine-button" windows element
And I click on "multiply-button" windows element
And I click on "nine-button" windows element
And I click on "equals-button" windows element
Then I check that calculator result field is equal to "eighty-one"
And I close windows application

@Division
Scenario: Division
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When I click on "eight-button" windows element
And I click on "divide-button" windows element
And I click on "one-button" windows element
And I click on "equals-button" windows element
Then I check that calculator result field is equal to "eight"
And I close windows application

@Combination
Scenario: Combination
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When I click on "seven-button" windows element
And I click on "multiply-button" windows element
And I click on "nine-button" windows element
And I click on "plus-button" windows element
And I click on "one-button" windows element
And I click on "equals-button" windows element
And I click on "divide-button" windows element
And I click on "eight-button" windows element
And I click on "equals-button" windows element
Then I check that calculator result field is equal to "eight"
And I close windows application


