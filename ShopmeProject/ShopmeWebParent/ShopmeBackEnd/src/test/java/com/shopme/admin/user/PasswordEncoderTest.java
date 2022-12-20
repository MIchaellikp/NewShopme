package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	@Test
	public void testEncodePassword(){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "nnnnd2022";
		String password = passwordEncoder.encode(rawPassword);
		System.out.println(password);
		
		boolean matches = passwordEncoder.matches(rawPassword, password);
		
		assertThat(matches).isTrue();
	}
}
