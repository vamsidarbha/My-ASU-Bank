package com.bankapp.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(
		        "/", 
		        "/404",
		        "/500",
		        "/home", 
		        "/login/identify", 
		        "/login/verifyIdentity", 
		        "/signup",
		        "/registrationConfirm", 
		        "/resendRegistrationToken",
		        "/badUser",
		        // Resources
		        "/webjars/**",
		        "/css/**",
		        "/img/**",
		        "/js/**",
		        "/app/**"
		).permitAll().anyRequest().authenticated().and()
		    .requiresChannel().and()
		    .formLogin()
		        .loginPage("/")
		        .usernameParameter("m_email")
		        .passwordParameter("m_password")
		        .successHandler(new AuthSuccessHandler())
		        .permitAll()
		        .and()
	        .logout()
		        .logoutUrl("/logout")
		        .logoutSuccessUrl("/")
		        .invalidateHttpSession( true )
	            .deleteCookies("JSESSIONID").and()
	        .sessionManagement()
	            .sessionFixation()
	            .migrateSession()
	            .maximumSessions(1)
	            .expiredUrl("/?error=expired")
	            .and()
	            .invalidSessionUrl("/");
		
		http.addFilterBefore(new CrossScriptingFilter(), BasicAuthenticationFilter.class);
			     
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		final DaoAuthenticationProvider authProvider = new CustomDaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(12);
	}
}