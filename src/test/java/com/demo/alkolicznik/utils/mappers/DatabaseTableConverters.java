package com.demo.alkolicznik.utils.mappers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseTableConverters {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        DatabaseTableConverters.jdbcTemplate = jdbcTemplate;
    }

    public static List<Beer> convertToBeerList(String sql) {
        List<Beer> beers = jdbcTemplate.query(sql, RowMappers.BEER);
        return beers;
    }

    public static List<Store> convertToStoreList(String sql) {
        List<Store> stores = jdbcTemplate.query(sql, RowMappers.STORE);
        return stores;
    }

    /**
     * @param sql    needs to have {@code beer_id} and {@code store_id}
     *               (in that order) in WHERE condition with a placeholder
     *               (i.e. {@code ... WHERE beer_id = ? AND store_id = ?})
     * @param beers  the beers bean
     * @param stores the stores bean
     */
    // O(n**2)
    public static List<BeerPrice> convertToBeerPriceList(String sql,
                                                         List<Beer> beers, List<Store> stores) {
        List<BeerPrice> beerPrices = new ArrayList<>();
        for (Store store : stores) {
            for (Beer beer : beers) {
                BeerPrice beerPrice = (BeerPrice) jdbcTemplate.query
                        (sql, ResultSetExtractors.BEER_PRICE, beer.getId(), store.getId());
                if (beerPrice == null) {
                    continue;
                }
                beerPrice.setBeer(beer);
                beerPrice.setStore(store);
                store.getPrices().add(beerPrice);
                beer.getPrices().add(beerPrice);
                beerPrices.add(beerPrice);
            }
        }
        return beerPrices;
    }

    public static List<User> convertToUserList(String sql) {
        List<User> users = jdbcTemplate.query(sql, RowMappers.USER);
        return users;
    }

    /**
     * @param sql   needs to have {@code beer_id} in WHERE condition
     *              with a placeholder (i.e. {@code ... WHERE beer_id = ?})
     * @param beers the beers bean
     */
    public static List<BeerImage> convertToBeerImageList(String sql, List<Beer> beers) {
        List<BeerImage> beerImages = new ArrayList<>();
        for (Beer beer : beers) {
            BeerImage beerImage = (BeerImage) jdbcTemplate
                    .query(sql, ResultSetExtractors.BEER_IMAGE, beer.getId());
            if (beerImage == null) {
                continue;
            }
            beer.setImage(beerImage);
            beerImage.setBeer(beer);
            beerImages.add(beerImage);
        }
        return beerImages;
    }

    /**
     * @param sql    just a selected table containing store images (no conditions)
     * @param stores the stores bean
     */
    // O(n**2)
    public static List<StoreImage> convertToStoreImageList(String sql, List<Store> stores) {
        List<StoreImage> storeImages = jdbcTemplate.query(sql, RowMappers.STORE_IMAGE);
        for (Store store : stores) {
            for (var image : storeImages) {
                if (!store.getName().equals(image.getStoreName())) {
                    continue;
                }
                store.setImage(image);
                image.getStores().add(store);
                break;
            }
        }
        return storeImages;
    }
}

