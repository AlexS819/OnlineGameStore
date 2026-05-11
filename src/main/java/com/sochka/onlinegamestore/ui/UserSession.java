package com.sochka.onlinegamestore.ui;

import com.sochka.onlinegamestore.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Singleton cache maintaining runtime existence of active authenticated subject.
 */
@Component
@Getter
@Setter
public class UserSession {
    
    private UserDTO currentUser;

    public void clear() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }
}
