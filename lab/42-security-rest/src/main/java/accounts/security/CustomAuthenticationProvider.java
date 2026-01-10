package accounts.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

//TODO-17 (Optional): Create custom AuthenticationProvider
//- Note that it needs to implement AuthenticationProvider interface
//- Uncomment the commented code fragment below so that this custom
//AuthenticationProvider handles a user with the following credentials
//- "spring"/"spring" with "ROLE_ADMIN" role

// CHANGE: Created custom AuthenticationProvider implementing AuthenticationProvider interface
// WHY: An AuthenticationProvider is a Spring Security component that handles the actual
// authentication logic. Unlike UserDetailsService which only loads user details, an
// AuthenticationProvider can implement completely custom authentication logic. In this case,
// it authenticates the "spring"/"spring" user against a custom authentication system
// (simulated by checkCustomAuthenticationSystem). This demonstrates pluggable authentication
// mechanisms - you can authenticate against LDAP, external APIs, custom databases, etc.
// The authenticate() method processes the authentication request and returns an authenticated
// Authentication object with ROLE_ADMIN authority.

//TODO-18a (Optional): Add authentication based upon the custom AuthenticationProvider
//- Annotate the class with @Component to make it a Spring manager bean

// CHANGE: Added @Component annotation to register as Spring-managed bean
// WHY: The @Component annotation makes this class a Spring bean that Spring Security
// will auto-detect and register as an AuthenticationProvider. Spring Security maintains
// a list of AuthenticationProviders and tries each one during authentication. By registering
// this as a @Component, it becomes part of the authentication chain. When a user attempts
// to login, Spring Security will call authenticate() on all registered providers until one
// successfully authenticates the user.
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		// CHANGE: Uncommented custom authentication logic
		// WHY: This code implements the core authentication mechanism. It:
		// 1. Extracts username and password from the authentication request
		// 2. Validates them against a custom authentication system
		// 3. Returns an authenticated token with ROLE_ADMIN authority if valid
		// 4. Throws BadCredentialsException if invalid
		// This demonstrates how you can plug in ANY authentication system - external APIs,
		// custom databases, biometric systems, etc. - by implementing AuthenticationProvider.
	    String username = authentication.getName();
	    String password = authentication.getCredentials().toString();

	    if (!checkCustomAuthenticationSystem(username, password)) {
	    	throw new BadCredentialsException("Bad credentials provided");
	    }
	      
	    return new UsernamePasswordAuthenticationToken(
	              username, password, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	// Use custom authentication system for the verification of the
	// passed username and password. (Here we are just faking it.)
	private boolean checkCustomAuthenticationSystem(String username, String password) {
		return username.equals("spring") && password.equals("spring");
	}
}
