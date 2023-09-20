package com.demo.alkolicznik.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServerInfo {

    @Bean
    public String baseUrl(String schema, String ipAddress, int serverPort) {
        return schema + "://" + ipAddress + ":" + serverPort;
    }

    @Bean
    public String ipAddress() throws UnknownHostException {
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
