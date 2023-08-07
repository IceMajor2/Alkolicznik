package com.demo.alkolicznik.config;

import java.util.List;

import javax.sql.DataSource;

import com.demo.alkolicznik.config.mappers.DatabaseTableConverters;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
@Profile("main")
public class TestConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);

	@Autowired
	private ApplicationContext context;

	private static JdbcTemplate jdbcTemplate;

	@Bean
	public void setJdbcTemplate() {
		TestConfig.jdbcTemplate = new JdbcTemplate(dataSource());
	}

	@Bean
	public List<Store> stores() {
		LOGGER.info("Creating 'stores' bean...");
		String sql = "SELECT * FROM store";
		List<Store> stores = DatabaseTableConverters.convertToStoreList(sql);
		return stores;
	}

	@Bean
	public List<Beer> beers() {
		LOGGER.info("Creating 'beers' bean...");
		String sql = "SELECT * FROM beer";
		List<Beer> beers = DatabaseTableConverters.convertToBeerList(sql);
		return beers;
	}

	@Bean
	@DependsOn("beers")
	public List<BeerImage> beerImages() {
		LOGGER.info("Creating 'beerImages' bean...");
		List<Beer> beers = (List<Beer>) context.getBean("beers");
		String sql = "SELECT * FROM beer_image WHERE beer_id = ?";
		List<BeerImage> beerImages = DatabaseTableConverters
				.convertToBeerImageList(sql, beers);
		updateBeerImageRemoteId(beerImages);
		return beerImages;
	}

	private void updateBeerImageRemoteId(List<BeerImage> beerImages) {
		LOGGER.info("Updating 'beer_image' table with remote IDs...");
		for (var image : beerImages) {
			String sql = "UPDATE beer_image SET remote_id = ? WHERE beer_id = ?";
			jdbcTemplate.update(sql, image.getRemoteId(), image.getId());
		}
	}

	private void updateStoreImageRemoteId(List<StoreImage> storeImages) {
		LOGGER.info("Updating 'store_image' table with remote IDs...");
		for(var image : storeImages) {
			String sql = "UPDATE store_image SET remote_id = ? WHERE store_name = ?";
			jdbcTemplate.update(sql, image.getRemoteId(), image.getStoreName());
		}
	}

	@Bean
	@DependsOn("stores")
	public List<StoreImage> storeImages() {
		LOGGER.info("Creating 'storeImages' bean...");
		List<Store> stores = (List<Store>) context.getBean("stores");
		String sql = "SELECT * FROM store_image";
		List<StoreImage> storeImages = DatabaseTableConverters
				.convertToStoreImageList(sql, stores);
		updateStoreImageRemoteId(storeImages);
		return storeImages;
	}

	@Bean
	@DependsOn({ "stores", "beers" })
	public List<BeerPrice> beerPrices() {
		LOGGER.info("Creating 'beerPrices' bean...");
		List<Beer> beers = (List<Beer>) context.getBean("beers");
		List<Store> stores = (List<Store>) context.getBean("stores");
		String sql = "SELECT * FROM beer_price WHERE beer_id = ? AND store_id = ?";
		List<BeerPrice> beerPrices = DatabaseTableConverters
				.convertToBeerPriceList(sql, beers, stores);
		return beerPrices;
	}

	@Bean
	public List<User> users() {
		LOGGER.info("Creating 'users' bean...");
		String sql = "SELECT * FROM users";
		List<User> users = DatabaseTableConverters.convertToUserList(sql);
		return users;
	}

	@Bean
	@Primary
	public String imageKitPath2() {
		return "/test/beer";
	}

