# Title: Appium Test 1
# Author: Vistassja Williams
# Date: April 6, 2023

@CalculatorImages
Feature: Appium Test 1

@Addition
Scenario: Addition
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
And "oneImg" image should be displayed
When I click on windows element using image "oneImg"
And "plusImg" image should be displayed
And I click on windows element using image "plusImg"
And "sevenImg" image should be displayed
And I click on windows element using image "sevenImg"
And "equalsImg" image should be displayed
And I click on windows element using image "equalsImg"
Then I check that calculator result field is equal to "eight"
And I close windows application

@Subtraction
Scenario: Subtraction
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
And "nineImg" image should be displayed
When I click on windows element using image "nineImg"
And "minusImg" image should be displayed
And I click on windows element using image "minusImg"
And "oneImg" image should be displayed
And I click on windows element using image "oneImg"
And "equalsImg" image should be displayed
And I click on windows element using image "equalsImg"
And I check that calculator result field is equal to "eight"
And I close windows application

@Multiplication
Scenario: Multiplication
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When "nineImg" image should be displayed
When I click on windows element using image "nineImg"
And "multiplyImg" image should be displayed
And I click on windows element using image "multiplyImg"
And "nineImg" image should be displayed
And I click on windows element using image "nineImg"
And "equalsImg" image should be displayed
And I click on windows element using image "equalsImg"
Then I check that calculator result field is equal to "eighty-one"
And I close windows application

@Division
Scenario: Division
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When "eightImg" image should be displayed
When I click on windows element using image "eightImg"
When "divideImg" image should be displayed
And I click on windows element using image "divideImg"
When "oneImg" image should be displayed
And I click on windows element using image "oneImg"
When "equalsImg" image should be displayed
And I click on windows element using image "equalsImg"
Then I check that calculator result field is equal to "eight"
And I close windows application

@Combination
Scenario: Combination
Given I open application with name "WindowsCalculator"
And I verify calculator result field is not null
When I click on windows element using image "sevenImg"
And I click on windows element using image "multiplyImg"
And I click on windows element using image "nineImg"
And I click on windows element using image "plusImg"
And I click on windows element using image "oneImg"
And I click on windows element using image "equalsImg"
And I click on windows element using image "divideImg"
And I click on windows element using image "eightImg"
And I click on windows element using image "equalsImg"
Then I check that calculator result field is equal to "eight"
And I close windows application


