package com.demo.alkolicznik;

import com.demo.alkolicznik.repositories.ImageKitRepository;
import io.imagekit.sdk.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(
        prefix = "data",
        value = "delete",
        havingValue = "true",
        matchIfMissing = false
)
@ConditionalOnMissingBean(ReloadScript.class)
@RequiredArgsConstructor
@PropertySource("classpath:imageKit.properties")
@Slf4j
public class DeleteScript implements CommandLineRunner {

    private final ImageKitRepository imageKitRepository;

    @Value("${imageKit.path}")
    private String imageKitPath;

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        log.info("Deleting application's data...");
        final String deleteScript = "delete.sql";
        final String schemaScript = "schema.sql";

        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        log.info("Executing '{}' script...", deleteScript);
        resourceDatabasePopulator.addScript(new ClassPathResource(deleteScript));
        log.info("Executing '{}' script...", schemaScript);
        resourceDatabasePopulator.addScript(new ClassPathResource(schemaScript));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

    @Override
    public void run(String... args) {
        log.info("Deleting ImageKit directory: '%s'...".formatted(imageKitPath));
        deleteFolder("");
        log.info("Successfully deleted ImageKit directory");
    }

    private void deleteFolder(String path) {
        try {
            imageKitRepository.deleteFolder(path);
        } catch (NotFoundException e) {}
    }
}
