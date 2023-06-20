package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class TestUtils {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        TestUtils.jdbcTemplate = jdbcTemplate;
    }

    public static List<Store> getStores() {
        String sql = "SELECT * FROM store";
        List<Store> initializedStores = jdbcTemplate.query(sql, mapToStore());
        return initializedStores;
    }

    public static List<Beer> getBeers() {
        String sql = "SELECT * FROM beer";
        List<Beer> initializedBeers = jdbcTemplate.query(sql, mapToBeer());
        return initializedBeers;
    }

    public static RowMapper<Store> mapToStore() {
        return new RowMapper<Store>() {
            @Override
            public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
                Store store = new Store();
                store.setId(rs.getLong("id"));
                store.setName(rs.getString("name"));
                return store;
            }
        };
    }

    public static RowMapper<Beer> mapToBeer() {
        return new RowMapper<Beer>() {
            @Override
            public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Beer beer = new Beer();
                beer.setId(rs.getLong("id"));
                beer.setName(rs.getString("name"));
                return beer;
            }
        };
    }

    /**
     * Convert {@code ResponseEntity<T>} body to {@code JSONObject}.
     *
     * @param response with non-null body
     * @return body of {@code ResponseEntity<T>} as {@code JSONObject}
     */
    public static <T> JSONObject getJsonObject(ResponseEntity<T> response) {
        String jsonInString = new Gson().toJson(response.getBody());
        try {
            JSONObject jsonObject = new JSONObject(jsonInString);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert JSON in {@code String} to {@code JSONObject}.
     * @param json JSON string
     * @return {@code JSONObject}
     */
    public static JSONObject getJsonObject(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
