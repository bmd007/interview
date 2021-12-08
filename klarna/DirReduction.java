
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.springframework.test.util.AssertionErrors.assertEquals;


public class DirReduction {

    enum Direction {

        NORTH {
            public String toString() {
                return "NORTH";
            }

            public boolean isOppositeOf(Direction other) {
                return other.equals(SOUTH);
            }
        },
        SOUTH {
            public String toString() {
                return "SOUTH";
            }

            public boolean isOppositeOf(Direction other) {
                return other.equals(NORTH);
            }
        },
        EAST {
            public String toString() {
                return "EAST";
            }

            public boolean isOppositeOf(Direction other) {
                return other.equals(WEST);
            }
        },
        WEST {
            public String toString() {
                return "WEST";
            }

            public boolean isOppositeOf(Direction other) {
                return other.equals(EAST);
            }
        };

        public abstract String toString();

        public abstract boolean isOppositeOf(Direction other);
    }

    private final static List<Direction> DIRECTIONS = Arrays.asList(DirReduction.Direction.NORTH, DirReduction.Direction.WEST, DirReduction.Direction.SOUTH, DirReduction.Direction.EAST);

    public static String[] dirReduc(String[] arr) {
        Stack<Direction> directionStack = new Stack<>();
        Arrays.stream(arr)
                .map(Direction::valueOf)
                .filter(DIRECTIONS::contains)
                .forEach(direction -> {
                    if (directionStack.empty()) {
                        directionStack.push(direction);
                    } else {
                        Direction stackTop = directionStack.peek();
                        if (stackTop.isOppositeOf(direction)) {
                            directionStack.pop();
                        } else {
                            directionStack.push(direction);
                        }
                    }
                });
        return directionStack.stream()
                .map(Direction::toString)
                .collect(Collectors.toList())
                .toArray(new String[]{});
    }

    @Test
    public void testSimpleDirReduc() throws Exception {
        assertEquals("\"NORTH\", \"SOUTH\", \"SOUTH\", \"EAST\", \"WEST\", \"NORTH\", \"WEST\"",
                new String[]{"WEST"},
                dirReduc(new String[]{"NORTH", "SOUTH", "SOUTH", "EAST", "WEST", "NORTH", "WEST"}));

        assertEquals("\"NORTH\", \"WEST\", \"SOUTH\", \"EAST\"",
                new String[]{"NORTH", "WEST", "SOUTH", "EAST"},
                dirReduc(new String[]{"NORTH", "WEST", "SOUTH", "EAST"}));
    }

}
