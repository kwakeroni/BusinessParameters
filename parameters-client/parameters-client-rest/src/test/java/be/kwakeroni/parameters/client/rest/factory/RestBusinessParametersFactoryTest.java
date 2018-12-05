package be.kwakeroni.parameters.client.rest.factory;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.rest.RestBusinessParametersClient;
import be.kwakeroni.test.extension.ContextClassLoaderExtension;
import be.kwakeroni.test.extension.ContextClassLoaderExtension.AddToClasspath;
import be.kwakeroni.test.extension.TemporaryFolderExtension;
import be.kwakeroni.test.extension.TemporaryFolderExtension.TemporaryFolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(TemporaryFolderExtension.class)
@ExtendWith(ContextClassLoaderExtension.class)
class RestBusinessParametersFactoryTest {

    private static final String URL = "testurl";
    private static final Map<String, String> PROPERTIES = Collections.singletonMap("parameters.rest.url", URL);

    @Mock
    BiConsumer<String, ClientWireFormatterContext> clientConstructor;
    @Captor
    ArgumentCaptor<String> url;
    @Captor
    ArgumentCaptor<ClientWireFormatterContext> context;
    @TemporaryFolder
    @AddToClasspath
    Path temporaryClasspath;

    RestBusinessParametersFactory factory = new RestBusinessParametersFactory() {
        @Override
        RestBusinessParametersClient createClient(String url, ClientWireFormatterContext context) {
            clientConstructor.accept(url, context);
            return super.createClient(url, context);
        }
    };

    @Test
    void isExposedAsAService() {
        Iterator<BusinessParametersFactory> iterator = ServiceLoader.load(BusinessParametersFactory.class).iterator();

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isInstanceOf(RestBusinessParametersFactory.class);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void getInstance() {
        BusinessParameters parameters = factory.getInstance(PROPERTIES);
        assertThat(parameters)
                .isNotNull()
                .isInstanceOf(RestBusinessParametersClient.class);
    }

    @Test
    void getWritableInstance() {
        WritableBusinessParameters parameters = factory.getWritableInstance(PROPERTIES);
        assertThat(parameters)
                .isNotNull()
                .isInstanceOf(RestBusinessParametersClient.class);
    }

    @Test
    void getsUrlFromProperties() {
        BusinessParameters parameters = factory.getInstance(PROPERTIES);
        verify(clientConstructor).accept(url.capture(), any());

        assertThat(url.getValue()).isEqualTo(URL);
    }

    @Test
    void loadsClientWireFormatters() {
        registerService(ClientWireFormatterFactory.class, TestClientWireFormatterFactory.class);

        BusinessParameters parameters = factory.getInstance(PROPERTIES);
        verify(clientConstructor).accept(any(), context.capture());

        assertThat(context.getValue()).isNotNull();

        TestClientWireFormatter formatter = context.getValue().getWireFormatter(TestClientWireFormatter.class);
        assertThat(formatter).isSameAs(TestClientWireFormatterFactory.FORMATTER);
    }

    public static interface TestClientWireFormatter extends ClientWireFormatter {

    }

    public static class TestClientWireFormatterFactory implements ClientWireFormatterFactory {

        static final TestClientWireFormatter FORMATTER = mock(TestClientWireFormatter.class);

        @Override
        public String getWireFormat() {
            return "json";
        }

        @Override
        public void visitInstances(Visitor visitor) {
            visitor.visit(TestClientWireFormatter.class, FORMATTER);
        }
    }

    private <S> void registerService(Class<S> serviceClass, Class<? extends S> implementationClass) {
        Path servicesDir = temporaryClasspath.resolve("META-INF/services");
        try {
            Files.createDirectories(servicesDir);
            Files.write(servicesDir.resolve(serviceClass.getName()), Collections.singleton(implementationClass.getName()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}