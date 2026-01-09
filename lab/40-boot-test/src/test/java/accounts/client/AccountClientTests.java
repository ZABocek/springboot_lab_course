package accounts.client;

import common.money.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.net.URI;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// TODO-00: In this lab, you are going to exercise the following:
// - Using @SpringBootTest and webEnvironment for end-to-end testing
//   (You are going to refactor the test code of previous lab of "38-rest-ws"
//    to use Spring Boot test framework.)
// - Understanding the different usage model of TestRestTemplate from RestTemplate
//    * Usage of a relative path rather than an absolute path
//    * Handling the 404 response from the service
// - Using MockMvc for Web slice testing
// - Understanding the difference between @MockBean and @Mock

// TODO-01: Make this class a Spring Boot test class
// - Add @SpringBootTest annotation with WebEnvironment.RANDOM_PORT
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// EXPLANATION: @SpringBootTest starts the entire Spring application context for integration testing.
// WebEnvironment.RANDOM_PORT starts the embedded servlet container (Tomcat) on a random available port,
// allowing true end-to-end testing of the REST endpoints.
public class AccountClientTests {

	// TODO-02: Autowire TestRestTemplate bean to a field
	// - Name the field as restTemplate
	@Autowired
	private TestRestTemplate restTemplate;
	// EXPLANATION: TestRestTemplate is a Spring Boot test utility that provides a convenient way to make
	// HTTP requests to the running test server. Unlike RestTemplate, it handles relative URLs and provides
	// better integration with test servers.

	// TODO-03: Update code below to use TestRestTemplate (as opposed to RestTemplate)
	// - Remove RestTemplate from this code
	// - Remove BASE_URL from this code or change the value of it to ""
	// - Run the tests and observe that they pass except
	//   "addAndDeleteBeneficiary" test
	//   (If you are using Maven, the tests will run as configured in pom.xml)

	/**
	 * Base URL changed to empty string since TestRestTemplate uses relative URLs
	 * to reach the running test server on its dynamically assigned port.
	 */
	private static final String BASE_URL = "";
	// EXPLANATION: TestRestTemplate automatically resolves relative URLs against the running test server,
	// so we don't need to specify the full URL with localhost:8080. The BASE_URL is now empty, and we'll
	// use relative paths like "/accounts" instead of "http://localhost:8080/accounts".

	private Random random = new Random();

	@Test
	public void listAccounts() {
		String url = BASE_URL + "/accounts";
		// we have to use Account[] instead of List<Account>, or Jackson won't know what
		// type to unmarshal to
		Account[] accounts = restTemplate.getForObject(url, Account[].class);
		assertThat(accounts.length >= 21).isTrue();
		assertThat(accounts[0].getName()).isEqualTo("Keith and Keri Donald");
		assertThat(accounts[0].getBeneficiaries().size()).isEqualTo(2);
		assertThat(accounts[0].getBeneficiary("Annabelle").getAllocationPercentage()).isEqualTo(Percentage.valueOf("50%"));
	}

	@Test
	public void getAccount() {
		String url = BASE_URL + "/accounts/{accountId}";
		Account account = restTemplate.getForObject(url, Account.class, 0);
		assertThat(account.getName()).isEqualTo("Keith and Keri Donald");
		assertThat(account.getBeneficiaries().size()).isEqualTo(2);
		assertThat(account.getBeneficiary("Annabelle").getAllocationPercentage()).isEqualTo(Percentage.valueOf("50%"));
	}

	@Test
	public void createAccount() {
		String url = BASE_URL + "/accounts";
		// use a random account number to avoid conflict
		String number = "12345%4d".formatted(random.nextInt(10000));
		Account account = new Account(number, "John Doe");
		account.addBeneficiary("Jane Doe");
		URI newAccountLocation = restTemplate.postForLocation(url, account);

		Account retrievedAccount = restTemplate.getForObject(newAccountLocation, Account.class);
		assertThat(retrievedAccount.getNumber()).isEqualTo(account.getNumber());

		Beneficiary accountBeneficiary = account.getBeneficiaries().iterator().next();
		Beneficiary retrievedAccountBeneficiary = retrievedAccount.getBeneficiaries().iterator().next();

		assertThat(retrievedAccountBeneficiary.getName()).isEqualTo(accountBeneficiary.getName());
		assertThat(retrievedAccount.getEntityId()).isNotNull();
	}

	// TODO-04: Modify the code below so that it handles 404 HTTP response status
	//          from the server (instead of handling it as an exception as in the
	//          case of RestTemplate)
	// - Remove the "assertThrows" statement (since you are not going to
	//   check if an exception is thrown)
	// - Use "getForEntity" method (instead of "getForObject" method) of
	//   "TestRestTemplate"
	// - Verify that the HTTP response status is 404
	// - Run all tests - they should all pass
	@Test
	public void addAndDeleteBeneficiary() {
		// perform both add and delete to avoid issues with side effects
		String addUrl = BASE_URL + "/accounts/{accountId}/beneficiaries";
		URI newBeneficiaryLocation = restTemplate.postForLocation(addUrl, "David", 1);
		Beneficiary newBeneficiary = restTemplate.getForObject(newBeneficiaryLocation, Beneficiary.class);
		assertThat(newBeneficiary.getName()).isEqualTo("David");

		restTemplate.delete(newBeneficiaryLocation);

		// EXPLANATION: Instead of using getForObject which throws an exception on 4xx/5xx responses,
		// we use getForEntity which returns a ResponseEntity with the status code. This allows us to
		// check the HTTP status code (404) without catching an exception, making the test more readable.
		var response = restTemplate.getForEntity(newBeneficiaryLocation, Beneficiary.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// TODO-05: Observe a log message in the console indicating
	//          Tomcat started as part of testing
	// - Search for "Tomcat started on port(s):"
	// - Note how long it takes for this test to finish - it is
	//   in the range of several seconds

}
