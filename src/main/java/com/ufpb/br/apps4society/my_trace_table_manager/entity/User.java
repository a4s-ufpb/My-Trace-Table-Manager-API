package com.ufpb.br.apps4society.my_trace_table_manager.entity;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserRequest;
import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.UserResponse;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity(name = "tb_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "creator")
    List<Theme> themes = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "creator")
    List<TraceTable> traceTables = new ArrayList<>();

    public User(UserRequest userRequest) {
        this.name = userRequest.name();
        this.email = userRequest.email();
        this.password = userRequest.password();
        this.role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == Role.USER) return List.of(new SimpleGrantedAuthority("USER"));
        else return List.of(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    public UserResponse entityToResponse(){
        return new UserResponse(id,name,email,role.getRole());
    }

    public boolean userNotHavePermission(User user){
        return !this.equals(user) && this.getRole() == Role.USER;
    }
}
