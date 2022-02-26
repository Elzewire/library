package it.raccoon.library.services;

import it.raccoon.library.domain.Role;
import it.raccoon.library.domain.LibUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UserService extends UserDetailsService {
    LibUser saveUser(LibUser libUser);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    LibUser findByUsername(String username);
    List<LibUser> getUsers(int page, int size);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
