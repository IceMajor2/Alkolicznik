package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Convert JSON in {@code String} to {@code JSONObject}.
     *
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

    public static JSONArray getValues(String json, String key) {
        DocumentContext documentContext = JsonPath.parse(json);
        JSONArray values = documentContext.read("$..%s".formatted(key));
        return values;
    }

    public static int getLength(String json) {
        DocumentContext documentContext = JsonPath.parse(json);
        return documentContext.read("$.length()");
    }

    public static Beer fetchBeer(Long id) {
        String sql = "SELECT * FROM beer WHERE beer.id = ?";
        Beer beer = jdbcTemplate.queryForObject(sql, mapToBeer(), id);
        return beer;
    }

    public static Store fetchStore(Long id) {
        String sql = "SELECT * FROM store WHERE store.id = ?";
        Store store = jdbcTemplate.queryForObject(sql, mapToStore(), id);
        return store;
    }

    /**
     * Convert list of integers (presumably field {@code id} of an entity
     * into an array of integers.
     *
     * @param ids list of integers
     * @return array of integers
     */
    public static Integer[] convertIdsToArray(List<Integer> ids) {
        return ids.toArray(new Integer[0]);
    }

    /**
     * Convert list of longs to list of integers.
     *
     * @param list list of longs
     * @return list of integers
     */
    public static List<Integer> convertLongListToIntList(List<Long> list) {
        return list.stream()
                .map(num -> (Integer) num.intValue())
                .toList();
    }

    /**
     * Convert list of strings (presumably field {@code name} of an entity
     * into an array of strings.
     *
     * @param names list of strings
     * @return array of strings
     */
    public static String[] convertNamesToArray(List<String> names) {
        return names.toArray(new String[0]);
    }
}
