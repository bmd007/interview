
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SmoothieMachine {
    static Map menu = new HashMap<String, List<String>>() {{
        put("Just Desserts", Arrays.asList("peanut", "banana", "ice cream", "chocolate", "cherry"));
        put("Classic", Arrays.asList("strawberry", "banana", "pineapple", "mango", "peach", "honey", "ice", "yogurt"));
        put("Forest Berry", Arrays.asList("strawberry", "raspberry", "blueberry", "honey", "ice", "yogurt"));
        put("Freezie", Arrays.asList("blackberry", "blueberry", "black currant", "grape juice", "frozen yogurt"));
        put("Greenie", Arrays.asList("green apple", "kiwi", "lime", "avocado", "spinach", "ice", "apple juice"));
        put("Vegan Delite", Arrays.asList("strawberry", "passion fruit", "pineapple", "mango", "peach", "ice", "soy " + "milk"));
    }};

    public static String ingredients(String order) {
        if (order.trim().isEmpty()) {
            throw new IllegalArgumentException("order can not be empty");
        }
        String[] orderParts = order.split(",");
        ArrayList<String> ingredients = Optional.ofNullable((List<String>) menu.get(orderParts[0]))
                .map(ArrayList::new)
                .orElseThrow(() -> new IllegalArgumentException("order contains a non existing smoothie"));
        for (int i = 1; i < orderParts.length; i++) {
            String toBeRemoved = orderParts[i].split("-")[1];
            ingredients.remove(toBeRemoved);
        }
        List<String> sortedIngredients = ingredients.stream().sorted().collect(Collectors.toList());
        return String.join(",", sortedIngredients);
    }


    @Test
    public void classicSmoothie() {
        assertEquals("banana,honey,ice,mango,peach,pineapple,strawberry,yogurt",
                ingredients("Classic"));
    }

    @Test
    public void classicSmoothieWithoutStrawberry() {
        assertEquals("banana,honey,ice,mango,peach,pineapple,yogurt",
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
}
