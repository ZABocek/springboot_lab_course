package accounts.web;

import accounts.internal.StubAccountManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rewards.internal.account.Account;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A JUnit test case testing the AccountController.
 */
public class AccountControllerTests {

	private static final long EXPECTED_ACCOUNT_ID = StubAccountManager.TEST_ACCOUNT_ID;
	private static final String EXPECTED_ACCOUNT_NUMBER = StubAccountManager.TEST_ACCOUNT_NUMBER;

	private AccountController controller;

	@BeforeEach
	public void setUp() {
		controller = new AccountController(new StubAccountManager());
	}

	// TODO-07: Remove the @Disabled annotation, run the test, it should now pass.
	@Test
	public void testHandleListRequest() {
		List<Account> accounts = controller.accountList();

		// Non-empty list containing the one and only test account
		assertNotNull(accounts);
		assertEquals(1, accounts.size());

		// Validate that account
		Account account = accounts.getFirst();
		assertEquals(EXPECTED_ACCOUNT_ID, (long) account.getEntityId());
		assertEquals(EXPECTED_ACCOUNT_NUMBER, account.getNumber());
	}

	// TODO-10a: Remove the @Disabled annotation, run the test, it should pass.
	@Test
	public void testHandleDetailsRequest() {
		Account account = controller.accountDetails((int) EXPECTED_ACCOUNT_ID);

		assertNotNull(account);
		assertEquals(EXPECTED_ACCOUNT_ID, (long) account.getEntityId());
		assertEquals(EXPECTED_ACCOUNT_NUMBER, account.getNumber());
	}

}
