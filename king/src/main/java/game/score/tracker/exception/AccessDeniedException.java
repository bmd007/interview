package game.score.tracker.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("missing (valid) session key");
    }
}
