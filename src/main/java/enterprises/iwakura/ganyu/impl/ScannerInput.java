package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.Input;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.Scanner;

/**
 * An implementation of {@link Input} that reads input from an {@link InputStream} using a {@link Scanner}.
 */
public class ScannerInput implements Input {

    protected final Scanner scanner;

    public ScannerInput(InputStream inputStream) {
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public String readNextInput() {
        return scanner.nextLine();
    }
}
