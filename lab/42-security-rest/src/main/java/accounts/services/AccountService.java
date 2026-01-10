package accounts.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    // TODO-09: Add method security annotation to a method
    // - Uncomment and complete PreAuthorize annotation below
    //   so that the method is permitted to be invoked only
    //   when both of following two run-time conditions are met:
    //   (Use SpEL to specify these conditions.)
    //
    //   (a) the logged-in user belongs to "ADMIN" role
    //   (b) the value of the "username" argument matches
    //       the value of the logged-in principal's
    //       username, which can be accessed as
    //       principal.username or authentication.name.
    //
    // CHANGE: Added @PreAuthorize annotation with SpEL expression
    // WHY: Method-level security is a fine-grained authorization mechanism that allows us to
    // restrict method invocation based on runtime conditions. This prevents users from
    // accessing authorities of other users even if they somehow bypass endpoint security.
    // The SpEL expression checks two conditions: (1) user has ADMIN role, AND
    // (2) the requested username parameter matches the authenticated user's username.
    @PreAuthorize("hasRole('ADMIN') and #username == authentication.name")
    public List<String> getAuthoritiesForUser(String username) {

        // TODO-08: Retrieve authorities (roles) for the logged-in user
        // (This is probably not a typical business logic you will
        //  have in a service layer. This is mainly to show
        //  how SecurityContext object is maintained in the local
        //  thread, which can be accessed via SecurityContextHolder)
        // CHANGE: Retrieved current user's authorities from SecurityContextHolder
        // WHY: SecurityContextHolder is a static holder of the current SecurityContext.
        // The SecurityContext contains the Authentication object which holds the user's
        // authorities (roles). This demonstrates that Spring Security information is stored
        // in thread-local storage and is accessible throughout the request processing pipeline.
        // In this case, we're getting the authentication from the context and extracting their granted authorities.
        // - Replace null below with proper code - use SecurityContextHolder
        // - Restart the application (or let Spring Boot Devtools to restart the app)
        // - Using Chrome Incognito browser or "curl", access
        //   http://localhost:8080/authorities?username=<username>
        // - Verify that roles of the logged-in user get displayed
        Collection<? extends GrantedAuthority> grantedAuthorities
                = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return grantedAuthorities.stream()
                                 .map(GrantedAuthority::getAuthority)
                                 .collect(Collectors.toList());
    }

}
