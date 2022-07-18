package game.score.tracker;

import org.junit.jupiter.api.Test;

class ScoreKeeperApplicationTest {
    @Test
    void appHasAGreeting() {
        ScoreKeeperApplication classUnderTest = new ScoreKeeperApplication();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
    }
}
