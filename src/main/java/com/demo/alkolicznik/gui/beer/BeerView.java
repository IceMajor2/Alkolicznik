package com.demo.alkolicznik.gui.beer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.gui.MainLayout;
import com.demo.alkolicznik.gui.templates.FormTemplate;
import com.demo.alkolicznik.gui.templates.ViewTemplate;
import com.demo.alkolicznik.utils.RequestUtils;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("Baza piw | Alkolicznik")
@PermitAll
public class BeerView extends ViewTemplate<BeerRequestDTO, BeerResponseDTO> {

	private static final String DEFAULT_CITY = "Olsztyn";

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	private BeerForm wizard;

	private BeerService beerService;

	private WebClient webClient;

	public BeerView(BeerService beerService, WebClient webClient) {
		super("Piwa");
		this.beerService = beerService;
		this.webClient = webClient;

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
			Cookie authCookie = RequestUtils.getAuthCookie(VaadinRequest.getCurrent());
			var beers = RequestUtils.request(HttpMethod.GET, "/api/beer",
					Map.of("city", city), authCookie,
					new ParameterizedTypeReference<List<BeerResponseDTO>>() {});
			this.grid.setItems(beers);
		} catch (WebClientResponseException e) {
			this.grid.setItems(Collections.EMPTY_LIST);
		}
		updateDisplayText(city);
	}

	@Override
	protected void updateList() {
		if (loggedUser.hasAccountantRole()) {
			Cookie authCookie = RequestUtils.getAuthCookie(VaadinRequest.getCurrent());
			var beers = RequestUtils.request(HttpMethod.GET, "/api/beer", authCookie,
					new ParameterizedTypeReference<List<BeerResponseDTO>>() {});
			this.grid.setItems(beers);
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
			Cookie authCookie = RequestUtils.getAuthCookie(VaadinRequest.getCurrent());
			RequestUtils.request(HttpMethod.DELETE, "/api/beer", null, deleteRequest,
					authCookie, BeerDeleteResponseDTO.class);
		} catch (WebClientResponseException e) {
			showError(RequestUtils.extractErrorMessage(e));
			return;
		}
		updateList();
		closeEditor();
	}


	private void createBeer(BeerForm.CreateEvent event) {
		var request = event.getBeer();
		if (!validate(request)) {
			return;
		}
		try {
			beerService.add(request);
		}
		catch (BeerAlreadyExistsException e) {
			showError(e.getMessage());
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
		BeerUpdateDTO request = convertToUpdate(event.getBeer());
		if (!validate(request)) {
			return;
		}
		try {
			beerService.update(beerToUpdateId, request);
		}
		catch (ObjectsAreEqualException e) {
			showError(e.getMessage());
			return;
		}
		updateList();
		closeEditor();
	}

	private Image getImage(BeerResponseDTO beerDTO) {
		try {
			return beerService.getImageComponent(beerDTO.getId());
		}
		catch (ImageNotFoundException e) {
			return null;
		}
	}

	private boolean validate(Object object) {
		String errorMessage = getErrorMessage(object);
		if (!errorMessage.isEmpty()) {
			showError(errorMessage);
			return false;
		}
		return true;
	}

	private void showError(String message) {
		Notification.show(message, 4000, Notification.Position.BOTTOM_END);
	}

	private String getErrorMessage(Object object) {
		var errors = validator.validate(object);
		ConstraintViolation violation = errors.stream().findFirst().orElse(null);
		if (violation == null) {
			return "";
		}
		return violation.getMessage();
	}

//	private <T> T request(HttpMethod method, String endpoint, Object body, Map<String, String> parameters, Class<T> responseClass) {
//		Cookie jwtCookie = CookieUtils.getAuthCookie(VaadinService.getCurrentRequest());
//		return webClient.method(method)
//				.uri(uriBuilder -> {
//					uriBuilder.path(endpoint);
//					for(var entry : parameters.entrySet()) {
//						uriBuilder.queryParam(entry.getKey(), entry.getValue());
//					}
//					return uriBuilder.build();
//				})
//				.cookie(jwtCookie.getName(), jwtCookie.getValue())
//				.contentType(MediaType.APPLICATION_JSON)
//				.bodyValue(body)
//				.retrieve()
//				.bodyToMono(responseClass)
//				.block();
//	}
}
