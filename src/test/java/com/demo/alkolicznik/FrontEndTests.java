package com.demo.alkolicznik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FrontEndTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void welcomePageTitleTest() {
        Document page = getHtml("/");
        String pageTitle = page.title();

        assertThat(pageTitle).isEqualTo("Alkolicznik");
    }

    @Test
    public void welcomePageContainsSearchBeerReferenceTest() {
        Document page = getHtml("/");
        // Link to "/search_beer" should be
        // the first link in the welcome page
        Element link = page.select("a").first();
        assertThat(link)
                .withFailMessage("There's not a single link on the page.")
                .isNotNull();

        String relHref = link.attr("href");

        assertThat(relHref)
                .withFailMessage("The first link should redirect to '/search_beer'.")
                .isEqualTo("/search_beer");
    }

    private Document getHtml(String url) {
        String rawResponse = restTemplate.getForObject(url, String.class);
        Document doc = Jsoup.parse(rawResponse);
        return doc;
    }
}
