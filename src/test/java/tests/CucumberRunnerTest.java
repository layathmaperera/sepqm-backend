package tests;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Member 4 – Cucumber TestNG Runner.
 * Extends AbstractTestNGCucumberTests (NOT JUnit runner).
 * Generates surefire HTML report via maven-surefire-report-plugin.
 */
@CucumberOptions(
        features = "src/test/resources/features/student_registration.feature",
        glue     = {"steps"},
        plugin   = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)
public class CucumberRunnerTest extends AbstractTestNGCucumberTests {

    /**
     * Override to allow parallel scenario execution (optional – remove if not needed).
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}

