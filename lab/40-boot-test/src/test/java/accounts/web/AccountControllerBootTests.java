package accounts.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import accounts.AccountManager;
import rewards.internal.account.Account;

// TODO-06: Get yourself familiarized with various testing utility classes
// - Uncomment the import statements below
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// EXPLANATION: These static imports provide convenient access to MockMvc request builders (get, post, etc.),
// result matchers (status, content, jsonPath), and Mockito BDD-style mocking methods (given, willReturn, etc.).

// TODO-07: Replace @ExtendWith(SpringExtension.class) with the following annotation
// - @WebMvcTest(AccountController.class) // includes @ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
// EXPLANATION: @WebMvcTest is a Spring Boot annotation that loads only the web layer (controllers, filters, etc.)
// for testing, omitting service and repository layers. This makes tests faster and more focused. It automatically
// includes @ExtendWith(SpringExtension.class), so we don't need it separately. It also provides a MockMvc bean.
public class AccountControllerBootTests {

	// TODO-08: Autowire MockMvc bean
	@Autowired
	private MockMvc mockMvc;
	// EXPLANATION: MockMvc is a Spring test utility that allows testing the web layer without starting a real server.
	// It simulates HTTP requests and responses, and provides assertion methods to verify the controller behavior.

	// TODO-09: Create AccountManager mock bean using @MockBean annotation
	@MockBean
	private AccountManager accountManager;
	// EXPLANATION: @MockBean creates a Mockito mock of AccountManager and registers it in the Spring test context.
	// Unlike @Mock (which requires MockitoAnnotations.openMocks(this)), @MockBean integrates the mock directly
	// into the Spring application context, making it available for dependency injection into the controller.

	// TODO-10: Write positive test for GET request for an account
	// - Uncomment the code and run the test and verify it succeeds
	@Test
	public void accountDetails() throws Exception {

		given(accountManager.getAccount(0L))
			.willReturn(new Account("1234567890", "John Doe"));

		mockMvc.perform(get("/accounts/0"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("name").value("John Doe"))
			.andExpect(jsonPath("number").value("1234567890"));

		verify(accountManager).getAccount(0L);
		// EXPLANATION: This test uses BDD-style mocking (given/willReturn) to configure the mock, then verifies
		// that the controller returns the expected JSON response with the correct status and field values.

	}

	// TODO-11: Write negative test for GET request for a non-existent account
	// - Uncomment the "given" and "verify" statements
	// - Write code between the "given" and "verify" statements
	// - Run the test and verify it succeeds
	@Test
	public void accountDetailsFail() throws Exception {

		given(accountManager.getAccount(any(Long.class)))
			.willThrow(new IllegalArgumentException("No such account with id " + 0L));

		// (Write code here)
		// - Use mockMvc to perform HTTP Get operation using "/accounts/9999"
        //   as a non-existent account URL
		// - Verify that the HTTP response status is 404
		mockMvc.perform(get("/accounts/9999"))
			.andExpect(status().isNotFound());
		// EXPLANATION: We test the error handling path by configuring the mock to throw an exception,
		// then verify that MockMvc receives a 404 status. This tests the controller's exception handling logic.

		verify(accountManager).getAccount(any(Long.class));

	}

    // TODO-12: Write test for `POST` request for an account
	// - Uncomment Java code below
	// - Write code between the "given" and "verify" statements
	// - Run the test and verify it succeeds
	@Test
	public void createAccount() throws Exception {

		Account testAccount = new Account("1234512345", "Mary Jones");
		testAccount.setEntityId(21L);

		given(accountManager.save(any(Account.class)))
			.willReturn(testAccount);

		// (Write code here)
		// Use mockMvc to perform HTTP Post operation to "/accounts"
		// - Set the request content type to APPLICATION_JSON
		// - Set the request content with Json string of the "testAccount"
		//   (Use "asJsonString" method below to convert the "testAccount"
		//   object into Json string)
		// - Verify that the response status is 201
		// - Verify that the response "Location" header contains "http://localhost/accounts/21"
		mockMvc.perform(post("/accounts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(testAccount)))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "http://localhost/accounts/21"));
		// EXPLANATION: This test verifies POST behavior by sending a JSON request body to "/accounts",
		// then asserting that the response is 201 Created with the correct Location header pointing
		// to the newly created resource.

		verify(accountManager).save(any(Account.class));
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// TODO-13 (Optional): Experiment with @MockBean vs @Mock
	// - Change `@MockBean` to `@Mock` for the `AccountManager dependency above
	// - Run the test and observe a test failure
	// - Change it back to `@MockBean`

}
