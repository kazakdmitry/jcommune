package org.jtalks.jcommune.web.util;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * <tt>AuthenticationFailureHandler</tt> which extends <tt>SimpleUrlAuthenticationFailureHandler</tt>
 * by adding to the Session an attribute with value of username which was used in authentication
 * @author Andrei Alikov
 */
public class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private String usernameSessionAttribute = "username";

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session != null || isAllowSessionCreation()) {
            request.getSession().setAttribute(usernameSessionAttribute, exception.getAuthentication().getName());
        }

        super.onAuthenticationFailure(request, response, exception);
    }

    /**
     * Gets name of Session's attribute which is used to store username.
     * Default value is "username"
     * @return name of Session attribute which is used to store username
     */
    public String getUsernameSessionAttribute() {
        return usernameSessionAttribute;
    }

    /**
     * Set the name of Session's attribute which is used to store username
     * @param usernameSessionAttribute
     */
    public void setUsernameSessionAttribute(String usernameSessionAttribute) {
        this.usernameSessionAttribute = usernameSessionAttribute;
    }
}
