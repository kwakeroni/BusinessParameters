package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;


/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(MockitoJUnitRunner.class)
public class InMemoryBackendTest {

    private InMemoryBackend backend;
    @Mock
    private BackendWireFormatterContext<InMemoryQuery<?>> context;
    @Mock
    private GroupData group1Data;
    private String group1 = "group1";
    @Mock
    private Stream<EntryData> group1Stream;
    @Mock
    private GroupData group2Data;
    private String group2 = "group2";
    @Mock
    private Object externalQuery;
    @Mock
    private InMemoryQuery<Object> internalQuery;
    @Mock
    private Object externalValue;
    @Mock
    private Object internalValue;

    @Before
    public void setUpData() {
        backend = new InMemoryBackend(context);
        backend.addGroupData(group1, group1Data);
        backend.addGroupData(group2, group2Data);

        doReturn(internalQuery).when(context).internalize(any(), same(externalQuery));
        doReturn(externalValue).when(internalQuery).externalizeResult(same(internalValue), same(context));
        doReturn(group1Stream).when(group1Data).getEntries();
    }

    @Test
    public void testGet() throws Exception {
        doReturn(Optional.of(internalValue)).when(internalQuery).apply(group1Stream);

        Object result = backend.get(group1, externalQuery);

        assertThat(result).isSameAs(externalValue);

        verifyZeroInteractions(group2Data);
    }

    @Test
    public void testGetReturnsNullWhenNoResult() throws Exception {
        doReturn(Optional.empty()).when(internalQuery).apply(group1Stream);

        Object result = backend.get(group1, externalQuery);

        assertThat(result).isNull();
    }

    @Test
    public void testGetFailsWhenQueryReturnsNull() throws Exception {
        doReturn(null).when(internalQuery).apply(group1Stream);

        assertThatThrownBy(() -> backend.get(group1, externalQuery))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSet() throws Exception {
        //backend.set(group1, externalQuery, externalValue);
    }


    @Test
    public void testGetGroupNames() throws Exception {
        assertThat(backend.getGroupNames()).containsExactlyInAnyOrder(group1, group2);
    }

}