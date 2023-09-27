package com.demo.alkolicznik.datascripts;

import com.demo.alkolicznik.datascripts.workers.ImageKitReloader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Launching this class reloads database both from
 * this web application, and from remote ImageKit server.
 */
@Configuration
@Profile("!demo")
@ConditionalOnProperty(
        prefix = "data",
        value = "reload",
        havingValue = "true",
        matchIfMissing = false
)
@PropertySource("classpath:imageKit.properties")
@RequiredArgsConstructor
@Slf4j
public class ReloadScript implements CommandLineRunner {

    private final ImageKitReloader imageKitReloader;

    @Value("${imageKit.path}")
    private String imageKitPath;

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        log.info("Reloading application's data to demonstration data...");
        final String deleteScript = "data_sql/delete.sql";
        final String schemaScript = "data_sql/schema.sql";
        final String dataScript = "data_sql/data.sql";

        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        log.info("Executing '{}' script...", deleteScript);
        resourceDatabasePopulator.addScript(new ClassPathResource(deleteScript));
        log.info("Executing '{}' script...", schemaScript);
        resourceDatabasePopulator.addScript(new ClassPathResource(schemaScript));
        log.info("Executing '{}' script...", dataScript);
        resourceDatabasePopulator.addScript(new ClassPathResource(dataScript));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Reloading ImageKit directory...");
        imageKitReloader.reload(imageKitPath);
        log.info("Successfully reloaded ImageKit directory");
    }
}
