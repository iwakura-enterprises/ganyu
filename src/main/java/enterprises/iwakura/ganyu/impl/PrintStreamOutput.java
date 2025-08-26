package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.Output;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;

/**
 * An implementation of {@link Output} that writes messages to provided {@link PrintStream} instances.
 */
@RequiredArgsConstructor
public class PrintStreamOutput implements Output {

    private final PrintStream infoStream;
    private final PrintStream errorStream;

    @Override
    public void info(String message) {
        infoStream.println(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        errorStream.println(message);
        if (throwable != null) {
            throwable.printStackTrace(errorStream);
        }
    }
}
