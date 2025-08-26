package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.Output;
import lombok.Getter;

/**
 * An implementation of {@link Output} that stores the last output message and throwable for later retrieval.
 */
@Getter
public class ReadableOutput implements Output {

    private String lastOutput;
    private Throwable lastThrowable;

    @Override
    public void info(String message) {
        this.lastOutput = message;
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.lastOutput = message;
        this.lastThrowable = throwable;
    }
}
