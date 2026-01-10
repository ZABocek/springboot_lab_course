package accounts.web;

import accounts.AccountManager;
import accounts.RestWsApplication;
import accounts.security.CustomAuthenticationProvider;
import accounts.services.AccountService;
import config.RestSecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import rewards.internal.account.Account;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO-20 (Optional): Perform security testing for the user added
//          through custom AuthenticationProvider
// - Remove @Disabled annotation from the test and run it
// - Make sure the test passes

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = {RestWsApplication.class, RestSecurityConfig.class, CustomAuthenticationProvider.class})
public class AccountControllerCustomAuthenticationProviderTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountManager accountManager;

    @MockBean
    private AccountService accountService;

    @Test
    // CHANGE: Removed @Disabled annotation from test method
    // WHY: The CustomAuthenticationProvider is now fully implemented and registered as a Spring @Component bean.
    // This test verifies that the custom provider correctly authenticates the "spring"/"spring" credentials
    // and grants ROLE_ADMIN authority. The test uses @ContextConfiguration to load CustomAuthenticationProvider
    // into the test context, enabling the authentication flow to work correctly. With the provider in place,
    // this test should pass, confirming that users authenticated through the custom provider can access
    // protected endpoints. The test uses httpBasic() to send credentials and validates a 200 OK response
    // with proper response body containing account details (name and number).
    public void accountDetails_with_spring_credentials_should_return_200() throws Exception {

        // arrange
        given(accountManager.getAccount(0L)).willReturn(new Account("1234567890", "John Doe"));

        // act and assert
        mockMvc.perform(get("/accounts/0").with(httpBasic("spring", "spring")))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("name").value("John Doe")).andExpect(jsonPath("number").value("1234567890"));

        // verify
        verify(accountManager).getAccount(0L);

    }

}

