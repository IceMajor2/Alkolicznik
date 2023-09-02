package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.utils.mappers.DatabaseTableConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@Profile("main")
@PropertySource("classpath:application.properties")
public class MainProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainProfile.class);

    private static JdbcTemplate jdbcTemplate;

    @Bean
    @Primary
    public String imageKitPath2() {
        return "/test";
    }

    @Bean("jdbcTemplate")
    @DependsOn("dataSource")
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        MainProfile.jdbcTemplate = new JdbcTemplate(dataSource);
        return MainProfile.jdbcTemplate;
    }

    @Bean("stores")
    public List<Store> stores() {
        LOGGER.info("Creating 'stores' bean...");
        String sql = "SELECT * FROM store";
        List<Store> stores = DatabaseTableConverters.convertToStoreList(sql);
        return stores;
    }

    @Bean("beers")
    public List<Beer> beers() {
        LOGGER.info("Creating 'beers' bean...");
        String sql = "SELECT * FROM beer";
        List<Beer> beers = DatabaseTableConverters.convertToBeerList(sql);
        return beers;
    }

    @Bean("beerPrices")
    @DependsOn({"stores", "beers"})
    public List<BeerPrice> beerPrices(List<Store> stores, List<Beer> beers) {
        LOGGER.info("Creating 'beerPrices' bean...");
        String sql = "SELECT * FROM beer_price WHERE beer_id = ? AND store_id = ?";
        List<BeerPrice> beerPrices = DatabaseTableConverters
                .convertToBeerPriceList(sql, beers, stores);
        return beerPrices;
    }

    @Bean("users")
    @ConditionalOnProperty(prefix = "security.config", name = "enabled", havingValue = "true", matchIfMissing = true)
    public List<User> users() {
        LOGGER.info("Creating 'users' bean...");
        String sql = "SELECT * FROM users";
        List<User> users = DatabaseTableConverters.convertToUserList(sql);
        return users;
    }

    @Bean("dataSource")
    @ConditionalOnProperty(prefix = "database.table.image", name = "enabled", havingValue = "true")
    public DataSource dataSource2() {
        LOGGER.info("Loading data source with images included...");
        return
                (new EmbeddedDatabaseBuilder())
                        .addScript("classpath:data_sql/schema.sql")
                        .addScript("classpath:data_sql/beer-data.sql")
                        .addScript("classpath:data_sql/store-data.sql")
                        .addScript("classpath:data_sql/beer-price-data.sql")
                        .addScript("classpath:data_sql/user-data.sql")
                        .addScript("classpath:data_sql/image-data.sql")
                        .build();
    }

    @Bean("dataSource")
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {
        LOGGER.info("Loading data source with images excluded...");
        return
                (new EmbeddedDatabaseBuilder())
                        .addScript("classpath:data_sql/schema.sql")
                        .addScript("classpath:data_sql/beer-data.sql")
                        .addScript("classpath:data_sql/store-data.sql")
                        .addScript("classpath:data_sql/beer-price-data.sql")
                        .addScript("classpath:data_sql/user-data.sql")
                        .build();
    }
}
