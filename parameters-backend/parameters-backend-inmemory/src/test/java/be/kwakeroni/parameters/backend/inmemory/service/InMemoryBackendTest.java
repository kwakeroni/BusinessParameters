package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendGroupFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryBackendTest {

    private InMemoryBackend backend;
    @Mock
    private GroupDataStore dataStore;
    @Mock
    private ParameterGroupDefinition<?> group1Definition;
    @Mock
    private EntryData entry1Data;
    @Mock
    private InMemoryGroup group1;
    private String group1Name = "group1";
    @Mock
    private GroupData group1Data;
    @Mock
    private ParameterGroupDefinition<?> group2Definition;
    @Mock
    private EntryData entry2Data;
    @Mock
    private InMemoryGroup group2;
    private String group2Name = "group2";
    @Mock
    private GroupData group2Data;
    @Mock
    private BackendQuery<InMemoryQuery<?>, Object> backendQuery;
    @Mock
    private InMemoryQuery<Object> inMemoryQuery;
    @Mock
    private InMemoryBackendGroupFactoryContext factoryContext;
    @Mock
    private Supplier<Stream<ParameterGroupDefinition<?>>> definitions;
    @Mock
    private Object value;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EntryModification entryModification;

    @Before
    public void setUpData() {
        when(group1Definition.getName()).thenReturn(group1Name);
        when(group2Definition.getName()).thenReturn(group2Name);
        when(group1Definition.apply(factoryContext)).thenReturn(group1);
        when(group2Definition.apply(factoryContext)).thenReturn(group2);
        when(definitions.get()).thenAnswer((i) -> Stream.of(group1Definition, group2Definition));
        when(group1Data.getGroup()).thenReturn(group1);
        when(group1Data.getEntries()).thenAnswer(__ -> Stream.of(entry1Data));
        when(group2Data.getGroup()).thenReturn(group2);
        when(group2Data.getEntries()).thenAnswer(__ -> Stream.of(entry2Data));
        when(dataStore.getGroupData(group1)).thenReturn(group1Data);
        when(dataStore.getGroupData(group2)).thenReturn(group2Data);

        backend = new InMemoryBackend(factoryContext, definitions, dataStore);

        doReturn(inMemoryQuery).when(backendQuery).raw();
    }

    @Test
    public void testGet() {
        doReturn(Optional.of(value)).when(inMemoryQuery).apply(streamWithOnly(entry1Data));

        Object result = backend.select(group1Name, backendQuery);

        assertThat(result).isSameAs(value);

        verifyZeroInteractions(entry2Data);
    }

    @Test
    public void testGetReturnsNullWhenNoResult() {
        doReturn(Optional.empty()).when(inMemoryQuery).apply(streamWithOnly(entry1Data));

        Object result = backend.select(group1Name, backendQuery);

        assertThat(result).isNull();
    }

    @Test
    public void testGetFailsWhenQueryReturnsNull() {
        doReturn(null).when(inMemoryQuery).apply(streamWithOnly(entry1Data));

        assertThatThrownBy(() -> backend.select(group1Name, backendQuery))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSet() {
        when(inMemoryQuery.getEntryModification(eq(value), streamWithOnly(entry1Data))).thenReturn(entryModification);

        backend.update(group1Name, backendQuery, value);

        verifyZeroInteractions(entry2Data);
    }

    @Test
    public void testGetGroupNames() {
        assertThat(backend.getGroupNames()).containsExactlyInAnyOrder(group1Name, group2Name);
    }

    private Stream<EntryData> streamWithOnly(EntryData entry) {
        return Mockito.argThat(hasOnly(entry)::matches);
    }

    private Matcher<Stream<EntryData>> hasOnly(EntryData entry) {
        return new BaseMatcher<Stream<EntryData>>() {

            List<?> entries;

            @Override
            public boolean matches(Object o) {
                this.entries = ((Stream<?>) o).collect(Collectors.toList());

                return this.entries.size() == 1 && this.entries.get(0) == entry;
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(Collections.singletonList(entry));
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                List<?> result = (this.entries == null) ? ((Stream<?>) item).collect(Collectors.toList()) : this.entries;
                description.appendValue(result);
            }
        };
    }
}