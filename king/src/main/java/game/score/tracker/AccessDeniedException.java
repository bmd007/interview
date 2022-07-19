package game.score.tracker;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("missing (valid) session key");
    }
}
