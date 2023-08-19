package com.demo.alkolicznik.security.config;

import javax.net.ssl.SSLException;

import com.demo.alkolicznik.repositories.UserRepository;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import reactor.netty.http.client.HttpClient;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

	private final static String SECURITY_USER_CONSTRAINT = "CONFIDENTIAL";

	private final static String REDIRECT_PATTERN = "/*";

	private final static String CONNECTOR_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

	private final static String CONNECTOR_SCHEME = "http";

	private final UserRepository userRepository;

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException
						("User '%s' was not found".formatted(username)));
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager
			(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(13);
	}

	@Bean
	public WebClient webClient(WebClient.Builder webClientBuilder) throws SSLException {
		SslContext sslContext = SslContextBuilder
				.forClient()
				// TODO: only this app's certificate should be in trust store
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
		ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
				HttpClient.create().secure(t -> t.sslContext(sslContext)));
		return webClientBuilder
				.clientConnector(httpConnector)
				// TODO: base url should be passed as argument (property)
				.baseUrl("https://127.0.0.1:8433")
				.build();
	}

	@Bean
	public TomcatServletWebServerFactory servletWebServerFactory() {
		TomcatServletWebServerFactory tomcat =
				new TomcatServletWebServerFactory() {
					@Override
					protected void postProcessContext(Context context) {
						SecurityConstraint constraint = new SecurityConstraint();
						constraint.setUserConstraint(SECURITY_USER_CONSTRAINT);
						SecurityCollection collection = new SecurityCollection();
						collection.addPattern(REDIRECT_PATTERN);
						constraint.addCollection(collection);
						context.addConstraint(constraint);
					}
				};
		tomcat.addAdditionalTomcatConnectors(createHttpConnector());
		return tomcat;
	}

	private Connector createHttpConnector() {
		Connector connector =
				new Connector(CONNECTOR_PROTOCOL);
		connector.setScheme(CONNECTOR_SCHEME);
		connector.setSecure(false);
		connector.setPort(8080);
		connector.setRedirectPort(8433);
		return connector;
	}
}
