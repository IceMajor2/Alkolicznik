package com.demo.alkolicznik.config.mappers;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import static com.demo.alkolicznik.config.ImageKitConfig.extractFilenameFromUrl;
import static com.demo.alkolicznik.config.ImageKitConfig.getRemoteId;

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
	 *
	 * @param sql needs to have {@code beer_id} and {@code store_id}
	 * (in that order) in WHERE condition with a placeholder
	 * (i.e. {@code ... WHERE beer_id = ? AND store_id = ?})
	 * @param beers the beers bean
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
	 *
	 * @param sql needs to have {@code beer_id} in WHERE condition
	 * with a placeholder (i.e. {@code ... WHERE beer_id = ?})
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
	 *
	 * @param sql just a selected table containing store images (no conditions)
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

enum RowMappers implements RowMapper {

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
			user.setRoles(new HashSet<>());
			user.setAccountNonLocked(rs.getBoolean("account_non_locked"));

			Array rolesSql = rs.getArray("roles");
			Object[] roles = (Object[]) rolesSql.getArray();
			for (Object role : roles) {
				user.getRoles().add(Roles.valueOf(role.toString()));
			}
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
			storeImage.setRemoteId(getRemoteId(extractFilenameFromUrl(url)));
			return storeImage;
		}
	}
}

enum ResultSetExtractors implements ResultSetExtractor {
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
			beerImage.setRemoteId(getRemoteId(extractFilenameFromUrl(url)));
			return beerImage;
		}
	}
}
