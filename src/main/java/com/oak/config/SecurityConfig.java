package com.oak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.oak.auth.JwtAuthenticationFilter;
import com.oak.auth.JwtAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtAuthenticationProvider jwtAuthenticationProvider;
	
	@Autowired
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		/*
		 * auth.inMemoryAuthentication().withUser("mkyong").password("123456").
		 * roles("USER");
		 * auth.inMemoryAuthentication().withUser("admin").password("admin").
		 * roles("ADMIN");
		 * auth.inMemoryAuthentication().withUser("dba").password("123456").
		 * roles("DBA");
		 */
		auth.authenticationProvider(jwtAuthenticationProvider);

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.GET).antMatchers(HttpMethod.OPTIONS).antMatchers(HttpMethod.POST, "/users");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Added for Custom Authentication Filter
		http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().antMatchers(HttpMethod.GET).permitAll().antMatchers(HttpMethod.OPTIONS).permitAll()
				.antMatchers(HttpMethod.POST, "/users").permitAll().anyRequest().authenticated().and().httpBasic().and()
				.csrf().disable().headers().frameOptions().disable();

		/*
		 * http.authorizeRequests().antMatchers(HttpMethod.GET).permitAll().
		 * antMatchers(HttpMethod.OPTIONS).permitAll()
		 * .antMatchers(HttpMethod.POST,
		 * "/users").permitAll().anyRequest().authenticated().and().httpBasic().
		 * and() .csrf().disable().headers().frameOptions().disable();
		 */

	}
}