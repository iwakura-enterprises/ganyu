package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.Output;

/**
 * An implementation of {@link Output} that writes to System.out and System.err.
 */
public class ConsoleOutput extends PrintStreamOutput {

    public ConsoleOutput() {
        super(System.out, System.err);
    }
}
