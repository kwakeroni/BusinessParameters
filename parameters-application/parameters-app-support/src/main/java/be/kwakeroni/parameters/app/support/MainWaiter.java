package be.kwakeroni.parameters.app.support;

import java.io.IOException;
import java.io.UncheckedIOException;

public class MainWaiter {

    public static void waitForExit() {
        try {
            System.out.println("Press 'q' to exit");
            int input = 0;
            while (input != 'q') {
                // Thread.onSpinWait();
                input = System.in.read();
            }
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

}
