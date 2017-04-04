package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryBackendTest {

    private InMemoryBackend backend;
    @Mock
    private GroupData group1Data;
    @Mock
    private BackendGroup<InMemoryQuery<?>> group1;
    private String group1Name = "group1";
    @Mock
    private Stream<EntryData> group1Stream;
    @Mock
    private GroupData group2Data;
    @Mock
    private BackendGroup<InMemoryQuery<?>> group2;
    private String group2Name = "group2";
    @Mock
    private BackendQuery<InMemoryQuery<?>, Object> backendQuery;
    @Mock
    private InMemoryQuery<Object> inMemoryQuery;
    @Mock
    private Object value;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EntryModification entryModification;

    @Before
    public void setUpData() {
        backend = new InMemoryBackend();
        backend.addGroupData(group1Name, group1Data);
        backend.addGroupData(group2Name, group2Data);

//        doReturn(group1Name).when(group1).getName();
//        doReturn(group2Name).when(group2).getName();
        doReturn(group1Stream).when(group1Data).getEntries();
        doReturn(inMemoryQuery).when(backendQuery).raw();
    }

    @Test
    public void testGet() throws Exception {
        doReturn(Optional.of(value)).when(inMemoryQuery).apply(group1Stream);

        Object result = backend.select(group1Name, backendQuery);

        assertThat(result).isSameAs(value);

        verifyZeroInteractions(group2Data);
    }

    @Test
    public void testGetReturnsNullWhenNoResult() throws Exception {
        doReturn(Optional.empty()).when(inMemoryQuery).apply(group1Stream);

        Object result = backend.select(group1Name, backendQuery);

        assertThat(result).isNull();
    }

    @Test
    public void testGetFailsWhenQueryReturnsNull() throws Exception {
        doReturn(null).when(inMemoryQuery).apply(group1Stream);

        assertThatThrownBy(() -> backend.select(group1Name, backendQuery))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSet() throws Exception {
        when(inMemoryQuery.getEntryModification(value, group1Stream)).thenReturn(entryModification);

        backend.update(group1Name, backendQuery, value);

        verify(inMemoryQuery).getEntryModification(value, group1Stream);
        verify(group1Data).modifyEntry(entryModification.getEntry(), entryModification.getModifier());
        verifyZeroInteractions(group2Data);
    }

    @Test
    public void testGetGroupNames() throws Exception {
        assertThat(backend.getGroupNames()).containsExactlyInAnyOrder(group1Name, group2Name);
    }

}