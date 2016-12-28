package com.oak.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.oak.solr.entities.Content;
import com.oak.solr.entities.User;
import com.oak.solr.services.ContentService;
import com.oak.solr.services.UserService;
import com.oak.solr.vo.ContentResponseVO;

@Component("jwtAuthenticationProvider")
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String id = authentication.getName();
		String token = authentication.getCredentials().toString();

		// System.out.println("Authenticating " + id + ":" + token);

		try {

			User user = UserService.searchById(id).getResults().get(0);

			String jwt = user.getJwt();

			if (user.getStatus().equals(UserService.ACTIVATED) && jwt != null && jwt.equals(token)) {
				List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();

				if (user.getGroups() != null && !user.getGroups().isEmpty()) {
					List<String> groups = user.getGroups();

					for (String group : groups) {

						ContentResponseVO cvo = ContentService.searchByTypeAndName("group", group, 0);

						if (cvo!=null && cvo.getResults() != null) {
							for (Content content : cvo.getResults()) {
								for (String role : content.getTags()) {
									grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + role.trim()));
								}

							}
						}

					}
				}

				Authentication auth = new UsernamePasswordAuthenticationToken(id, token, grantedAuths);

				return auth;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(JwtAuthenticationToken.class);
	}

}
