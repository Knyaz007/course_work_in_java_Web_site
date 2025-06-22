package com.example.laba.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Класс-обёртка для сущности User, реализующий интерфейс UserDetails,
 * необходимый для интеграции с Spring Security.
 * 
 * Используется системой безопасности для аутентификации и авторизации пользователя.
 * Содержит информацию о пользователе: email (вместо username), пароль и роли.
 */
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String email; // используется как username
    private String password;
    private Collection<? extends GrantedAuthority> authorities; // роли/права пользователя

    /**
     * Конструктор класса
     * 
     * @param id           ID пользователя
     * @param email        Email (используется как имя пользователя)
     * @param password     Зашифрованный пароль
     * @param authorities  Коллекция прав пользователя (роли)
     */
    public UserDetailsImpl(Long id, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Статический метод для создания объекта UserDetailsImpl из сущности User.
     * Преобразует роль пользователя в формат, ожидаемый Spring Security.
     *
     * @param user пользователь из базы данных
     * @return объект UserDetailsImpl
     */
    public static UserDetailsImpl build(User user) {
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRoles())
        );

        return new UserDetailsImpl(
                user.getUserId(),
                user.getEmail(), // используем email как имя пользователя
                user.getPassword(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    /**
     * Возвращает список прав/ролей пользователя.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает email пользователя как "username".
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Указывает, истекла ли учётная запись. true = не истекла.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, заблокирована ли учётная запись. true = не заблокирована.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, истекли ли учётные данные (пароль). true = не истекли.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, активен ли пользователь. true = активен.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
