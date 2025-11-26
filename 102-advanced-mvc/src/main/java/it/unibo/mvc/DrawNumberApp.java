package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_ATTEMPTS = 10;
    private static final String CONFIG_FILE = "config.yml";
    private static final String PATH = "src" 
                                        + File.separator 
                                        + "main" 
                                        + File.separator 
                                        + "resources" 
                                        + File.separator 
                                        + CONFIG_FILE;
    private static final String FILE_OUTPUT = "src" 
                                                + File.separator 
                                                + "main" 
                                                + File.separator 
                                                + "resources" 
                                                + File.separator 
                                                + "output.txt";

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
        * if try-catch fails
        */
        int min = DEFAULT_MIN;
        int max = DEFAULT_MAX; 
        int attempts = DEFAULT_ATTEMPTS;
        /*
        * Read rules 
        */
        try (BufferedReader br = new BufferedReader(new FileReader(PATH, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {   //NOPMD
                final StringTokenizer st = new StringTokenizer(line, ":");
                final String key = st.nextToken().trim();
                final int value = Integer.parseInt(st.nextToken().trim());
                switch (key) {
                    case "minimum":
                        min = value;
                        break;
                    case "maximum":
                        max = value;
                        break;
                    case "attempts":
                        attempts = value;
                        break;
                    default: 
                        throw new IllegalStateException("Error in " + PATH);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();  //NOPMD
        }
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        this.model = new DrawNumberImpl(min, max, attempts);
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (final IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException if file not found
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(
            new DrawNumberViewImpl(),
            new DrawNumberViewImpl(),
            new PrintStreamView(System.out),
            new PrintStreamView(FILE_OUTPUT)
        );
    }

}
