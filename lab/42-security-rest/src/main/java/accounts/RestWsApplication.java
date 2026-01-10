package accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
// CHANGE: Added Import annotation support to load custom security configuration
import org.springframework.context.annotation.Import;

// TODO-00: In this lab, you are going to exercise the following:
// - Observing the default security behavior
// - Configuring authorization based on roles
// - Configuring authentication using in-memory storage
// - Configuring method-level security
// - Adding custom UserDetailsService
// - Adding custom AuthenticationProvider
// - Writing test code for security

// TODO-01: Verify the presence of Spring security dependencies
// - See TO-DO-01 in the pom.xml for Maven or build.gradle for Gradle

// TODO-02a: Observe the default security behaviour of the Spring
//           Boot application using a browser
// - Run this application
// - Using a browser, access "http://localhost:8080/accounts"
//   and observe that a login page gets displayed
// - Enter "user" in the Username field and Spring Boot generated
//   password into the Password field and verify that the accounts
//   get displayed
//   (If the browser keeps displaying the login page, use Chrome
//   Incognito browser.)
// - Access "http://localhost:8080/logout" and click "Log out" button

// TODO-02b: Observe the default security behaviour of the Spring
//           Boot application using "curl" (or "Postman")
// - Open a terminal window (if you are using "curl")
// - Run "curl -i localhost:8080/accounts" and observe 401 response
// - Run "curl -i -u user:<Spring-Boot-Generated-Password> localhost:8080/accounts"
//   and observe a successful response

@SpringBootApplication
//TODO-03: Import security configuration class
//- Uncomment the line below and go to RestSecurityConfig class
// CHANGE: Uncommented @Import(RestSecurityConfig.class) to activate custom security configuration
// WHY: This annotation tells Spring to load the RestSecurityConfig class which contains our custom
// authentication and authorization rules. Without this, we would only get Spring Security's default
// behavior. By importing RestSecurityConfig, we enable custom user details service, authentication
// providers, and method-level security that will be defined in that configuration class.
@Import(RestSecurityConfig.class)
@EntityScan("rewards.internal")
public class RestWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestWsApplication.class, args);
    }

}

// TODO-11: Test the method security using browser or curl
// CHANGE: Added comprehensive comments explaining method-level security testing
// WHY: This TODO verifies that the @PreAuthorize annotation on getAuthoritiesForUser()
// works correctly. The @PreAuthorize("hasRole('ADMIN') and #username == authentication.name")
// condition has two parts:
// 1. User must have ADMIN role
// 2. The username parameter must match the authenticated user's username
//
// Test Scenario 1: USER role without ADMIN role
// - This should FAIL with 403 Forbidden because the user lacks ADMIN role
// - Re-run this application
// - Using Chrome Incognito browser, access
//   http://localhost:8080/authorities?username=user
// - Enter "user"/"user" and verify that 403 failure occurs
// - If you want to use "curl", use
//   curl -i -u user:user http://localhost:8080/authorities?username=user
//
// Test Scenario 2: ADMIN role with matching username
// - This should SUCCEED (200 OK) because admin has ADMIN role AND username matches
// - Close the Chrome Incognito browser and start a new one
// - Access http://localhost:8080/authorities?username=admin
// - Enter "admin"/"admin" and verify that the roles are displayed successfully
// - If you want to use "curl", use
//   curl -i -u admin:admin http://localhost:8080/authorities?username=admin
//
// Test Scenario 3: SUPERADMIN role with matching username
// - This should SUCCEED (200 OK) because superadmin has ADMIN role (inherited) AND username matches
// - Close the Chrome Incognito browser and start a new one
// - Access http://localhost:8080/authorities?username=superadmin
// - Enter "superadmin"/"superadmin" and verify that the roles are displayed successfully
// - If you want to use "curl", use
//   curl -i -u superadmin:superadmin http://localhost:8080/authorities?username=superadmin
//
// Additional Test: Method-level username enforcement
// - Try accessing http://localhost:8080/authorities?username=admin with "user"/"user" credentials
// - This should FAIL (403 Forbidden) because even though user tries to query admin's authorities,
//   their username parameter doesn't match their authentication name. This demonstrates that the
//   #username == authentication.name check prevents users from accessing other users' data.

