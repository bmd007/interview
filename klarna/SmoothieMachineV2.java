package wonderland.interview.testBed;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class SmoothieMachine {

    static Map<String, Set<String>> menu = new HashMap<>() {{
        put("Just Desserts", Set.of("banana", "ice cream", "chocolate", "peanut", "cherry"));
        put("Classic", Set.of("strawberry", "banana", "pineapple", "mango", "peach", "honey"));
        put("Forest Berry", Set.of("strawberry", "raspberry", "blueberry", "honey"));
        put("Freezie", Set.of("blackberry", "blueberry", "black currant", "grape juice", "frozen yogurt"));
        put("Greenie", Set.of("green apple", "lime", "avocado", "spinach", "ice", "apple juice"));
        put("Vegan Delite", Set.of("strawberry", "passion fruit", "pineapple", "mango", "peach", "ice", "soy " + "milk"));
    }};

    public static String ingredients(String order) {
        if (order == null || order.isBlank()) {
            throw new IllegalArgumentException("order can not be empty");
        }
        String[] orderParts = order.split(",");
        if (orderParts.length < 1) {
            throw new IllegalArgumentException("order malformed");
        }
        String smoothieName = orderParts[0];
        if (smoothieName.isBlank()) {
            throw new IllegalArgumentException("order malformed");
        }
        Set<String> toBeRemovedFromOrder = new HashSet<>();
        for (int i = 1; i < orderParts.length; i++) {
            String orderPart = orderParts[i];
            if (orderPart.isBlank() || !orderPart.contains("-")|| orderPart.contains("+")) {
                throw new IllegalArgumentException();
            } else if (orderPart.contains("-")) {
                toBeRemovedFromOrder.add(orderPart.split("-")[1]);
            }
        }
        return Optional.ofNullable(menu.get(smoothieName))
                .orElseThrow(() -> new IllegalArgumentException("order contains a non existing smoothie"))
                .stream()
                .filter(ingredient -> !toBeRemovedFromOrder.contains(ingredient))
                .sorted()
                .collect(Collectors.joining(","));
    }

    @Test
    public void classicSmoothie() {
        assertEquals("banana,honey,mango,peach,pineapple,strawberry",
                ingredients("Classic"));
    }

    @Test
    public void classicSmoothieWithoutStrawberry() {
        assertEquals("banana,honey,mango,peach,pineapple",
                ingredients("Classic,-strawberry"));
    }

    @Test
    public void justDesserts() {
        System.out.println(menu.get("Just Desserts"));
        assertEquals("banana,cherry,chocolate,ice cream,peanut",
                ingredients("Just Desserts"));
    }

    @Test
    public void justDessertsWithoutIceCreamAndPeanut() {
        assertEquals("banana,cherry,chocolate",
                ingredients("Just Desserts,-ice cream,-peanut"));
    }

    @Test
    public void classicWithExtraMango() {
        assertThrows(IllegalArgumentException.class, () -> ingredients("Classic,mango"));
    }
}
