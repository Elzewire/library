package it.raccoon.library.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.raccoon.library.domain.LibUser;
import it.raccoon.library.domain.Role;
import it.raccoon.library.domain.Roles;
import it.raccoon.library.repositories.RoleRepository;
import it.raccoon.library.repositories.UserRepository;
import it.raccoon.library.security.jwt.JwtProvider;
import it.raccoon.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public LibUser saveUser(LibUser libUser) {
        Role rUser = roleRepository.findByName(Roles.USER.getName());
        Set<Role> roles = new HashSet<>();
        roles.add(rUser);
        libUser.setRoles(roles);
        libUser.setPassword(passwordEncoder.encode(libUser.getPassword()));
        return userRepository.save(libUser);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        LibUser libUser = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        libUser.getRoles().add(role);
        userRepository.save(libUser);
    }

    @Override
    public LibUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<LibUser> getUsers(int page, int size) {
        return userRepository.findAll();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            try {
                String username = jwtProvider.getUsernameFromToken(token);
                LibUser libUser = userRepository.findByUsername(username);
                String accessToken = jwtProvider.generateAccessToken(username, libUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> errors = new HashMap<>();
                errors.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errors);
            }
        } else {
            throw new RuntimeException("Refresh token is missing!");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LibUser libUser = userRepository.findByUsername(username);
        if (libUser == null) {
            throw new UsernameNotFoundException("User " + username + " does not exist.");
        }
        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        libUser.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new User(libUser.getUsername(), libUser.getPassword(), authorities);
    }
}
