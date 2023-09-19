package com.demo.alkolicznik.utils.mappers;

import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.image.BeerImage;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum ResultSetExtractors implements ResultSetExtractor {
    BEER_PRICE {
        @Override
        public BeerPrice extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }
            BeerPrice beerPrice = new BeerPrice();
            MonetaryAmount price = Monetary.getDefaultAmountFactory()
                    .setCurrency(rs.getString("price_currency"))
                    .setNumber(rs.getBigDecimal("price_amount")).create();
            beerPrice.setPrice(price);
            return beerPrice;
        }
    }, BEER_IMAGE {
        @Override
        public BeerImage extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }
            String url = rs.getString("url");
            BeerImage beerImage = new BeerImage();
            beerImage.setImageUrl(url);
            beerImage.setId(rs.getLong("beer_id"));
            beerImage.setRemoteId(rs.getString("remote_id"));
            return beerImage;
        }
    };
}
