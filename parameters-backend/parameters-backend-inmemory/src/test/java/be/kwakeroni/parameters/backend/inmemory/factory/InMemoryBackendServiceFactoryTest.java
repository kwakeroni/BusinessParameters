package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.fallback.TransientGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.persistence.PersistedGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import be.kwakeroni.test.logging.LogEvent;
import be.kwakeroni.test.logging.LoggerSpy;
import be.kwakeroni.test.logging.SLF4JTestLog;
import be.kwakeroni.test.util.TestConfigurationProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.slf4j.event.Level;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryBackendServiceFactoryTest {

    private static ThreadLocal<TestState> testState = new ThreadLocal<>();
    private static ThreadLocal<ClassLoader> previousClassLoader = new ThreadLocal<>();
    private static ThreadLocal<Supplier<GroupDataStore>> dataStoreSupplier = new ThreadLocal<>();

    private InMemoryBackendServiceFactory factory;
    private TemporaryFolder folder = new TemporaryFolder();
    private LoggerSpy logger;
    @Mock
    private BackendConstructor backendConstructor;
    @Mock
    private UnaryOperator<Properties> propertiesOperator;
    @Mock
    private Configuration configuration;

    @BeforeEach
    void setupContext() throws Exception {
        folder.create();
        folder.newFolder("META-INF", "services");
        previousClassLoader.set(Thread.currentThread().getContextClassLoader());

        URL url = folder.getRoot().toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        InMemoryBackendServiceFactory.INSTANCE = null;
        TestConfigurationProvider.setConfiguration(this.configuration);
        when(this.configuration.get(any())).thenCallRealMethod();
    }

    @AfterEach
    void resetContext() {
        Thread.currentThread().setContextClassLoader(previousClassLoader.get());
        previousClassLoader.remove();
        folder.delete();

        InMemoryBackendServiceFactory.INSTANCE = null;
        TestConfigurationProvider.clear();
    }

    @BeforeEach
    void backupState() {
        testState.set(new TestState());
        dataStoreSupplier.set(InMemoryBackendServiceFactory.DATA_STORE_SUPPLIER);

    }

    @AfterEach
    void resetState() {
        InMemoryBackendServiceFactory.setDataStoreSupplier(dataStoreSupplier.get());
        testState.remove();
        dataStoreSupplier.remove();
        SLF4JTestLog.reset();
    }

    @BeforeEach
    void setUp() {
        this.logger = SLF4JTestLog.loggerSpy();
        this.factory = new InMemoryBackendServiceFactory() {
            @Override
            BusinessParametersBackend<InMemoryQuery<?>> getInstance(InMemoryBackendGroupFactoryContext factories, Supplier<Stream<ParameterGroupDefinition<?>>> definitions, GroupDataStore dataStore) {
                backendConstructor.create(factories, definitions, dataStore);
                return super.getInstance(factories, definitions, dataStore);
            }
        };

        when(propertiesOperator.apply(any())).then(returnsFirstArg());
    }

    @Test
    @DisplayName("Creates an InMemoryBackend")
    void testGetInstance() {

        BusinessParametersBackend<?> backend = factory.getInstance();

        assertThat(backend).isInstanceOf(InMemoryBackend.class);
    }

    @Test
    @DisplayName("Creates only one InMemoryBackend (singleton)")
    void testGetInstanceIsSingleton() {

        BusinessParametersBackend<?> backend = factory.getInstance();
        BusinessParametersBackend<?> backend2 = factory.getInstance();

        assertThat(backend).isSameAs(backend2);
    }


    @Test
    @DisplayName("Supplies definitions from ServiceLoader")
    void testGetInstanceWithDefinitions(@Mock ParameterGroupDefinition<?> definition1, @Mock ParameterGroupDefinition<?> definition2) throws Exception {

        exposeService(ParameterGroupDefinitionCatalog.class, TestCatalog.class);

        when(definition1.getName()).thenReturn("group1");
        when(definition2.getName()).thenReturn("group-B");
        testState.get().definitions = Arrays.asList(definition1, definition2);

        factory.getInstance();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Supplier<Stream<ParameterGroupDefinition<?>>>> supplier = ArgumentCaptor.forClass(Supplier.class);
        verify(backendConstructor).create(any(), supplier.capture(), any());

        assertThat(supplier.getValue().get()).contains(definition1, definition2);
    }

    @Test
    @DisplayName("Supplies group factories from ServiceLoader")
    void testGetInstanceWithFactoryContext() throws Exception {

        exposeService(InMemoryGroupFactory.class, TestGroupFactory.class);

        factory.getInstance();

        ArgumentCaptor<InMemoryBackendGroupFactoryContext> context = ArgumentCaptor.forClass(InMemoryBackendGroupFactoryContext.class);

        verify(backendConstructor).create(context.capture(), any(), any());

        assertThat(context.getValue().getVisitor(TestGroupFactory.class)).isNotNull();
    }

    @Nested
    @DisplayName("Supplies a default GroupDataStore")
    class SuppliesDefaultTest {

        @Test
        @DisplayName(" - transient if no storage folder is provided")
        void testTransientGroupDataStore() {

            factory.getInstance();

            ArgumentCaptor<GroupDataStore> store = ArgumentCaptor.forClass(GroupDataStore.class);
            verify(backendConstructor).create(any(), any(), store.capture());

            assertThat(store.getValue())
                    .isNotNull()
                    .isInstanceOf(TransientGroupDataStore.class);
        }

        @Test
        @DisplayName(" - logs transient storage as warning")
        void testLogTransientGroupDataStore() {

            factory.getInstance();

            ArgumentCaptor<LogEvent> log = ArgumentCaptor.forClass(LogEvent.class);
            verify(logger).log(log.capture());
            assertThat(log.getValue().getLevel()).isEqualTo(Level.WARN);
        }

        @Test
        @DisplayName(" - persistent if storage folder is provided")
        void testPersistentGroupDataStore() throws Exception {

            File storageFolder = folder.newFolder();
            exposeProperties("inmemory.storage.folder", storageFolder.getAbsolutePath());

            factory.getInstance();

            ArgumentCaptor<GroupDataStore> store = ArgumentCaptor.forClass(GroupDataStore.class);
            verify(backendConstructor).create(any(), any(), store.capture());

            assertThat(store.getValue())
                    .isNotNull()
                    .isInstanceOf(PersistedGroupDataStore.class);
        }

        @Test
        @DisplayName(" - logs persistent storage as info")
        void testLogPersistentGroupDataStore() throws Exception {

            File storageFolder = folder.newFolder();
            exposeProperties("inmemory.storage.folder", storageFolder.getAbsolutePath());

            factory.getInstance();

            ArgumentCaptor<LogEvent> log = ArgumentCaptor.forClass(LogEvent.class);
            verify(logger).log(log.capture());
            assertThat(log.getValue().getLevel()).isEqualTo(Level.INFO);
            assertThat(log.getValue().getFormattedMessage()).contains(storageFolder.getAbsolutePath());
        }

    }

    @Test
    @DisplayName("Supports overriding the default GroupDataStore supplier")
    void testAllowsOverridingGroupDataStore(@Mock GroupDataStore store) {

        InMemoryBackendServiceFactory.setDataStoreSupplier(() -> store);

        factory.getInstance();

        verify(backendConstructor).create(any(), any(), same(store));
    }

    public static class TestCatalog implements ParameterGroupDefinitionCatalog {
        @Override
        public Stream<ParameterGroupDefinition<?>> stream() {
            return testState.get().definitions.stream();
        }
    }

    public static class TestGroupFactory implements InMemoryGroupFactory {
        @Override
        public void register(Registry registry) {
            registry.register(TestGroupFactory.class, this);
        }

        @Override
        public void unregister(Consumer<Class<?>> registry) {
            registry.accept(TestGroupFactory.class);
        }
    }

    private static class TestState {
        private List<ParameterGroupDefinition<?>> definitions = Collections.emptyList();
    }

    private interface BackendConstructor {
        void create(InMemoryBackendGroupFactoryContext factories, Supplier<Stream<ParameterGroupDefinition<?>>> definitions, GroupDataStore dataStore);
    }

    private <S> void exposeService(Class<S> service, Class<? extends S> implementor) throws Exception {
        File spec = new File(folder.getRoot(), "META-INF/services/" + service.getName());
        Files.write(spec.toPath(), Collections.singleton(implementor.getName()));
    }

    private void exposeProperties(String property, String value) {
        when(this.configuration.getStringParameter(property)).thenReturn(Optional.of(value));
    }

}