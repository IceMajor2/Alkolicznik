package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.utils.mappers.DatabaseTableConverters;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.results.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("main")
public class MainProfile {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainProfile.class);

	@Autowired
	private ApplicationContext context;

	private static JdbcTemplate jdbcTemplate;

	@Bean
	public void setJdbcTemplate() {
		MainProfile.jdbcTemplate = new JdbcTemplate(dataSource());
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
		updateUrlsWithUpdatedAt(beerImages, "beer_image", "beer_id");
		return beerImages;
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
		updateUrlsWithUpdatedAt(storeImages, "store_image", "id");
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
		return "/test";
	}

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
	public List<String> correctPasswords() {
//		LOGGER.info("Creating 'correctPasswords' bean...");
		return Arrays.asList("kl;jdvba;gbirjea", "3rt90qw4gmkvsvr",
				"ojpeaipqe4903-qAP[WC", "IJWQ[O;EJFIVKvjifdibs3", "2jiof43qpv4kcvlsA",
				"dsamkfaiovero33", "FOKJp[ewc[vrewvrv", "j39dasvp4q2adcfrvbEWSF",
				"32dsajivq4oipvfeWK");
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
		for (var image : storeImages) {
			String sql = "UPDATE store_image SET remote_id = ? WHERE store_name = ?";
			jdbcTemplate.update(sql, image.getRemoteId(), image.getStoreName());
		}
	}

	private void updateUrlsWithUpdatedAt(List<? extends ImageModel> images, String tableName, String idColumn) {
		LOGGER.info("Updating '%s' table with 'updatedAt' key...".formatted(tableName));
		for (var image : images) {
			long updatedAt = getUpdatedAt(image.getRemoteId());
			String newURL = image.getImageUrl() + "?updatedAt=" + updatedAt;
			String sql = "UPDATE %s m SET url = ? WHERE %s = ?".formatted(tableName, idColumn);
			jdbcTemplate.update(sql, newURL, image.getId());
			image.setImageUrl(newURL);
		}
	}

	private long getUpdatedAt(String fileId) {
		try {
			Result result = ImageKit.getInstance().getFileDetail(fileId);
			return result.getUpdatedAt().toInstant().getEpochSecond();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
