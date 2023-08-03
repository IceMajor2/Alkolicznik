package com.demo.alkolicznik.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.BeerImage;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TestUtils {

	private static ResourceLoader resourceLoader;

	private static JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(ApplicationContext context) {
		jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
	}

	@Autowired
	public void setResourceLoader(ResourceLoader resourceLoader) {
		TestUtils.resourceLoader = resourceLoader;
	}

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

	public static BeerPrice getBeerPrice(Long storeId, Long beerId, List<Store> stores, List<Beer> beers) {
		Store store = getStore(storeId, stores);
		for (BeerPrice beerPrice : store.getPrices()) {
			if (beerPrice.getBeer().getId().equals(beerId)) {
				return beerPrice;
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

	public static BeerImage getImage(Long beerId, List<Beer> beers) {
		return getBeer(beerId, beers).getImage().get();
	}

	public static User fetchUser(int id) {
		var handler = new UserRowCallbackHandler();

		String sql = "SELECT * FROM users u WHERE u.id = " + id;
		jdbcTemplate.query(sql, handler);
		User user = handler.getResults().values().stream().findFirst().get();
		return user;
	}

	public static class UserRowCallbackHandler implements RowCallbackHandler {

		private Map<Long, User> results = new HashMap<>();

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("id"));
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			user.setRoles(new HashSet<>());
			user.setAccountNonLocked(rs.getBoolean("account_non_locked"));

			Array rolesSqlArray = rs.getArray("roles");
			Object[] roles = (Object[]) rolesSqlArray.getArray();

			for (Object role : roles) {
				user.getRoles().add(Roles.valueOf(role.toString()));
			}

			results.put(user.getId(), user);
		}

		public Map<Long, User> getResults() {
			return results;
		}
	}

	public static String buildURI(String uriString, Map<String, ?> parameters) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
		for (var entry : parameters.entrySet()) {
			builder
					.queryParam(entry.getKey(), entry.getValue());
		}
		String urlTemplate = builder.encode().toUriString();
		return urlTemplate;
	}

	public static String getRawPathToClassPathResource(String resource) {
		URI uri = null;
		try {
			uri = resourceLoader.getResource("classpath:" + resource).getURI();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return Paths.get(uri).toAbsolutePath().toString();
	}

	public static String getRawPathToImage(String imageFilename) {
		URI uri = null;
		try {
			uri = resourceLoader.getResource("classpath:data_img/" + imageFilename).getURI();
		}
		catch (IOException e) {
			try {
				uri = resourceLoader.getResource("classpath:data_img").getURI();
				return Paths.get(uri).toAbsolutePath().toString() + '/' + imageFilename;
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		String rawPath = Paths.get(uri).toAbsolutePath().toString();
		return rawPath;
	}
}
