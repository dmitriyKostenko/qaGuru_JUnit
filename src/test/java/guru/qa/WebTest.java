package guru.qa;

import com.codeborne.selenide.CollectionCondition;
import guru.qa.data.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class WebTest {

    @ValueSource(strings = {"Санкт-Петербург", "Москва"})
    @ParameterizedTest(name = "Проверка числа результатов в строке поиска Gismeteo для запроса {0}")
    void gismeteoSearchCommonTest(String testData) {
        open("https://www.gismeteo.ru/");
        $("input[type='search']").setValue(testData);
        $$(".search-list a")
                .shouldHave(CollectionCondition.sizeGreaterThan(5))
                .first()
                .shouldHave(text(testData));
    }

    @CsvSource(value = {
            "Москва, Россия, Москва (город федерального значения)",
            "Краснодар, Россия, Краснодарский край, Краснодар (городской округ)"
    })
    @ParameterizedTest(name = "Проверка числа результатов в строке поиска Gismeteo для запроса {0}")
    void gismeteoSearchCommonTestDifferentExpectedText(String searchQuery, String expectedText) {
        open("https://www.gismeteo.ru/");
        $("input[type='search']").setValue(searchQuery);
        $$("a.search-item")
                .shouldHave(CollectionCondition.sizeGreaterThan(5))
                .first()
                .shouldHave(text(expectedText));
    }

    static Stream<Arguments> wikipediaSiteMenuTextDataProvider() {
        return Stream.of(
                Arguments.of(List.of("Main page", "Contents", "Current events", "Random article", "About Wikipedia", "Contact us", "Donate"), Locale.EN),
                Arguments.of(List.of("Pagina principale", "Ultime modifiche", "Una voce a caso", "Nelle vicinanze", "Vetrina", "Aiuto", "Sportello informazioni"), Locale.IT),
                Arguments.of(List.of("Hauptseite", "Themenportale", "Zufälliger Artikel"), Locale.DE)
        );
    }

    @MethodSource("wikipediaSiteMenuTextDataProvider")
    @ParameterizedTest(name = "Проверка отображения названия разделов меню для локали: {1}")
    void wikipediaSiteMenuContentText(List<String> menuContent, Locale locale) {
        open("https://www.wikipedia.org/");
        $$("div.central-featured-lang").find(text(locale.name())).click();
        $$("#p-navigation li").filter(visible)
                .shouldHave(CollectionCondition.texts(menuContent));
    }

    @EnumSource(Locale.class)
    @ParameterizedTest
    void checkLocaleTest(Locale locale) {
        open("https://www.wikipedia.org/");
        $$("div.central-featured-lang").find(text(locale.name())).shouldBe(visible);
    }

}
