package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class TestUtils {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(ApplicationContext context) {
        jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
    }

    public static Beer getBeer(Long beerId, List<Beer> beers) {
        for (Beer beer : beers) {
            if (beer.getId() == beerId) {
                return beer;
            }
        }
        return null;
    }

    public static Store getStore(Long storeId, List<Store> stores) {
        for (Store store : stores) {
            if (store.getId() == storeId) {
                return store;
            }
        }
        return null;
    }

    public static BeerPrice getBeerPrice(Long storeId, Long beerId, List<Store> stores, List<Beer> beers) {
        Store store = getStore(storeId, stores);
        for (BeerPrice beerPrice : store.getPrices()) {
            if (beerPrice.getBeer().getId().equals(beerId)) {
                return beerPrice;
            }
        }
        return null;
    }

    public static Image getImage(Long beerId, List<Beer> beers) {
        return getBeer(beerId, beers).getImage();
    }

    public static User fetchUser(Long id) {
        var handler = new UserRowCallbackHandler();

        String sql = "SELECT * FROM users u WHERE u.id = " + id;
        jdbcTemplate.query(sql, handler);
        User user = handler.getResults().values().stream().findFirst().get();
        return user;
    }

    public static class UserRowCallbackHandler implements RowCallbackHandler {

        private Map<Long, User> results = new HashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRoles(new HashSet<>());
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));

            Array rolesSqlArray = rs.getArray("roles");
            Object[] roles = (Object[]) rolesSqlArray.getArray();

            for(Object role : roles) {
                user.getRoles().add(Roles.valueOf(role.toString()));
            }

            results.put(user.getId(), user);
        }

        public Map<Long, User> getResults() {
            return results;
        }
    }
}
