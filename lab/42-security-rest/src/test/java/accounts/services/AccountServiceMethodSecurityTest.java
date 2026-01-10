package accounts.services;

import accounts.RestWsApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

// TODO-12a: Perform method security testing with a running server
// - Take some time to understand what each test is for
// - Remove @Disabled annotation from each test and run it
// - Make sure all tests pass

// CHANGE: Removed @Disabled annotation from method security tests
// WHY: These tests verify that the @PreAuthorize annotation on getAuthoritiesForUser()
// is working correctly against a real running server. They test both success and failure
// scenarios for method-level authorization using TestRestTemplate with real HTTP requests.

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AccountServiceMethodSecurityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAuthoritiesForUser_should_return_403_for_user() {

        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth("user", "user")
                                                         .getForEntity("/authorities?username=user", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getAuthoritiesForUser_should_return_authorities_for_admin() {

        String[] authorities = restTemplate.withBasicAuth("admin", "admin")
                                           .getForObject("/authorities?username=admin", String[].class);
        assertThat(authorities.length).isEqualTo(2);
        assertThat(authorities.toString().contains("ROLE_ADMIN"));
        assertThat(authorities.toString().contains("ROLE_USER"));

    }

    // TODO-12b: Write a test that verifies that getting authorities
    //           using "/authorities?username=superadmin" with
    //           "superadmin"/"superadmin" credential should return
    //           three roles "ROLE_SUPERADMIN", "ROLE_ADMIN", and
    //           "ROLE_USER".
    @Test
    // CHANGE: Implemented method security test for SUPERADMIN role
    // WHY: This test verifies that the @PreAuthorize annotation permits SUPERADMIN users
    // to access their own authorities. SUPERADMIN has all three roles (USER, ADMIN, SUPERADMIN),
    // and both @PreAuthorize conditions are satisfied:
    // 1. hasRole('ADMIN') = true (SUPERADMIN inherits ADMIN role)
    // 2. #username == authentication.name = true (superadmin parameter matches authenticated username)
    // This demonstrates method-level authorization working end-to-end for the highest privilege user.
    public void getAuthoritiesForUser_should_return_authorities_for_superadmin() {
        // Call the authorities endpoint with superadmin credentials and their username parameter
        String[] authorities = restTemplate.withBasicAuth("superadmin", "superadmin")
                                           .getForObject("/authorities?username=superadmin", String[].class);
        
        // Verify three roles are returned
        assertThat(authorities.length).isEqualTo(3);
        assertThat(authorities.toString().contains("ROLE_SUPERADMIN"));
        assertThat(authorities.toString().contains("ROLE_ADMIN"));
        assertThat(authorities.toString().contains("ROLE_USER"));
    }

}