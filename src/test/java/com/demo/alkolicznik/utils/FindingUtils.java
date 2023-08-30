package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;

import java.util.ArrayList;
import java.util.List;

public class FindingUtils {

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

    public static BeerPrice getBeerPrice(Long storeId, Long beerId, List<BeerPrice> prices) {
        for (var price : prices) {
            if (price.getBeer().getId().equals(beerId) && price.getStore().getId().equals(storeId)) {
                return price;
            }
        }
        return null;
    }

    public static List<Beer> getBeersInCity(String city, List<Beer> beers) {
        List<Beer> beersInCity = new ArrayList<>();

        one:
        for (Beer beer : beers) {
            for (BeerPrice beerPrice : beer.getPrices()) {
                if (beerPrice.getStore().getCity().equals(city)) {
                    beersInCity.add(beer);
                    continue one;
                }
            }
        }
        return beersInCity;
    }

    public static BeerImage getBeerImage(Long beerId, List<BeerImage> beerImages) {
        for (var image : beerImages) {
            if (image.getId().equals(beerId)) {
                return image;
            }
        }
        return null;
    }

    public static StoreImage getStoreImage(Long storeId, List<Store> stores) {
        for (var store : stores) {
            if (store.getId().equals(storeId)) {
                return store.getImage().orElse(null);
            }
        }
        return null;
    }

    public static StoreImage getStoreImage(String storeName, List<StoreImage> storeImages) {
        for (var image : storeImages) {
            if (image.getStoreName().equals(storeName)) {
                return image;
            }
        }
        return null;
    }

    public static User getUser(String username, List<User> users) {
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public static String getUserRoleLowerCase(String username, List<User> users) {
        return getUser(username, users).getRole().name().toLowerCase();
    }
}
