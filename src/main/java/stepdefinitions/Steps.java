package stepdefinitions;

import java.awt.Desktop.Action;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.imageio.ImageIO;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.TakesScreenshot;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.imagecomparison.*;
import io.appium.java_client.windows.WindowsDriver;

public class Steps {

	private static WindowsDriver appSession = null;
	private static WebElement calculatorResult = null;
	private static Properties objectLocators = new Properties();
	private static Properties testData = new Properties();
	private static FileInputStream objectFile;
	private static FileInputStream dataFile;
	public static String image;

	private Steps() {
		throw new IllegalStateException("Utility class");
	}

	/*
	 * Opens data files, and sets capabilities and settings of the driver. This step
	 * *must* always be the first step in the feature file.
	 */
	@Given("I open application with name {string}")
	public static void openWindowsApplication(String app) {
		try {
			String objectFileLocation = "";
			String dataFileLocation = "";

			if (app.equals("WindowsCalculator")) {
				objectFileLocation = System.getProperty("user.dir")
						+ "\\src\\test\\java\\objectLocators\\object.properties";
				dataFileLocation = System.getProperty("user.dir") + "\\src\\test\\java\\dataFiles\\data.properties";
			} else {
				throw new IllegalArgumentException(
						"The application name you have chosen does not have data files in the test suite");
			}

			// Clear the old data just in case this function gets called twice without
			// closeApplication() being explicitly called.
			closeWindowsApplication();

			// Get objects and data that will be used in the steps.
			objectFile = new FileInputStream(objectFileLocation);
			dataFile = new FileInputStream(dataFileLocation);
			objectLocators.load(objectFile);
			testData.load(dataFile);

			// Get the name of the app and add it to the capabilities
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("app", testData.getProperty(app));
			capabilities.setCapability("unicodeKeyboard", "true");

			// Add the images plugin to the capabilities
			capabilities.setCapability("plugins", "images");

			// Connect the Appium client to the Appium server to open the application and
			// add the capabilities
			appSession = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);

			// This allows Appium to locate and tap on image elements if their initial
			// positions have changed between the time you find it and the time you interact
			// with it. (For example, when the screen suddenly maximizes)
			appSession.setSetting("autoUpdateImageElementPosition", true);

			// Increasing this over this amount causes the elements not to be detected.
			appSession.setSetting("imageMatchThreshold", 0.4);

			// Allows me to see the visualization of the image that was matched on the
			// screen when I debug.
			appSession.setSetting("getMatchedImageResult", true);

			// These two were enabled to produce a more accurate matching of the reference
			// image (screenshot) the live application image
			appSession.setSetting("fixImageTemplateSize", true);
			appSession.setSetting("fixImageTemplateScale", true);

			maxWindow();
			appSession.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		} catch (Exception e) {
			System.err.println("openWindowsApplication() has exception " + e.getMessage());
			e.printStackTrace();

		} finally {
			try {
				objectFile.close();
				dataFile.close();
				System.out.println("Successfully closed input streams for data and object files!");

			} catch (Exception e) {
				System.err.println("openWindowsApplication() has exception " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/*
	 * Finds an element using the images plugin, determines if it is currently
	 * displayed on the screen and if found, saves the visual of the found element
	 * in the src\byteImages folder.
	 */
	@Given("{string} image should be displayed")
	public static void imageElementShouldBeDisplayed(String img) {
		try {
			System.out.println("imageElementShouldBeDisplayed(): The windows img is |" + img + "|");
			WebElement element = findElementByImage(img);
			Assert.assertNotNull(element);

			// Try to avoid race conditions and the StaleElementReferenceException.
			if (element != null) {
				Assert.assertTrue(findElementByImage(img).isDisplayed());
				saveByteImage(element.getAttribute("visual").getBytes(), img + ".png");
			}
		} catch (Exception e) {
			System.err.println("imageElementShouldBeDisplayed() has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Finds an element using the images plugin then clicks on that element.
	 */
	@And("I click on windows element using image {string}")
	public static void clickWindowsButtonUsingImage(String img) {
		try {
			System.out.println("clickWindowsButtonUsingImage(): The windows img is |" + img + "|");
			WebElement element = findElementByImage(img);
			Assert.assertNotNull(element);

			int attempts = 1;
			if (element != null) {
				while (attempts < 6) {
					try {
						findElementByImage(img).click();
						break;
					} catch (StaleElementReferenceException e) {
						System.out.println("Exception on attempt " + attempts);
					}
					attempts++;
				}
			}
		} catch (Exception e) {
			System.err.println("clickWindowsButtonUsingImage(" + img + ") has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Takes a screenshot of the current page and saves it in src\screenshots.
	 */
	@And("I take a screenshot and save with name {string}")
	public static void iTakeAScreenshotAndSaveWithName(String fileName) {
		try {
			File srcFile = ((TakesScreenshot) appSession).getScreenshotAs(OutputType.FILE);
			FileHandler.copy(srcFile,
					new File(System.getProperty("user.dir") + "\\src\\screenshots\\" + fileName + ".png"));
		} catch (Exception e) {
			System.err.println("iTakeAScreenshotAndSaveWithName() has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Takes a screenshot of the current window of the live application and compares
	 * that with a previously taken screenshot.
	 */
	@And("I take a screenshot and compare with other image {string}")
	public static void iTakeAScreenshotAndCompareWithOtherImage(String imgToCompare) {
		try {

			System.out.println("iTakeAScreenshotAndCompareWithOtherImage():");

			System.out.println("################# Image to Compare: " + imgToCompare);

			// There's also an option to use the File class for these instead. While I think
			// it may
			// require more storage, it may allow me to debug easier if I ever need to in
			// the future.
			byte[] screenshot = Base64.getEncoder().encode(appSession.getScreenshotAs(OutputType.BYTES));
			byte[] originalImg = getRefImageAsBytes(imgToCompare);
			double similarityScore = 0;

			// Save the images that will be compared for debugging:
			saveByteImage(screenshot, "ScreenshotToCompare.png");
			saveByteImage(originalImg, "OrigImageToCompare.png");

			// Get the similarity result and enable the visualization (for debugging)
			SimilarityMatchingResult result = appSession.getImagesSimilarity(screenshot, originalImg,
					new SimilarityMatchingOptions().withEnabledVisualization());

			// The similarity score is a float number in range [0.0, 1.0]. 1.0 is the
			// highest score (means both images are totally equal).
			similarityScore = result.getScore();
			System.out
					.println("iTakeAScreenshotAndCompareWithOtherImage(): The similarity score is " + similarityScore);

			if (result.getVisualization() != null) {
				saveByteImage(result.getVisualization(), imgToCompare + "_Visualization.png");
			}

			Assert.assertTrue(similarityScore > 0.95);

			System.out.println("##################################################");
		} catch (Exception e) {
			System.err.println("iTakeAScreenshotAndCompareWithOtherImage() has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Maximizes the current window.
	 */
	@And("I maximize window")
	public static void maxWindow() {
		try {
			appSession.manage().window().maximize();
		} catch (Exception e) {
			System.err.println("maxwindow() has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Finds an element by its name (object locator) and then clicks on it.
	 */
	@When("I click on {string} windows element")
	public static void clickWindowsButton(String elementName) {
		try {
			System.out.println("clickWindowsButton(): The windows element name is |" + elementName + "|");
			WebElement element = findElementByName(elementName);
			Assert.assertNotNull(element);
			if (element != null) {
				element.click();
			}
		} catch (Exception e) {
			System.err.println("clickWindowsButton(" + elementName + ") has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Finds an element using a name object locator and determines if it is
	 * displayed on the screen.
	 */
	@Given("{string} should be displayed")
	public static void elementShouldBeDisplayed(String elementName) {
		try {
			WebElement element = findElementByName(elementName);
			Assert.assertNotNull(element);
			if (element != null) {
				Assert.assertTrue(element.isDisplayed());
			}
		} catch (Exception e) {
			System.err.println("enterIntoInputField() has exception " + e.getMessage());
			e.printStackTrace();
			// Close the application to provide a fresh slate for the other tests.
			closeWindowsApplication();
		}
	}

	/*
	 * Closes the application.
	 */
	@Then("I close windows application")
	public static void closeWindowsApplication() {
		try {
			calculatorResult = null;
			if (appSession != null) {
				appSession.quit();
			}
			appSession = null;
			objectLocators.clear();
			testData.clear();
		} catch (Exception e) {
			System.err.println("closeWindowsApplication() has exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Finds an element on the current window using an image. refImage must be a
	 * data property that has a value that is a path to the image file.
	 */
	protected static WebElement findElementByImage(String refImage) {
		WebElement returnElement = null;
		try {

			String imageToFind = getRefImageAsString(refImage);

			System.out.println("findElementByImage(): Starting to wait for " + refImage);
			// Waiting 30 seconds for an element to be present on the page, checking
			// for its presence once every 5 seconds.
			Wait<WindowsDriver> wait = new FluentWait<WindowsDriver>(appSession).withTimeout(Duration.ofSeconds(120))
					.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

			returnElement = wait.until(new Function<WindowsDriver, WebElement>() {
				public WebElement apply(WindowsDriver driver) {
					System.out.println("findElementByImage() wait loop: Stil looking for " + refImage);
					return driver.findElement(AppiumBy.image(imageToFind));
				}
			});

		} catch (Exception e) {

			System.err.println("findElementByImage() has exception " + e.getMessage());
			System.err.println("findElementByImage() was trying to find " + refImage);

			e.printStackTrace();
		}
		return returnElement;
	}

	/*
	 * Finds an element using a name object locator.
	 */
	protected static WebElement findElementByName(String elementName) {
		WebElement returnElement = null;
		if (objectLocators != null && elementName != null) {
			try {
				System.out.println("findElementByName(): The elementName to find is " + elementName
						+ " and the object locator of that element is |" + objectLocators.getProperty(elementName)
						+ "|");
				returnElement = appSession.findElement(By.name(objectLocators.getProperty(elementName)));
			} catch (Exception e) {
				System.err.println("findElementByName() has exception " + e.getMessage());
				e.printStackTrace();
				returnElement = null;
				// Close the application to provide a fresh slate for the other tests.
				closeWindowsApplication();
			}
		}
		return returnElement;
	}

	/*
	 * Gets a data value from a .properties file.
	 */
	protected static String getDataValue(String data) {
		if (testData != null && data != null) {
			return testData.getProperty(data);
		}
		return null;
	}

	/*
	 * Gets the path of the image as it is in the .properties file, reads the bytes
	 * of that file, then returns a base64 encoded String representation of that
	 * file.
	 */
	protected static String getRefImageAsString(String refImg) {
		String toReturn = null;
		try {
			Path path = getImagePath(refImg);

			// Reads the image bytes using the image path, and converts the bytes into a
			// String using the Base64 encoding scheme.
			toReturn = Base64.getEncoder().encodeToString(Files.readAllBytes(path));
		} catch (Exception e) {
			System.err.println("getRefImageAsString() has exception " + e.getMessage());
			e.printStackTrace();
		}
		return toReturn;
	}

	/*
	 * Gets the path of the image as it is in the .properties file, reads the bytes
	 * of that file, then returns an encoded Base64 byte array
	 */
	protected static byte[] getRefImageAsBytes(String refImg) {
		byte[] toReturn = null;
		try {
			Path path = getImagePath(refImg);

			System.out.println("getRefImageAsBytes(): The path is|" + path.toString() + "|");

			// Reads the image bytes using the image path, and encodes the bytes using the
			// Base64 encoding scheme
			toReturn = Base64.getEncoder().encode(Files.readAllBytes(path));
		} catch (Exception e) {
			System.err.println("getRefImageAsBytes() has exception " + e.getMessage());
			e.printStackTrace();
		}
		return toReturn;
	}

	/*
	 * Returns the path of an image in the test data file. I think maybe this
	 * requires rework, because this function could be used for other non-image
	 * properties as well.
	 */
	protected static Path getImagePath(String refImg) {
		Path path = null;
		try {
			// Get the image path that the data property points to and append the project
			// file directory
			String imagePath = System.getProperty("user.dir") + testData.getProperty(refImg);
			File file = new File(imagePath);
			path = file.toPath();

			System.out.println("getRefImageAsString(): The refImg is|" + refImg + "| and the imagePath is |" + imagePath
					+ "|" + "and the computed Path is " + "|" + path.toString() + "|");
		} catch (Exception e) {
			System.err.println("getImagePath() has exception " + e.getMessage());
			e.printStackTrace();
		}
		return path;
	}

	/*
	 * Takes an base64 encoded byte array and saves it as a .png image file. I am
	 * mostly using this function as a debugging function, so that I can view the
	 * encode images that iTakeAScreenshotAndCompareWithOtherImage is creating.
	 */
	protected static void saveByteImage(byte[] encodedImgByteArray, String fileName) {
		try {
			byte[] decodedImgByteArray = Base64.getDecoder().decode(encodedImgByteArray);
			ByteArrayInputStream inStreambj = new ByteArrayInputStream(decodedImgByteArray);
			BufferedImage newImage = ImageIO.read(inStreambj);

			fileName = System.getProperty("user.dir") + "\\src\\byteImages\\" + fileName;
			ImageIO.write(newImage, "png", new File(fileName));
			System.out.println("saveByteImage(): Image generated from the byte array.");
		} catch (Exception e) {
			System.err.println("saveByteImage() has exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Given("I verify calculator result field is not null")
	public static void verifyResultField() {
		try {
			// Accessibility ID = Automation ID
			calculatorResult = appSession.findElement(AppiumBy.accessibilityId("CalculatorResults"));
			Assert.assertNotNull(calculatorResult);
		} catch (Exception e) {
			System.err.println("verifyResultField() has exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Then("I check that calculator result field is equal to {string}")
	public static void checkCalculatorResult(String checkNum) {
		try {
			Assert.assertEquals(testData.getProperty(checkNum), getCalculatorResultText());
		} catch (Exception e) {
			System.err.println("checkCalculatorResult() has exception " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected static String getCalculatorResultText() {
		// trim extra text and whitespace off of the display value
		System.out.println("getCalculatorResultText(): The calculator result is "
				+ calculatorResult.getText().replace("Display is", "").trim());
		return calculatorResult.getText().replace("Display is", "").trim();
	}
}
