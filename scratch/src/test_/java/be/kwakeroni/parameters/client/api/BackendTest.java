package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.test.Dag;
import be.kwakeroni.parameters.test.Environment;
import be.kwakeroni.parameters.test.Slot;
import be.kwakeroni.parameters.test.TVProgram;
import org.junit.Test;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BackendTest {

    private Environment env = new Environment();

    @Test
    public void test(){
        String programName =
        TVProgram.using(env.getParameters())
                .forKey(Dag.MAANDAG)
                .at(Slot.atHalfPast(8))
                .getValue(TVProgram.NAME);

        System.out.println(programName);

    }

}
