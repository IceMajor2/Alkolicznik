package com.demo.alkolicznik.gui.beer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.image.ImageResponseDTO;
import com.demo.alkolicznik.exceptions.ApiError;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.demo.alkolicznik.utils.request.CookieUtils;
import com.demo.alkolicznik.utils.request.RequestUtils;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("Baza piw | Alkolicznik")
@PermitAll
public class BeerView extends ViewTemplate<BeerRequestDTO, BeerResponseDTO> {

	private static final String DEFAULT_CITY = "Olsztyn";

	private static final ParameterizedTypeReference<List<BeerResponseDTO>> BEERS_DTO_REF =
			new ParameterizedTypeReference<>() {};

	private CloseableHttpClient httpClient;

	private BeerForm wizard;

	public BeerView(CloseableHttpClient httpClient) {
		super("Piwa");
		this.httpClient = httpClient;

		setSizeFull();
		add(
				getToolbar(new BeerRequestDTO()),
				getSearchText(),
				getContent()
		);
		updateList();
		updateDisplayText();
		closeEditor();
	}

	@Override
	protected FormTemplate<BeerRequestDTO> getForm() {
		wizard = new BeerForm();
		wizard.setWidth("25em");

		wizard.addCreateListener(this::createBeer);
		wizard.addUpdateListener(this::updateBeer);
		wizard.addDeleteListener(this::deleteBeer);
		wizard.addCloseListener(event -> closeEditor());
		return wizard;
	}

	@Override
	protected Grid getGrid() {
		this.grid = new Grid<>();
		grid.setSizeFull();

		if (!loggedUser.isUser()) {
			grid.addColumn(beer -> beer.getId()).setHeader("Id");
		}
		grid.addComponentColumn(beer -> this.getImage(beer)).setHeader("Zdjęcie");
		grid.addColumn(beer -> beer.getBrand()).setHeader("Marka");
		grid.addColumn(beer -> beer.getType()).setHeader("Typ");
		grid.addColumn(beer -> beer.getVolume()).setHeader("Objętość");
		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		if (!loggedUser.isUser()) {
			grid.asSingleSelect().addValueChangeListener(event -> {
				if (wizard == null) {
					wizard = new BeerForm();
				}
				editModel(event.getValue());
			});
		}
		return grid;
	}

	@Override
	protected BeerRequestDTO convertToRequest(BeerResponseDTO beerResponse) {
		BeerRequestDTO beerRequest = new BeerRequestDTO();
		beerRequest.setBrand(beerResponse.getBrand());
		beerRequest.setType(beerResponse.getType());
		beerRequest.setVolume(beerResponse.getVolume());
		return beerRequest;
	}

	private BeerUpdateDTO convertToUpdate(BeerRequestDTO beer) {
		BeerUpdateDTO beerUpdate = new BeerUpdateDTO();
		beerUpdate.setBrand(beer.getBrand());
		beerUpdate.setType(beer.getType());
		beerUpdate.setVolume(beer.getVolume());
		return beerUpdate;
	}

	@Override
	protected void updateList(String city) {
		if (city.isBlank()) {
			updateList();
			return;
		}
		try {
			Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
			var beers = RequestUtils.request(HttpMethod.GET, "/api/beer",
					Map.of("city", city), null, authCookie, BEERS_DTO_REF);
			grid.setItems(beers.getBody());
		}
		catch (HttpClientErrorException e) {
			grid.setItems(Collections.EMPTY_LIST);
		}
		updateDisplayText(city);
	}

	@Override
	protected void updateList() {
		if (loggedUser.hasAccountantRole()) {
			Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
			var beers = RequestUtils.request(HttpMethod.GET, "/api/beer", authCookie,
					BEERS_DTO_REF);
			this.grid.setItems(beers.getBody());
			updateDisplayText("cała Polska");
		}
		else {
			updateList(DEFAULT_CITY);
			updateDisplayText(DEFAULT_CITY);
		}
	}

	private void deleteBeer(BeerForm.DeleteEvent event) {
		var request = event.getBeer();
		var deleteRequest = new BeerDeleteRequestDTO(request.getBrand(),
				request.getType(), request.getVolume());
		try {
			Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
			RequestUtils.request(HttpMethod.DELETE, "/api/beer", null, deleteRequest,
					authCookie, BeerDeleteResponseDTO.class);
		}
		catch (HttpClientErrorException e) {
			showError(RequestUtils.extractErrorMessage(e));
			return;
		}
		updateList();
		closeEditor();
	}

	private void createBeer(BeerForm.CreateEvent event) {
		var requestBody = event.getBeer();
		try {
			Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
			RequestUtils.request(HttpMethod.POST, "/api/beer", requestBody, authCookie,
					BeerResponseDTO.class);
		}
		catch (HttpClientErrorException e) {
			showError(RequestUtils.extractErrorMessage(e));
			return;
		}
		updateList();
		closeEditor();
	}

	private void updateBeer(BeerForm.UpdateEvent event) {
		Optional<BeerResponseDTO> selection = grid.getSelectionModel().getFirstSelectedItem();
		if (selection.isEmpty()) {
			showError("First select the beer to update from the grid");
			return;
		}
		Long beerToUpdateId = selection.get().getId();
		BeerUpdateDTO requestBody = convertToUpdate(event.getBeer());
		try {
			Cookie cookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
			BeerResponseDTO response = RequestUtils.patchRequest("/api/beer/" + beerToUpdateId,
					requestBody, cookie, BeerResponseDTO.class);
		} catch (ApiError e) {
			showError(e.getMessage());
			return;
		}
		updateList();
		closeEditor();
	}

	private Image getImage(BeerResponseDTO beerDTO) {
		try {
			Cookie authCookie = CookieUtils.getAuthCookie(VaadinRequest.getCurrent());
			var response = RequestUtils.request(HttpMethod.GET,
					"/api/beer/" + beerDTO.getId() + "/image", authCookie,
					ImageResponseDTO.class);
			return new Image(response.getBody().getImageUrl(), "Image");
		}
		catch (HttpClientErrorException e) {
			return null;
		}
	}

	private void showError(String message) {
		Notification.show(message, 4000, Notification.Position.BOTTOM_END);
	}
}
