package accounts.security;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//Optional exercise - Do the remaining steps only if you have extra time
//TODO-13 (Optional): Create custom UserDetailsService
//- Note that it needs to implement loadUserByUsername method
//of the UserDetailsService interface
//- Uncomment the commented code fragment below so that this custom
//UserDetailsService maintains UserDetails of two users:
//- "mary"/"mary" with "USER" role and
//- "joe"/"joe" with "USER" and "ADMIN" roles

// CHANGE: Added @Component annotation to register as Spring-managed bean
// WHY: The @Component annotation makes this class a Spring bean that will be
// auto-detected by component scanning. Since this implements UserDetailsService,
// Spring Security will recognize it as a UserDetailsService bean. The @Primary
// annotation ensures this bean takes precedence over the in-memory UserDetailsManager
// when multiple UserDetailsService beans exist. This enables custom user authentication
// against the mary/joe user store instead of the in-memory admin/user/superadmin store.

//TODO-14a (Optional): Add authentication based upon the custom UserDetailsService
//- Annotate the class with @Component to make it a Spring manager bean

//TODO-18b (Optional): Remove the CustomUserDetailsService definition
// - Comment the @Component annotation added in a previous task

// CHANGE: Commented out @Component annotation for CustomUserDetailsService
// WHY: Since we now have CustomAuthenticationProvider with @Component annotation,
// we need to disable the CustomUserDetailsService to allow the provider to take over.
// The CustomAuthenticationProvider handles "spring"/"spring" authentication and provides
// ROLE_ADMIN authority directly. When both beans are present, Spring Security uses the
// ProviderManager which tries all providers in order. By commenting @Component, only
// CustomAuthenticationProvider remains as the active authentication mechanism, ensuring
// "spring"/"spring" users are authenticated through the custom provider instead of the service.
// This demonstrates switching between different authentication mechanisms.
//@Component
@Primary
public class CustomUserDetailsService implements UserDetailsService {

	private PasswordEncoder passwordEncoder;

	public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User.UserBuilder builder = User.builder();
		// CHANGE: Uncommented custom user loading logic for mary and joe
		// WHY: This implements the loadUserByUsername method which is called whenever a user
		// attempts to authenticate. Instead of using the in-memory store (admin/user/superadmin),
		// this service loads users from the mary/joe custom user store. The switch statement
		// defines two users with different roles:
		// - mary/mary with USER role (basic user)
		// - joe/joe with USER and ADMIN roles (admin user)
		// The password is encoded using the injected PasswordEncoder for security.
		builder.username(username);
		builder.password(passwordEncoder.encode(username));
		switch (username) {
		    case "mary":
		        builder.roles("USER");
		        break;
		    case "joe":
		        builder.roles("USER", "ADMIN");
		        break;
		    default:
		        throw new UsernameNotFoundException("User not found.");
		}

		return builder.build();
	}
}
