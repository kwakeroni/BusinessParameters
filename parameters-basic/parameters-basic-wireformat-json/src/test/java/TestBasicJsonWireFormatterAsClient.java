import be.kwakeroni.parameters.basic.client.query.BasicClientWireFormatter;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.wireformat.json.BasicJsonWireFormat;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.types.api.ParameterType;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by kwakeroni on 28/09/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestBasicJsonWireFormatterAsClient {


    @Mock
    private TestValue value1;
    @Mock
    private Entry entry;
    @Mock
    private ParameterType<TestValue> parameterType;
    @Mock
    private Parameter<TestValue> parameter;
    @Mock
    private Parameter<TestValue> parameter2;
    @Mock
    private ValueQuery<TestValue> valueQuery;
    @Mock
    private EntryQuery entryQuery;
    @Mock
    private MappedQuery<TestValue, ?, ?, TestValue> mappedQuery;
    @Mock
    private RangedQuery<TestValue, ?, ?, TestValue> rangedQuery;
    @Mock
    private ClientWireFormatterContext context;

    BasicClientWireFormatter formatter = new BasicJsonWireFormat();

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(value1, parameter, valueQuery, entryQuery, context);
    }

    @Before
    public void setUp() {
        when(valueQuery.getParameter()).thenReturn(parameter);
    }

    @Test
    // Parameter is used to transform value to String
    public void testClientValueToWire() {
        when(parameter.toString(any())).thenReturn("ValueTest");

        Object wireValue = formatter.clientValueToWire(value1, valueQuery, context);

        assertThat(wireValue).isEqualTo("ValueTest");
        verify(valueQuery).getParameter();
        verify(parameter).toString(value1);
        verifyNoMoreInteractions();
    }

    @Test
    // Parameter is used to transform String to value
    public void testWireToClientValue() {
        when(parameter.fromString(anyString())).thenReturn(value1);

        Object clientValue = formatter.wireToClientValue("WireValue", valueQuery, context);

        assertThat(clientValue).isEqualTo(value1);
        verify(valueQuery).getParameter();
        verify(parameter).fromString("WireValue");
        verifyNoMoreInteractions();
    }

    @Test
    // Entry is transformed to JSON Object
    public void testClientEntryToWire() {
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("one", "111");
        entryMap.put("two", "22");
        when(entry.toMap()).thenReturn(entryMap);

        Object wireEntry = formatter.clientEntryToWire(entry, entryQuery, context);

        assertThat(wireEntry).isInstanceOf(String.class);
        JSONObject jsonEntry = new JSONObject((String) wireEntry);

        assertThat(jsonEntry.get("one")).isEqualTo("111");
        assertThat(jsonEntry.get("two")).isEqualTo("22");
    }

    @Test
    // JSON Object is transformed to Entry
    public void testWireToClientEntry() {
        when(parameter.getName()).thenReturn("1");
        when(parameter.fromString(anyString())).then(answer((String string) -> (TestValue) (() -> string)));
        when(parameter2.getName()).thenReturn("2");
        when(parameter2.fromString(anyString())).then(answer((String string) -> (TestValue) (() -> string)));

        JSONObject jsonEntry = new JSONObject()
                .put("1", "one")
                .put("2", "zwei");

        Entry clientEntry = formatter.wireToClientEntry(jsonEntry.toString(), entryQuery, context);

        assertThat(clientEntry.getValue(parameter).get()).isEqualTo("one");
        assertThat(clientEntry.getValue(parameter2).get()).isEqualTo("zwei");
    }

    @Test
    public void testExternalizeValueQuery() {
        when(parameter.getName()).thenReturn("param");

        Object wireQuery = formatter.externalizeValueQuery(valueQuery, context);

        assertEqualJSON(new JSONObject()
                .put("type", "basic.value")
                .put("parameter", "param"), wireQuery);
    }

    @Test
    public void testExternalizeEntryQuery() {
        Object wireQuery = formatter.externalizeEntryQuery(entryQuery, context);

        assertEqualJSON(new JSONObject()
                .put("type", "basic.entry"), wireQuery);
    }

    @Test
    public void testExternalizeMappedQuery() {
        JSONObject jsonSubQuery = new JSONObject();
        when(valueQuery.externalize(any())).thenReturn(jsonSubQuery);
        when(mappedQuery.getKeyString()).thenReturn("MapKey");
        doReturn(valueQuery).when(mappedQuery).getSubQuery();


        Object wireQuery = formatter.externalizeMappedQuery(mappedQuery, context);

        assertEqualJSON(new JSONObject()
                .put("type", "basic.mapped")
                .put("key", "MapKey")
                .put("subquery", jsonSubQuery), wireQuery);
    }

    @Test
    public void testExternalizeRangedQuery() {
        JSONObject jsonSubQuery = new JSONObject();
        when(valueQuery.externalize(any())).thenReturn(jsonSubQuery);
        when(rangedQuery.getValueString()).thenReturn("RangeKey");
        doReturn(valueQuery).when(rangedQuery).getSubQuery();


        Object wireQuery = formatter.externalizeRangedQuery(rangedQuery, context);

        assertEqualJSON(new JSONObject()
                .put("type", "basic.ranged")
                .put("value", "RangeKey")
                .put("subquery", jsonSubQuery), wireQuery);

    }

    private void assertEqualJSON(JSONObject expected, Object actual) {

        assertThat(actual).isInstanceOf(JSONObject.class);
        JSONObject jsonQuery = (JSONObject) actual;

        for (String key : expected.keySet()) {
            assertThat(jsonQuery.get(key)).isEqualTo(expected.get(key));
        }
        assertThat(jsonQuery.length()).isEqualTo(expected.length());

    }


    private static interface TestValue {
        String get();
    }
}
