package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "SELECT * FROM store";
        List<Store> initializedStores = jdbcTemplate.query(sql, this.mapToStore());
        return initializedStores;
    }

    @Bean
    public List<Beer> beers() {
        String sql = "SELECT * FROM beer";
        List<Beer> initializedBeers = jdbcTemplate.query(sql, this.mapToBeer());
        return initializedBeers;
    }

    @Bean
    public DataSource dataSource() {
        return
                (new EmbeddedDatabaseBuilder())
                        .addScript("classpath:data_sql/schema.sql")
                        .addScript("classpath:data_sql/beer-data.sql")
                        .addScript("classpath:data_sql/store-data.sql")
                        .addScript("classpath:data_sql/store_equipment-data.sql")
                        .build();
    }

    private RowMapper<Store> mapToStore() {
        return new RowMapper<Store>() {
            @Override
            public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
                Store store = new Store();
                store.setId(rs.getLong("id"));
                store.setName(rs.getString("name"));
                store.setCity(rs.getString("city"));
                store.setStreet(rs.getString("street"));
                return store;
            }
        };
    }

    private RowMapper<Beer> mapToBeer() {
        return new RowMapper<Beer>() {
            @Override
            public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Beer beer = new Beer();
                beer.setId(rs.getLong("id"));
                beer.setBrand(rs.getString("brand"));
                beer.setType(rs.getString("type"));
                beer.setVolume(rs.getDouble("volume"));
                return beer;
            }
        };
    }
}
