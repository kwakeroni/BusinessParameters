package be.kwakeroni.scratch.test;

import be.kwakeroni.scratch.env.Environment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class EnvironmentTestSupport {

    protected final Environment environment;

    public EnvironmentTestSupport(Environment environment) {
        this.environment = environment;
    }

    @BeforeEach
    public void setUp() {
        environment.reset();
    }

    @AfterEach
    public void tearDown() throws Exception {
        environment.close();
    }


}
