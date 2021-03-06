package com.agatapietrzycka.ticketreservation.entity;

import com.agatapietrzycka.ticketreservation.entity.enums.RoleType;
import com.agatapietrzycka.ticketreservation.util.RoleEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @SequenceGenerator(
            name = "role_seq",
            sequenceName = "role_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    private Long roleId;
    @Convert(converter = RoleEnumConverter.class)
    private RoleType role;

    public Set<? extends GrantedAuthority> getGrantedAuthorities() {
        return role.getRolesAndPermissions().stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}