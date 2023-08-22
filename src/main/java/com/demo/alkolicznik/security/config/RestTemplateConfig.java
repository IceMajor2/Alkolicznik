package com.demo.alkolicznik.security.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

	private final CloseableHttpClient httpClient;

	@Bean
	public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
		restTemplate.setUriTemplateHandler
				(new DefaultUriBuilderFactory("https://127.0.0.1:8433"));
		return restTemplate;
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
				new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);
		return clientHttpRequestFactory;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("poolScheduler");
		scheduler.setPoolSize(50);
		return scheduler;
	}
}
