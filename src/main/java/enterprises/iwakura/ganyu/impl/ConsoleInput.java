package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.Input;

/**
 * An implementation of {@link Input} that reads input from the console.
 */
public class ConsoleInput extends ScannerInput {

    public ConsoleInput() {
        super(System.in);
    }
}
