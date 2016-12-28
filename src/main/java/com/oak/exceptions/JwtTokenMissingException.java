package com.oak.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenMissingException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 576163702973884600L;

	public JwtTokenMissingException(String msg){
		super(msg);
	}
	
}
