package com.demo.alkolicznik.datascripts;

import com.demo.alkolicznik.datascripts.workers.ImageKitReloader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
public class DemoDataScript implements CommandLineRunner {

    private final ImageKitReloader imageKitReloader;

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        log.info("Reloading application's data to demonstration data... (using embedded database)");
        final String schemaScript = "data_sql/schema-demo-hsql.sql";
        final String dataScript = "data_sql/data-demo-hsql.sql";

        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
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
        imageKitReloader.reload();
        log.info("Successfully reloaded ImageKit directory");
    }
}
