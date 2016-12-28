package com.oak.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.oak.utils.JWTUtil;

public class JwtAuthenticationToken implements Authentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String token;
	private String credentials;
	private boolean authenticated;
	
	private List<GrantedAuthority> authorities;
	
	public JwtAuthenticationToken(String token){
		this.token = token;
		this.credentials = token;
		this.name = JWTUtil.parseToken(token).getEmail();
	}
	
	public JwtAuthenticationToken(String name,String token,List<GrantedAuthority> authorities){
		this.token = token;
		this.name = name;
		this.authorities = authorities;
		this.authenticated = true;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return credentials;
	}

	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

}