//	private void updateBeersWithPrices(List<BeerPrice> prices) {
//		List<Beer> beers = (List<Beer>) context.getBean("beers");
//		for (Beer beer : beers) {
//			for (BeerPrice price : prices) {
//				if (price.getBeer().equals(beer)) {
//					beer.getPrices().add(price);
//				}
//			}
//		}
//	}
//
//	private void updateStoresWithPrices(List<BeerPrice> prices) {
//		List<Store> stores = (List<Store>) context.getBean("stores");
//		for (Store store : stores) {
//			for (BeerPrice price : prices) {
//				if (price.getStore().equals(store)) {
//					store.saveBeer(price.getBeer(), price.getPrice());
//				}
//			}
//		}
//	}

	@Bean("dataSource")
	@ConditionalOnProperty(prefix = "enable.image", name = "database", havingValue = "true")
	public DataSource dataSource2() {
		LOGGER.info("Loading data source with images included...");
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

	@Bean("dataSource")
	public DataSource dataSource() {
		LOGGER.info("Loading data source with images excluded...");
		return
				(new EmbeddedDatabaseBuilder())
						.addScript("classpath:data_sql/schema.sql")
						.addScript("classpath:data_sql/beer-data.sql")
						.addScript("classpath:data_sql/store-data.sql")
						.addScript("classpath:data_sql/beer-price-data.sql")
						.addScript("classpath:data_sql/user-data.sql")
						.build();
	}

	@Bean("correctPasswords")
	public String[] correctPasswords() {
		LOGGER.info("Creating 'correctPasswords' bean...");
		String[] randomPasswords = new String[] { "kl;jdvba;gbirjea",
				"3rt90qw4gmkvsvr", "ojpeaipqe4903-qAP[WC", "IJWQ[O;EJFIVKvjifdibs3", "2jiof43qpv4kcvlsA",
				"dsamkfaiovero33", "FOKJp[ewc[vrewvrv", "j39dasvp4q2adcfrvbEWSF", "32dsajivq4oipvfeWK" };
		return randomPasswords;
	}

//	private RowMapper<Store> mapToStore() {
//		return new RowMapper<Store>() {
//			@Override
//			public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
//				Store store = new Store();
//				store.setId(rs.getLong("id"));
//				store.setName(rs.getString("name"));
//				store.setCity(rs.getString("city"));
//				store.setStreet(rs.getString("street"));
//
//				String sql = "SELECT * FROM store_image WHERE store_name = ?";
//				StoreImage image = jdbcTemplate.query(sql, new ResultSetExtractor<StoreImage>() {
//					@Override
//					public StoreImage extractData(ResultSet rs) throws SQLException, DataAccessException {
//						// at beginning ResultSet is pointed *BEFORE* the 1st row
//						if (!rs.next()) {
//							return null;
//						}
//						StoreImage image = new StoreImage();
//						Long imgId = rs.getLong("id");
//						String url = rs.getString("url");
//						String storeName = rs.getString("store_name");
//						String remoteId = getRemoteId(extractFilenameFromUrl(url));
//
//						image.setId(imgId);
//						image.setImageUrl(url);
//						image.setStoreName(storeName);
//						// update image table with ImageKit's remote id of this image
//						String updateQuery = "UPDATE store_image SET remote_id = ? WHERE store_name = ?";
//						jdbcTemplate.update(updateQuery, remoteId, storeName);
//						image.setRemoteId(remoteId);
//						return image;
//					}
//				}, store.getName());
//				if (image != null) {
//					store.setImage(image);
//					image.getStores().add(store);
//				}
//				return store;
//			}
//		};
//	}

//	private StoreImage mapToStoreImage(ResultSet rs) {
//
//	}

//	private RowMapper<Beer> mapToBeer() {
//		return new RowMapper<Beer>() {
//			@Override
//			public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
//				Beer beer = new Beer();
//				beer.setId(rs.getLong("id"));
//				beer.setBrand(rs.getString("brand"));
//				beer.setType(rs.getString("type"));
//				beer.setVolume(rs.getDouble("volume"));
//				String sql = "SELECT * FROM beer_image i WHERE i.beer_id = " + beer.getId();
//				BeerImage image = jdbcTemplate.query(sql, new ResultSetExtractor<BeerImage>() {
//					@Override
//					public BeerImage extractData(ResultSet rs) throws SQLException, DataAccessException {
//						// at beginning ResultSet is pointed *BEFORE* the 1st row
//						// due to the fact that this ResultSet may return at most 1 row (ID is UNIQUE)
//						// we move the pointer to the next row with rs.next() command
//						// if it returns false, then there's no image assigned to the entity
//						// thus returning null below
//						if (!rs.next()) {
//							return null;
//						}
//						BeerImage image = new BeerImage();
//						String url = rs.getString("url");
//						Long beerId = rs.getLong("beer_id");
//						String remoteId = getRemoteId(extractFilenameFromUrl(url));
//
//						image.setImageUrl(url);
//						image.setId(beerId);
//						// update image table with ImageKit's remote id of this image
//						String updateQuery = "UPDATE beer_image SET remote_id = ? WHERE beer_id = ?";
//						jdbcTemplate.update(updateQuery, remoteId, beerId);
//						image.setRemoteId(remoteId);
//						return image;
//					}
//				});
//				if (image != null) {
//					beer.setImage(image);
//					image.setBeer(beer);
//				}
//				return beer;
//			}
//		};
//	}

//	private RowMapper<BeerPrice> mapToBeerPrice() {
//		return new RowMapper<BeerPrice>() {
//			@Override
//			public BeerPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
//				BeerPrice beerPrice = new BeerPrice();
//
//				Beer beer = beers.get(rs.getLong("beer_id"));
//				beerPrice.setBeer(beer);
//
//				Store store = stores.get(rs.getLong("store_id"));
//				beerPrice.setStore(store);
//
//				MonetaryAmount price = Monetary.getDefaultAmountFactory()
//						.setCurrency(rs.getString("price_currency"))
//						.setNumber(rs.getBigDecimal("price_amount")).create();
//				beerPrice.setPrice(price);
//				return beerPrice;
//			}
//		};
//	}
}
