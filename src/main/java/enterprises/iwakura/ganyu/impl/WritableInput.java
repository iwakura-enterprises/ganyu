package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.Input;

/**
 * An implementation of {@link Input} that allows writing input programmatically.
 */
public class WritableInput implements Input {

    private final Object MUTEX = new Object();
    private String lastInput;

    @Override
    public String readNextInput() {
        synchronized (MUTEX) {
            while (lastInput == null) {
                try {
                    MUTEX.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            String input = lastInput;
            lastInput = null;
            return input;
        }
    }

    public void write(String text) {
        synchronized (MUTEX) {
            lastInput = text;
            MUTEX.notifyAll();
        }
    }
}
