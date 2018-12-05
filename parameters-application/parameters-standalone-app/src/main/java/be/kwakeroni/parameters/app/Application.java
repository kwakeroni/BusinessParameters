package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.app.support.MainWaiter;
import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.core.support.backend.ConfigurationSupport;
import be.kwakeroni.parameters.core.support.util.function.ThrowingPredicate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.UnaryOperator;

import static be.kwakeroni.parameters.core.support.util.function.ThrowingFunction.unchecked;

public class Application {

    private final Server server;

    public static void main(String[] args) throws Exception {
        initLogging();
        try {
            Optional<Application> app = create(args);
            if (app.isPresent()) {
                try (AutoCloseable closeable = app.get().start()) {
                    MainWaiter.waitForExit();
                }
            }
        } catch (ParseException exc) {
            System.out.println(exc.getMessage());
            Opt.HELP.handle("");
        }
    }

    public static Optional<Application> create(String... args) throws CLIException, ParseException {
        return createServer(args)
                .map(Application::new);
    }

    public static Optional<Application> create(Properties configurationProperties) {
        return createServer(configurationProperties)
                .map(Application::new);
    }

    private Application(Server server) {
        this.server = server;
    }

    private static void initLogging() {
        System.out.println("log4j.configuration=" + System.getProperty("log4j.configuration"));
        if (System.getProperty("log4j.configuration") == null) {
            if (Thread.currentThread().getContextClassLoader().getResource("log4j.properties") == null) {
                System.setProperty("log4j.configuration", "log4j.fallback.properties");
            }
        }
    }

    public AutoCloseable start() throws IOException {
        this.server.start();
        return this.server;
    }

    static Optional<Server> createServer(String[] args) throws CLIException, ParseException {
        CommandLineParser clParser = new DefaultParser();
        CommandLine commandLine = clParser.parse(CLI_OPTIONS, args);

        for (Opt opt : Opt.values()) {
            if (!opt.handle(commandLine)) {
                return Optional.empty();
            }
        }

        return Optional.of(new Server());
    }

    static Optional<Server> createServer(Properties configurationProperties) {
        Configuration configuration = ConfigurationSupport.of(configurationProperties);
        ServerConfigurationProvider.setConfiguration(configuration);
        return Optional.of(new Server());
    }


    private static final Options CLI_OPTIONS;

    static {
        CLI_OPTIONS = new Options();
        for (Opt opt : Opt.values()) {
            CLI_OPTIONS.addOption(opt.getOption());
        }
    }

    private enum Opt {
        HELP(option -> option
                .longOpt("help")
                .desc("Shows this help message"),
                value -> {
                    new HelpFormatter().printHelp("java -jar " + getJarFileName() + " [OPTIONS]", CLI_OPTIONS);
                    return false;
                }),
        CONFIG('c', option -> option
                .longOpt("config")
                .desc("Specifies the configuration file to be used")
                .hasArg()
                .argName("config file"),
                value -> {
                    File configurationFile = new File(value);
                    Configuration configuration = ConfigurationSupport.ofPropertiesFile(configurationFile);
                    ServerConfigurationProvider.setConfiguration(configuration);
                    return true;
                }),;

        private final String opt;
        private final Option option;
        private final ThrowingPredicate<String, ?> handler;

        Opt(char opt, UnaryOperator<Option.Builder> optionSpec, ThrowingPredicate<String, ?> handler) {
            this.opt = String.valueOf(opt);
            this.handler = handler;
            Option.Builder builder = Option.builder(this.opt);
            this.option = optionSpec.apply(builder).build();
        }

        Opt(UnaryOperator<Option.Builder> optionSpec, ThrowingPredicate<String, ?> handler) {
            this.opt = null;
            this.handler = handler;
            Option.Builder builder = Option.builder();
            this.option = optionSpec.apply(builder).build();
        }


        Option getOption() {
            return this.option;
        }

        private String getOptKey() {
            return (this.opt != null) ? this.opt : this.option.getLongOpt();
        }

        boolean handle(CommandLine commandLine) throws CLIException {
            if (commandLine.hasOption(getOptKey())) {
                String optValue = commandLine.getOptionValue(getOptKey());
                return handle(optValue);
            }
            return true;
        }

        boolean handle(String value) throws CLIException {
            try {
                return this.handler.test(value);
            } catch (Exception e) {
                throw new CLIException(e);
            }
        }
    }

    private static String getJarFileName() {
        return Optional.ofNullable(Application.class.getResource("Application.class"))
                .map(unchecked(URL::toURI))
                .filter(uri -> "jar".equals(uri.getScheme()))
                .map(URI::getSchemeSpecificPart)
                .filter(path -> path.startsWith("file:"))
                .filter(path -> path.contains("!"))
                .map(path -> path.substring("file:".length(), path.indexOf('!')))
                .map(Paths::get)
                .map(Path::getFileName)
                .map(Path::toString)
                .orElse("parameters-standalone-app.jar");

    }

    public static class CLIException extends Exception {
        public CLIException(String message) {
            super(message);
        }

        public CLIException(String message, Throwable cause) {
            super(message, cause);
        }

        public CLIException(Throwable cause) {
            super(cause);
        }
    }
}
