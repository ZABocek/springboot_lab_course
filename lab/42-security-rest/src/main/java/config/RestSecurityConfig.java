package config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// TODO-10: Enable method security
// - Add @EnableMethodSecurity annotation to this class

// CHANGE: Added @EnableMethodSecurity annotation to enable method-level authorization
// WHY: This annotation enables Spring Security's method-level security, which allows us to use
// @PreAuthorize, @PostAuthorize, @Secured, and other method security annotations.
// Without this annotation, method-level security checks (like those in AccountService.getAuthoritiesForUser())
// would be ignored. This enables fine-grained, method-level authorization that acts as a second layer
// of defense after endpoint-level authorization.
@EnableMethodSecurity
@Configuration
public class RestSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// @formatter:off
        http.authorizeHttpRequests((authz) -> authz
                // CHANGE: Configured authorization rules using requestMatchers method
                // RULE 1: DELETE /accounts/** for SUPERADMIN only - prevents unauthorized deletion of accounts
                .requestMatchers("DELETE", "/accounts/**").hasRole("SUPERADMIN")
                // RULE 2: POST/PUT /accounts/** for ADMIN and SUPERADMIN - allows data modification for admins only
                .requestMatchers("POST", "/accounts/**").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("PUT", "/accounts/**").hasAnyRole("ADMIN", "SUPERADMIN")
                // RULE 3: GET /accounts/** for all authenticated roles - read access for everyone
                .requestMatchers("GET", "/accounts/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                // RULE 4: GET /authorities for all authenticated roles - allows users to check their own roles
                .requestMatchers("GET", "/authorities").hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                // Deny any request that doesn't match any authorization rule - security best practice
                .anyRequest().denyAll())
        .httpBasic(withDefaults())
        .csrf(CsrfConfigurer::disable);
        // @formatter:on

        return http.build();
	}

	// TODO-14b (Optional): Remove the InMemoryUserDetailsManager definition
	// - Comment the @Bean annotation below
	
	// CHANGE: Commented out @Bean annotation for InMemoryUserDetailsManager
	// WHY: Since we now have CustomUserDetailsService with @Primary annotation,
	// we need to disable the in-memory UserDetailsManager. When both beans implement
	// UserDetailsService, the @Primary annotation on CustomUserDetailsService tells
	// Spring Security to use our custom service (mary/joe users) instead of the in-memory
	// store (admin/user/superadmin users). By commenting the @Bean, Spring won't register
	// the in-memory service as a bean, preventing bean definition conflicts and ensuring
	// only CustomUserDetailsService is used for authentication.
	//@Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {

		// CHANGE: Added three users with role-based hierarchy using in-memory authentication
		// WHY: In-memory user details storage is useful for testing and development. In production,
		// you would use a database or LDAP. The role hierarchy allows different permission levels.
		// User 1: Basic user with only USER role - read-only access to accounts
    	UserDetails user = User.withUsername("user").password(passwordEncoder.encode("user")).roles("USER").build();
		// User 2: Admin user with USER and ADMIN roles - can modify accounts (POST/PUT)
		UserDetails admin = User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build();
		// User 3: Super admin with all roles - can perform all operations including deletion
		UserDetails superadmin = User.withUsername("superadmin").password(passwordEncoder.encode("superadmin")).roles("USER", "ADMIN", "SUPERADMIN").build();

		// All passwords are encoded using the delegating password encoder (bcrypt by default)
		return new InMemoryUserDetailsManager(user, admin, superadmin);
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
