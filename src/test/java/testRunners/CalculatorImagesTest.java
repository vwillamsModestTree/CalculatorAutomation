package testRunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
features = "src/test/java/featureFiles"
,glue={"stepdefinitions"}, tags="@CalculatorImages", stepNotifications=true

)

public class CalculatorImagesTest {

}
