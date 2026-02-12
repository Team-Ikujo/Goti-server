package com.goti.domain.entity.user;

import com.goti.constants.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.*;

@Getter
@Entity(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = PROTECTED)
public class UserEntity {

	@Id
	private UUID id;

	private String mobile;

	private UserRole role;
}
