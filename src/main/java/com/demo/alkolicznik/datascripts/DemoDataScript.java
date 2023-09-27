package com.demo.alkolicznik.datascripts;

import com.demo.alkolicznik.datascripts.workers.ImageKitReloader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

@Component
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
public class DemoDataScript implements CommandLineRunner {

    private final ImageKitReloader imageKitReloader;

    @Value("${imageKit.path}")
    private String imageKitPath;

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) throws IOException {
        checkRemotePathCollision();
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
        imageKitReloader.reload(imageKitPath);
        log.info("Successfully reloaded ImageKit directory");
    }

    private void checkRemotePathCollision() throws IOException {
        final String property = "imageKit.path";
        String mainPath = getAbsolutePathToClassPathResource("imageKit.properties");
        String demoPath = getAbsolutePathToClassPathResource("application-demo.properties");
        Properties mainImageKit = getProperties(mainPath);
        Properties demoImageKit = getProperties(demoPath);
        String mainImageKitPath = mainImageKit.getProperty(property);
        String demoImageKitPath = demoImageKit.getProperty(property);

        if (mainImageKitPath == null || mainImageKitPath.isBlank())
            throw new IllegalStateException("Property '%s' was missing from '%s'".formatted(property, mainPath));
        if (demoImageKitPath == null || demoImageKitPath.isBlank())
            throw new IllegalStateException("Property '%s' was missing from '%s'".formatted(property, demoPath));
        if (Objects.equals(mainImageKitPath, demoImageKitPath))
            throw new IllegalStateException("Property '%s' has the same value in production ('%s') and demo ('%s'). "
                    .formatted(property, mainPath, demoPath) + "Please, make sure they differ");
    }

    private Properties getProperties(String path) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(path));
            return properties;
        } catch (IOException e) {
            return null;
        }
    }

    private String getAbsolutePathToClassPathResource(String file) {
        try {
            return Paths.get(new ClassPathResource(file).getURI()).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
