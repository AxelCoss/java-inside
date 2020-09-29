package fr.umlv.javainside;

import fr.umlv.javainside.annotation.JSONProperty;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONPrinterTest {
    public record Alien(int age, String planet) {
        public Alien {
            if (age < 0) {
                throw new IllegalArgumentException("negative age");
            }
            requireNonNull(planet);
        }
    }

    public record Person(String firstName, String lastName) {
        public Person {
            requireNonNull(firstName);
            requireNonNull(lastName);
        }
    }

    record Book(@JSONProperty("book-title") String title, int year) { }

    record BookWithEmptyAnnotation(@JSONProperty String book_title, int year) { }

    @Test
    public void convertARecordWithTwoStringArgumentIntoAJSONString() {
        Person person = new Person("Petit", "Jean");

        Map<String, Object> expectedJsonMap = IncompleteJSONParser.parse("{ \"firstName\":\"Petit\", \"lastName\":\"Jean\" }");

        assertEquals(expectedJsonMap, IncompleteJSONParser.parse(JSONPrinter.toJSON(person)));
    }

    @Test
    public void convertARecordWithAIntArgumentAndAStringArgumentIntoAJSONString() {
        Alien alien = new Alien(5, "Pluton");

        Map<String, Object> expectedJsonMap = IncompleteJSONParser.parse("{ \"age\":5, \"planet\":\"Pluton\" }");

        assertEquals(expectedJsonMap, IncompleteJSONParser.parse(JSONPrinter.toJSON(alien)));
    }

    @Test
    public void convertARecordWithAnAnnotatedComponentIntoAJSONString() {
        Book book = new Book("The journey", 2);

        Map<String, Object> expectedJsonMap = IncompleteJSONParser.parse("{ \"book-title\":\"The journey\", \"year\":2 }");

        assertEquals(expectedJsonMap, IncompleteJSONParser.parse(JSONPrinter.toJSON(book)));
    }

    @Test
    public void convertARecordWithAnEmptyAnnotatedComponentIntoAJSONString() {
        BookWithEmptyAnnotation book = new BookWithEmptyAnnotation("The journey", 2);

        Map<String, Object> expectedJsonMap = IncompleteJSONParser.parse("{ \"book-title\":\"The journey\", \"year\":2 }");

        assertEquals(expectedJsonMap, IncompleteJSONParser.parse(JSONPrinter.toJSON(book)));
    }
}
