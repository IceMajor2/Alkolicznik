package com.demo.alkolicznik.security.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class HttpClientConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConfig.class);

	// Determines the timeout in milliseconds until a connection is established.
	private static final int CONNECT_TIMEOUT = 30000;

	// The timeout when requesting a connection from the connection manager.
	private static final int REQUEST_TIMEOUT = 30000;

	// The timeout for waiting for data
	private static final int SOCKET_TIMEOUT = 60000;

	private static final int MAX_TOTAL_CONNECTIONS = 50;

	private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;

	private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		SSLContextBuilder builder = new SSLContextBuilder();
		try {
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		}
		catch (NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.error("Pooling Connection Manager Initialization failure: " + e.getMessage(), e);
		}
		SSLConnectionSocketFactory socketFactory = null;
		try {
			socketFactory = new SSLConnectionSocketFactory(builder.build());
		}
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			LOGGER.error("Pooling Connection Manager Initialization failure: " + e.getMessage());
		}

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("https", socketFactory)
				.register("http", new PlainConnectionSocketFactory())
				.build();

		PoolingHttpClientConnectionManager poolingConnectionManager =
				new PoolingHttpClientConnectionManager(socketFactoryRegistry());
		poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		poolingConnectionManager.setDefaultConnectionConfig(connectionConfig());

		return poolingConnectionManager;
	}

	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return new ConnectionKeepAliveStrategy() {
			@Override
			public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
				BasicHeaderElementIterator it = new BasicHeaderElementIterator
						(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement headerElement = it.next();
					String param = headerElement.getName();
					String value = headerElement.getValue();

					if (value != null && param.equalsIgnoreCase("timeout")) {
						return TimeValue.of
								(Long.parseLong(value) * 1000, TimeUnit.MILLISECONDS);
					}
				}
				return TimeValue.of(DEFAULT_KEEP_ALIVE_TIME_MILLIS,
						TimeUnit.MILLISECONDS);
			}
		};
	}

	@Bean
	public RequestConfig requestConfig() {
		return RequestConfig.custom()
				.setConnectionRequestTimeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.build();
	}

	@Bean
	public ConnectionConfig connectionConfig() {
		return ConnectionConfig.custom()
				.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
				.setSocketTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
				.build();
	}

	@Bean
	public CloseableHttpClient httpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		return HttpClients.custom()
				.setDefaultRequestConfig(requestConfig())
				.setConnectionManager(poolingHttpClientConnectionManager())
				.setConnectionManagerShared(true)
				.setKeepAliveStrategy(connectionKeepAliveStrategy())
				.build();
	}

	@Bean
	public Registry<ConnectionSocketFactory> socketFactoryRegistry() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		final TrustStrategy trustStrategy = (cert, authType) -> true;
		final SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(null, trustStrategy)
				.build();
		final SSLConnectionSocketFactory socketFactory =
				new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create()
				.register("https", socketFactory)
				.register("http", new PlainConnectionSocketFactory())
				.build();
		return socketFactoryRegistry;
	}

	@Bean
	public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
		return new Runnable() {
			@Override
			@Scheduled(fixedDelay = 10000)
			public void run() {
				try {
					if (connectionManager != null) {
						LOGGER.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
						connectionManager.closeExpired();
						connectionManager.closeIdle(TimeValue.of(
								CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS
						));
					}
					else {
						LOGGER.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
					}
				}
				catch (Exception e) {
					LOGGER.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
				}
			}
		};
	}
}
