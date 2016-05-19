package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.basic.external.BasicExternalizer;
import be.kwakeroni.parameters.client.basic.external.StandardBasicExternalizer;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.ParameterGroup;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class PrimitiveExternalizationTest {

    private final AtomicReference<Object> externalized = new AtomicReference<>();

    private final BasicExternalizer externalizer = new StandardBasicExternalizer();

    private final ExternalizationContext context = new ExternalizationContext() {
        @Override
        public <Externalizer> Externalizer getExternalizer(Class<Externalizer> type) {
            return type.cast(externalizer);
        }
    };

    private final BusinessParameters businessParameters = new BusinessParameters() {
        @Override
        public <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query) {
            System.out.println(group.getName()+"."+query);
            Object external = query.externalize(context);
            externalized.set(external);
            return null;
        }

    };

    private final TVProgram program = TVProgram.using(businessParameters);

    @Test
    public void testCustomGroup()throws Exception {

        program.forKey(Dag.MAANDAG).at(Slot.atHalfPast(8)).getValue(TVProgram.NAME);

//        ArgumentCaptor<Object>
        dump("", wired(externalized.get()));


    }

    private void dump(String prefix, Object o){
        if (o instanceof Map){
         dump(prefix, (Map<?,?>) o);
        } else {
            System.out.println(o);
        }

    }

    private void dump(String prefix, Map<?,?> map){
        System.out.println(prefix  + "{");
        map.forEach((key, value) -> {
            System.out.print(prefix + "  " + key + " : ");
            dump(prefix + "      ", value);
        } );
        System.out.println(prefix + "}");
    }

    private <T> T wired(T o) throws Exception {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        ClassLoader root = current;
        while (root.getParent() != null){
            root = root.getParent();
        }


        try (AutoCloseable reset = () -> Thread.currentThread().setContextClassLoader(current)) {
            Thread.currentThread().setContextClassLoader(root);

            System.out.println(current);
            System.out.println(root);
            System.out.println( sun.misc.VM.latestUserDefinedLoader());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            ObjectInputStream ois = new StandardObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            T type = (T) ois.readObject();
            return type;
        }


    }
}