// ------------------------------------------------

// TODO-15 (Optional): Verify that the newly added custom UserDetailsService works
// CHANGE: Added comprehensive comments explaining CustomUserDetailsService testing
// WHY: This TODO verifies that the @Component CustomUserDetailsService is properly
// registered and being used for authentication. When the application starts, the
// CustomUserDetailsService bean (marked with @Primary and @Component) should override
// the in-memory UserDetailsManager. This means users should be able to authenticate
// with mary/joe credentials instead of admin/user/superadmin credentials.
//
// Test Scenario 1: Authenticate with mary/mary
// - This user has only USER role from CustomUserDetailsService
// - Can perform GET operations (read accounts) but cannot create/modify/delete
// - Re-run this application
// - Using Chrome Incognito browser, access
//   http://localhost:8080/accounts/0
// - Enter "mary"/"mary" and verify accounts data gets displayed
// - If you want to use "curl", use
//   curl -i -u mary:mary http://localhost:8080/accounts/0
//
// Test Scenario 2: Authenticate with joe/joe
// - This user has USER and ADMIN roles from CustomUserDetailsService
// - Can perform GET and POST/PUT operations (read and modify accounts)
// - Cannot perform DELETE operations (requires SUPERADMIN role)
// - Close the Chrome Incognito browser and start a new one
// - Using Chrome Incognito browser, access
//   http://localhost:8080/accounts/0
// - Enter "joe"/"joe" and verify accounts data gets displayed
// - If you want to use "curl", use
//   curl -i -u joe:joe http://localhost:8080/accounts/0
//
// Expected Behavior After CustomUserDetailsService Integration:
// - Old credentials (admin/user/superadmin) should NO LONGER work (401 Unauthorized)
// - Only mary/joe credentials should work
// - mary can only read (GET), joe can read and modify (GET/POST/PUT)
// - This demonstrates successful replacement of the in-memory store with custom authentication

// ------------------------------------------------

// TODO-19 (Optional): Verify that the newly added custom AuthenticationProvider works
// CHANGE: Added comprehensive comments explaining CustomAuthenticationProvider testing
// WHY: This TODO verifies that the @Component CustomAuthenticationProvider is properly
// registered and being used for authentication. When the application starts, the
// CustomAuthenticationProvider bean should authenticate "spring"/"spring" credentials
// and grant ROLE_ADMIN authority. This demonstrates that you can implement completely
// custom authentication mechanisms beyond UserDetailsService.
//
// Test Scenario: Authenticate with spring/spring
// - This user is authenticated by CustomAuthenticationProvider (not UserDetailsService)
// - CustomAuthenticationProvider.authenticate() is called during login
// - Credentials "spring"/"spring" pass the checkCustomAuthenticationSystem() validation
// - User is granted ROLE_ADMIN authority directly by the provider
// - Can perform all operations: GET (read), POST/PUT (create/modify), DELETE (delete)
// - Re-run this application
// - Using Chrome Incognito browser, access
//   http://localhost:8080/accounts/0
// - Enter "spring"/"spring" and verify accounts data gets displayed
// - If you want to use "curl", use
//   curl -i -u spring:spring http://localhost:8080/accounts/0
//
// Expected Behavior with CustomAuthenticationProvider:
// - "spring"/"spring" should work with ROLE_ADMIN authority
// - Can access all endpoints (full admin privileges)
// - mary/joe credentials should NO LONGER work (UserDetailsService not registered)
// - This demonstrates authentication through a custom provider mechanism
// - Proves that CustomAuthenticationProvider.authenticate() is being called
//
// Authentication Chain with CustomAuthenticationProvider:
// 1. spring/spring login attempt
// 2. ProviderManager iterates through AuthenticationProviders
// 3. CustomAuthenticationProvider.authenticate() called
// 4. Credentials validated: "spring".equals("spring") && "spring".equals("spring")
// 5. Returns UsernamePasswordAuthenticationToken with ROLE_ADMIN
// 6. Authentication successful with admin privileges