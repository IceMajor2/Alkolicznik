package com.demo.alkolicznik;

import com.demo.alkolicznik.models.*;
import com.demo.alkolicznik.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TestConfig {

    @Autowired
    private ApplicationContext context;

    private static JdbcTemplate jdbcTemplate;
    private static Map<Long, Beer> beers = new HashMap<>();
    private static Map<Long, Store> stores = new HashMap<>();
    private static Map<Long, User> users = new HashMap<>();

    @Bean
    public void setJdbcTemplate() {
        TestConfig.jdbcTemplate = new JdbcTemplate(dataSource());
    }

    @Bean
    public List<Store> stores() {
        String sql = "SELECT * FROM store";
        List<Store> initializedStores = jdbcTemplate.query(sql, this.mapToStore());
        for (Store store : initializedStores) {
            stores.put(store.getId(), store);
        }
        return initializedStores;
    }

    @Bean
    public List<User> users() {
        String sql = "SELECT * FROM users";
        var handler = new TestUtils.UserRowCallbackHandler();
        jdbcTemplate.query(sql, handler);
        List<User> initializedUsers = new ArrayList<>(handler.getResults().values());
        for (User user : initializedUsers) {
            users.put(user.getId(), user);
        }
        return initializedUsers;
    }

    @Bean
    public List<Beer> beers() {
        String sql = "SELECT * FROM beer";
        List<Beer> initializedBeers = jdbcTemplate.query(sql, this.mapToBeer());
        for (Beer beer : initializedBeers) {
            beers.put(beer.getId(), beer);
        }
        return initializedBeers;
    }

    @Bean
    @DependsOn({"stores", "beers"})
    public List<BeerPrice> beerPrice() {
        String sql = "SELECT * FROM beer_price";
        List<BeerPrice> initializedPrices = jdbcTemplate.query(sql, this.mapToBeerPrice());
        updateStoresWithPrices(initializedPrices);
        updateBeersWithPrices(initializedPrices);
        return initializedPrices;
    }

    @Bean
    public List<String> randomPasswordsRight() {
        List<String> randomPasswords = List.of("kl;jdvba;gbirjea",
                "3rt90qw4gmkvsvr", "ojpeaipqe4903-qAP[WC", "IJWQ[O;EJFIVKvjifdibs3", "2jiof43qpv4kcvlsA",
                "dsamkfaiovero33", "FOKJp[ewc[vrewvrv", "j39dasvp4q2adcfrvbEWSF", "32dsajivq4oipvfeWK");
        return randomPasswords;
    }

    private void updateBeersWithPrices(List<BeerPrice> prices) {
        List<Beer> beers = (List<Beer>) context.getBean("beers");
        for (Beer beer : beers) {
            for (BeerPrice price : prices) {
                if (price.getBeer().equals(beer)) {
                    beer.getPrices().add(price);
                }
            }
        }
    }

    private void updateStoresWithPrices(List<BeerPrice> prices) {
        List<Store> stores = (List<Store>) context.getBean("stores");
        for (Store store : stores) {
            for (BeerPrice price : prices) {
                if (price.getStore().equals(store)) {
                    store.addBeer(price.getBeer(), price.getPrice());
                }
            }
        }
    }

    @Bean
    public DataSource dataSource() {
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
                String sql = "SELECT * FROM image i WHERE i.beer_id = " + beer.getId();
                ImageModel image = jdbcTemplate.query(sql, new ResultSetExtractor<ImageModel>() {
                    @Override
                    public ImageModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                        // at beginning ResultSet is pointed *BEFORE* the 1st row
                        // due to the fact that this ResultSet may return at most 1 row (ID is UNIQUE)
                        // we move the pointer to the next row with rs.next() command
                        // if it returns false, then there's no image assigned to the beer
                        // thus returning null below
                        if(!rs.next()) {
                            return null;
                        }
                        ImageModel image = new ImageModel();
                        image.setImageUrl(rs.getString("url"));
                        image.setId(rs.getLong("beer_id"));
                        return image;
                    }
                });
                if(image != null) {
                    beer.setImage(image);
                    image.setBeer(beer);
                }
                return beer;
            }
        };
    }

//    private RowMapper<ImageModel> mapToImage() {
//        return new RowMapper<ImageModel>() {
//            @Override
//            public ImageModel mapRow(ResultSet rs, int rowNum) throws SQLException {
//                ImageModel image = new ImageModel();
//                image.setId(rs.getLong("beer_id"));
//                image.setImageUrl(rs.getString("image_address"));
//                return image;
//            }
//        };
//    }

    private RowMapper<BeerPrice> mapToBeerPrice() {
        return new RowMapper<BeerPrice>() {
            @Override
            public BeerPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
                BeerPrice beerPrice = new BeerPrice();

                Beer beer = beers.get(rs.getLong("beer_id"));
                beerPrice.setBeer(beer);

                Store store = stores.get(rs.getLong("store_id"));
                beerPrice.setStore(store);

                MonetaryAmount price = Monetary.getDefaultAmountFactory()
                        .setCurrency(rs.getString("price_currency"))
                        .setNumber(rs.getBigDecimal("price_amount")).create();
                beerPrice.setPrice(price);
                return beerPrice;
            }
        };
    }
}
