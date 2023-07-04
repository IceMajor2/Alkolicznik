package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;

import java.util.List;

public class TestUtils {

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
}
