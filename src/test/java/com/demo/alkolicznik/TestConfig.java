package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class TestConfig {

    private static JdbcTemplate jdbcTemplate;

    @Bean
    public void setJdbcTemplate() {
        TestConfig.jdbcTemplate = new JdbcTemplate(dataSource());
    }

    @Bean
    public List<Store> stores() {
        System.out.println(jdbcTemplate);
        String sql = "SELECT * FROM store";
        List<Store> initializedStores = jdbcTemplate.query(sql, TestUtils.mapToStore());
        return initializedStores;
    }

    @Bean
    public List<Beer> beers() {
        String sql = "SELECT * FROM beer";
        List<Beer> initializedBeers = jdbcTemplate.query(sql, TestUtils.mapToBeer());
        return initializedBeers;
    }

    @Bean
    public DataSource dataSource(){
        return
                (new EmbeddedDatabaseBuilder())
                        .addScript("classpath:data_sql/schema.sql")
                        .addScript("classpath:data_sql/beer-data.sql")
                        .addScript("classpath:data_sql/store-data.sql")
                        .addScript("classpath:data_sql/store_equipment-data.sql")
                        .build();
    }
}
