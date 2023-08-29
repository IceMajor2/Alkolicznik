package com.demo.alkolicznik.security.config;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(type = "org.springframework.boot.test.mock.mockito.MockitoPostProcessor")
@RequiredArgsConstructor
// TODO: rename class and organize it better
public class RedirectConfig {

    private final static String SECURITY_USER_CONSTRAINT = "CONFIDENTIAL";

    private final static String REDIRECT_PATTERN = "/*";

    private final static String CONNECTOR_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    private final static String CONNECTOR_SCHEME = "http";

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
