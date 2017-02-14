package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.backend.definition.BasicBackendDefinitionBuilderFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InMemoryBasicDefinitionBuilder;
import org.junit.Test;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class BuilderTest {

    @Test
    public void testSimple(){
        System.out.println(testSimple(basicBuilder()));
    }

    private <Q> Q testSimple(BasicBackendDefinitionBuilderFactory<Q> builder){
        return builder.newGroup()
                .withParameter("A")
                .withParameter("B")
                .withGroupName("a.b");
    }

    @Test
    public void testMapped(){
        testMapped(basicBuilder());
    }

    private <Q> Q testMapped(BasicBackendDefinitionBuilderFactory<Q> builder){
        return builder.mapped(builder.newGroup()
                .withParameter("A")
                .withParameter("B"))
                .withGroupName("a.b");
    }

    public <Q> void test(BasicBackendDefinitionBuilderFactory<Q> builder){
        builder.mapped(
            builder.ranged(
                builder.newGroup()
                        .withParameter("A")
                        .withParameter("B")));
    }

    private BasicBackendDefinitionBuilderFactory<?> basicBuilder(){
        return new InMemoryBasicDefinitionBuilder();
    }
}
