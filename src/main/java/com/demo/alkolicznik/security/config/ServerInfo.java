package com.demo.alkolicznik.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServerInfo {

    @Bean("baseUrl")
    @Profile("!alwaysdata")
    public String baseUrl(String schema, String domain, int serverPort) {
        return schema + "://" + domain + ":" + serverPort;
    }

    @Bean("baseUrl")
    @Profile("alwaysdata")
    public String baseUrl2(Environment env, String schema) {
        return "https://" + env.getProperty("alwaysdata.base-url");
    }

    @Bean("domain")
    @Profile("alwaysdata")
    public String domain(Environment env) {
        return env.getProperty("alwaysdata.base-url");
    }

    @Bean("domain")
    @Profile("!alwaysdata")
    public String domain2() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @Bean
    public int serverPort(ServerProperties serverProperties) {
        return serverProperties.getPort();
    }

    @Bean("schema")
    @ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true", matchIfMissing = false)
    public String schema() {
        return "https";
    }

    @Bean("schema")
    @ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "false", matchIfMissing = true)
    public String schema2() {
        return "http";
    }
}
