package com.demo.alkolicznik.gui.beer;

import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.gui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Route(value = "beer", layout = MainLayout.class)
@PageTitle("Baza piw")
@PermitAll
public class BeerView extends VerticalLayout {

    private RestTemplate restTemplate;

    private Grid<BeerResponseDTO> grid;
    private BeerForm form;
    private Component searchToolbar;
    private TextField filterCity;

    public BeerView(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.rootUri("http://localhost:8080").build();
        setSizeFull();
        add(
                getCityToolbar(),
                getContent()
        );
        updateList("Olsztyn");
        closeEditor();
    }

    private void closeEditor() {
        form.setBeer(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(getBeerGrid(), getBeerForm());
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private BeerForm getBeerForm() {
        this.form = new BeerForm();
        form.setWidth("25em");

        form.addSaveListener(this::saveBeer);
       // form.addDeleteListener(this::deleteBeer);
        form.addCloseListener(event -> closeEditor());
        return form;
    }

    private void saveBeer(BeerForm.SaveEvent saveEvent) {
        ResponseEntity<BeerResponseDTO> response = restTemplate
                .postForEntity("/api/beer", saveEvent.getBeer(), BeerResponseDTO.class);
        closeEditor();
    }

    private void deleteBeer(BeerForm.DeleteEvent deleteEvent) {
        // to implement
    }

    private Component getCityToolbar() {
        this.filterCity = new TextField();
        filterCity.setPlaceholder("Wpisz miasto...");
        filterCity.setClearButtonVisible(true);
        filterCity.setValueChangeMode(ValueChangeMode.LAZY);
        filterCity.addValueChangeListener(event -> updateList(filterCity.getValue()));

        Button getCityButton = new Button("Szukaj");

        HorizontalLayout toolbar = new HorizontalLayout(filterCity, getCityButton);
        this.searchToolbar = toolbar;
        return toolbar;
    }

    private Grid getBeerGrid() {
        this.grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(beer -> beer.getId()).setHeader("Id");
        grid.addColumn(beer -> beer.getBrand()).setHeader("Marka");
        grid.addColumn(beer -> beer.getType()).setHeader("Typ");
        grid.addColumn(beer -> beer.getVolume()).setHeader("Objętość");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editBeer(event.getValue()));
        return grid;
    }

    private void editBeer(BeerResponseDTO beer) {
        if (beer == null) {
            closeEditor();
        } else {
            BeerRequestDTO formBeer = convertToRequest(beer);
            form.setBeer(formBeer);
            form.setVisible(true);
            addClassName("editing");
        }

    }

    private BeerRequestDTO convertToRequest(BeerResponseDTO beerResponse) {
        BeerRequestDTO beerRequest = new BeerRequestDTO();
        beerRequest.setBrand(beerResponse.getBrand());
        beerRequest.setType(beerResponse.getType());
        beerRequest.setVolume(beerResponse.getVolume());
        return beerRequest;
    }

    private void updateList(String city) {
        String uri = buildURI("/api/beer", Map.of("city", city));
        ResponseEntity<List<BeerResponseDTO>> response = restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<BeerResponseDTO>>() {});
        List<BeerResponseDTO> beers = response.getBody();
        this.grid.setItems(beers);
    }

    private String buildURI(String uriString, Map<String, ?> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
        for (var entry : parameters.entrySet()) {
            builder
                    .queryParam(entry.getKey(), entry.getValue());
        }
        String urlTemplate = builder.encode().toUriString();
        return urlTemplate;
    }

    private void updateList() {
        updateList("Olsztyn");
    }
}
