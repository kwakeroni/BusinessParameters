import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.SimpleBackendGroup;
import be.kwakeroni.parameters.basic.wireformat.json.BasicJsonWireFormat;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by kwakeroni on 29/09/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestBasicJsonWireFormatterAsBackend {

    @Mock
    private SimpleBackendGroup<Q> simpleBackendGroup;
    @Mock
    private MappedBackendGroup<Q, ?> mappedBackendGroup;
    @Mock
    private RangedBackendGroup<Q, ?> rangedBackendGroup;
    @Mock
    private BackendWireFormatterContext context;
    @Mock
    private Q queryObject;
    @Mock
    private Q subQueryObject;

    BasicBackendWireFormatter formatter = new BasicJsonWireFormat();

    @Before
    public void setUp() {
        when(simpleBackendGroup.as(any())).thenCallRealMethod();
        when(mappedBackendGroup.as(any())).thenCallRealMethod();
        when(rangedBackendGroup.as(any())).thenCallRealMethod();
    }

    @Test
    // Both backend and wire use String to represent values
    public void testBackendValueToWire() {
        Object wireValue = formatter.backendValueToWire("StringValue");
        assertThat(wireValue).isEqualTo("StringValue");
    }

    @Test
    // Both backend and wire use String to represent values
    public void testWireToBackendValue() {
        String backendValue = formatter.wireToBackendValue("StringValue");
        assertThat(backendValue).isEqualTo("StringValue");
    }

    @Test
    // Map is transformed to JSON Object
    public void testClientEntryToWire() {
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("one", "111");
        entryMap.put("two", "22");

        Object wireEntry = formatter.backendEntryToWire(entryMap);

        assertThat(wireEntry).isInstanceOf(JSONObject.class);
        JSONObject jsonEntry = (JSONObject) wireEntry;

        assertThat(jsonEntry.get("one")).isEqualTo("111");
        assertThat(jsonEntry.get("two")).isEqualTo("22");
    }

    @Test
    // JSON Object is transformed to Map
    public void testWireToBackendEntry() {
        JSONObject jsonEntry = new JSONObject()
                .put("1", "one")
                .put("2", "zwei");

        Map<String, String> backendEntry = formatter.wireToBackendEntry(jsonEntry);

        assertThat(backendEntry.get("1")).isEqualTo("one");
        assertThat(backendEntry.get("2")).isEqualTo("zwei");
    }

    @Test
    public void testTryInternalizeValueQuery() {

        when(simpleBackendGroup.getValueQuery(anyString())).thenReturn(queryObject);

        Object wireQuery = new JSONObject()
                .put("type", "basic.value")
                .put("parameter", "param");

        Optional<Q> backendQuery = formatter.tryInternalize(simpleBackendGroup, wireQuery, context);

        assertThat(backendQuery).isPresent()
                .hasValue(queryObject);

        verify(simpleBackendGroup).getValueQuery("param");
    }

    @Test
    public void testTryInternalizeEntryQuery() {
        when(simpleBackendGroup.getEntryQuery()).thenReturn(queryObject);

        Object wireQuery = new JSONObject()
                .put("type", "basic.entry");

        Optional<Q> backendQuery = formatter.tryInternalize(simpleBackendGroup, wireQuery, context);

        assertThat(backendQuery).isPresent()
                .hasValue(queryObject);

        verify(simpleBackendGroup).getEntryQuery();
    }

    @Test
    public void testTryInternalizeMappedQuery() {
        doReturn(rangedBackendGroup).when(mappedBackendGroup).getSubGroup();
        when(mappedBackendGroup.getMappedQuery(anyString(), any())).thenReturn(queryObject);
        when(context.internalize(any(), any())).thenReturn(subQueryObject);

        JSONObject subWireQuery = new JSONObject();

        Object wireQuery = new JSONObject()
                .put("type", "basic.mapped")
                .put("key", "TestKey")
                .put("subquery", subWireQuery);

        Optional<Q> backendQuery = formatter.tryInternalize(mappedBackendGroup, wireQuery, context);

        assertThat(backendQuery).isPresent()
                .hasValue(queryObject);

        verify(context).internalize(rangedBackendGroup, subWireQuery);
        verify(mappedBackendGroup).getMappedQuery("TestKey", subQueryObject);
    }

    @Test
    public void testTryInternalizeRangedQuery() {
        doReturn(simpleBackendGroup).when(rangedBackendGroup).getSubGroup();
        when(rangedBackendGroup.getRangedQuery(anyString(), any())).thenReturn(queryObject);
        when(context.internalize(any(), any())).thenReturn(subQueryObject);

        JSONObject subWireQuery = new JSONObject();

        Object wireQuery = new JSONObject()
                .put("type", "basic.ranged")
                .put("value", "TestValue")
                .put("subquery", subWireQuery);

        Optional<Q> backendQuery = formatter.tryInternalize(rangedBackendGroup, wireQuery, context);

        assertThat(backendQuery).isPresent()
                .hasValue(queryObject);

        verify(context).internalize(simpleBackendGroup, subWireQuery);
        verify(rangedBackendGroup).getRangedQuery("TestValue", subQueryObject);
    }

    private static interface Q {

    }
}
