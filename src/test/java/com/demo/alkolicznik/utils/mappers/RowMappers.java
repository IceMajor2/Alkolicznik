package com.demo.alkolicznik.utils.mappers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.StoreImage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public enum RowMappers implements RowMapper {

    BEER {
        @Override
        public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Beer beer = new Beer();
            beer.setId(rs.getLong("id"));
            beer.setBrand(rs.getString("brand"));
            beer.setType(rs.getString("type"));
            beer.setVolume(rs.getDouble("volume"));
            return beer;
        }
    }, STORE {
        @Override
        public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
            Store store = new Store();
            store.setId(rs.getLong("id"));
            store.setName(rs.getString("name"));
            store.setCity(rs.getString("city"));
            store.setStreet(rs.getString("street"));
            return store;
        }
    }, USER {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(Roles.valueOf(rs.getString("role")));
            return user;
        }
    }, STORE_IMAGE {
        @Override
        public StoreImage mapRow(ResultSet rs, int rowNum) throws SQLException {
            String url = rs.getString("url");
            StoreImage storeImage = new StoreImage();
            storeImage.setImageUrl(url);
            storeImage.setStoreName(rs.getString("store_name"));
            storeImage.setStores(new HashSet<>());
            storeImage.setId(rs.getLong("id"));
            storeImage.setRemoteId(rs.getString("remote_id"));
            return storeImage;
        }
    }
}
