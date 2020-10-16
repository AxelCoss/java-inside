package fr.umlv.javainside;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LoggerBenchMark {

    private record Foo() {}

    private class FooClass {}

    private static final Logger LOGGER = Logger.of(LoggerBenchMark.class, message -> { /*empty*/ });
    private static final Logger LOGGER_LAMBDA = Logger.lambdaOf(LoggerBenchMark.class, message -> { /*empty*/ });
    private static final Logger LOGGER_RECORD = Logger.recordOf(Foo.class, message -> { /*empty*/ });
    private static final Logger LOGGER_DISABLED;

    static {
        LOGGER_DISABLED = Logger.lambdaOf(FooClass.class, System.err::println);
        Logger.Implementation.enable(FooClass.class, false);
    }



    @Benchmark
    public void no_op() {
        // empty
    }

    @Benchmark
    public void simple_logger() {
        LOGGER.log("");
    }

    @Benchmark
    public void simple_logger_lambda() {
        LOGGER_LAMBDA.log("");
    }

    @Benchmark
    public void simple_logger_record() {
        LOGGER_RECORD.log("");
    }

    @Benchmark
    public void simple_logger_disabled() {
        LOGGER_DISABLED.log("");
    }
}

