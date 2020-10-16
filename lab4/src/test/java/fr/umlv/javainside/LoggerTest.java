package fr.umlv.javainside;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    @Test
    void of() {
        class Foo {}
        var logger = Logger.of(Foo.class, __ -> {});
        assertNotNull(logger);
    }

    @Test
    void ofError() {
        class Foo {}
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> Logger.of(Foo.class, null)),
                () -> assertThrows(NullPointerException.class, () -> Logger.of(null, __ -> {}))
        );
    }

    @Test
    public void log() {
        class Foo {}
        var logger = Logger.of(Foo.class, message -> {
            assertEquals("toto", message);
        });
        logger.log("toto");
    }

    @Test
    public void logWithNullAsValue() {
        class Foo {}
        var logger = Logger.of(Foo.class, Assertions::assertNull);
        logger.log(null);
    }

//    private static final Logger LOGGER_LAMBDA = Logger.lambdaOf(LoggerBenchMark.class, message -> { /*empty*/ });
//    private static final Logger LOGGER_RECORD = Logger.recordOf(LoggerBenchMark.Foo.class, message -> { /*empty*/ });

    @Test
    public void lambdaOf() {
        class Foo {}
        var logger = Logger.lambdaOf(Foo.class, message -> {
            assertEquals("toto", message);
        });
        logger.log("toto");

        Logger.Implementation.enable(Foo.class, false);
        logger.log("notToto");
    }

    @Test
    public void recordOf() {
        class Foo {}
        var logger = Logger.recordOf(Foo.class, message -> {
            assertEquals("toto", message);
        });
        logger.log("toto");

        Logger.Implementation.enable(Foo.class, false);
        logger.log("notToto");
    }
}
