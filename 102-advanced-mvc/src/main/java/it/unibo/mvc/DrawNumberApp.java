package it.unibo.mvc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        /*
        * Read rules 
        */
        final Configuration.Builder builder = new Configuration.Builder();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {   //NOPMD
                final StringTokenizer st = new StringTokenizer(line, ":");
                final String key = st.nextToken().trim();
                final int value = Integer.parseInt(st.nextToken().trim());
                switch (key) {
                    case "minimum":
                        builder.setMin(value);
                        break;
                    case "maximum":
                        builder.setMax(value);
                        break;
                    case "attempts":
                        builder.setAttempts(value);
                        break;
                    default: 
                        throw new IllegalStateException("Error in " + PATH);
                }
            }
        } catch (final IOException e) {
            for (final DrawNumberView view : views) {
                    view.displayError(e.getMessage()); 
            }
        }
        final Configuration conf = builder.build();
        if (conf.isConsistent()) {
            this.model = new DrawNumberImpl(conf);
        } else {
            for (final DrawNumberView view : views) {
                    view.displayError("Error: usign a defualt value"); 
            }
            this.model = new DrawNumberImpl(new Configuration.Builder().build());
        }
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
    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "Acceptable for exercising purposes."
    )
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
