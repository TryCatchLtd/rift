package uk.co.wireweb.rift.core.spi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Johansson
 *
 * @since 29 Apr 2011
 */
public class Identity {

    private Set<String> roles = new HashSet<String>();

    private boolean loggedIn;

    public void addRole(final String role) {
        this.roles.add(role);
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public boolean hasRole(final String role) {
        return this.roles.contains(role);
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
