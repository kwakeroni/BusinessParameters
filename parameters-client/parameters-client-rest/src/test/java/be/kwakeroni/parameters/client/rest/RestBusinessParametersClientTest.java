package be.kwakeroni.parameters.client.rest;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestBusinessParametersClientTest {

    static Runnable resetWireMock;
    static WireMockServer wireMock = new WireMockServer(9999) {
        {
            resetWireMock = () -> this.client.resetMappings();
        }
    };


    RestBusinessParametersClient client;
    @Mock
    ClientWireFormatterContext context;
    @Mock
    ParameterGroup<TestEntry> group;
    @Mock
    Query<TestEntry, TestResult> query;

    @BeforeAll
    static void setupClass() {
        wireMock.start();
        WireMock.configureFor("localhost", wireMock.port());
    }

    @BeforeEach
    void setUp() {
        this.client = new RestBusinessParametersClient(wireMock.baseUrl(), context);
    }

    @AfterEach
    void tearDown() {
        resetWireMock.run();
    }

    @AfterAll
    static void tearDownClass() {
        wireMock.stop();
    }


    @Nested
    @DisplayName("Sends queries")
    class GetTest {

        @Test
        @DisplayName("to a REST endpoint")
        void testSendQuery() {
            stubFor(post(urlMatching(".*")).willReturn(ok()));

            when(group.getName()).thenReturn("myGroup");
            when(query.externalize(context)).thenReturn("MY_QUERY");

            Optional<TestResult> result = client.get(group, query);

            assertThat(result).isNotNull();

            WireMock.verify(postRequestedFor(urlMatching("/myGroup/query"))
                    .withRequestBody(equalTo("MY_QUERY")));
        }

        @Test
        @DisplayName("and receives a response")
        void get() {
            TestResult expected = new TestResult();

            stubFor(post(urlMatching(".*")).willReturn(ok("MY_RESPONSE")));

            when(group.getName()).thenReturn("myGroup");
            when(query.externalize(context)).thenReturn("MY_QUERY");
            when(query.internalizeResult(any(), any())).thenReturn(Optional.of(expected));

            Optional<TestResult> result = client.get(group, query);
            assertThat(result).hasValue(expected);

            verify(query).internalizeResult(eq("MY_RESPONSE"), eq(context));
        }


        @Test
        @DisplayName("throwing Exceptions in case of errors")
        void testGetException(@Mock Entry entry) {

            TestResult expected = new TestResult();

            stubFor(post(urlMatching(".*")).willReturn(serverError()));

            when(group.getName()).thenReturn("myGroup");
            when(query.externalize(context)).thenReturn("MY_QUERY");
            when(query.internalizeResult(any(), any())).thenReturn(Optional.of(expected));


            assertThatThrownBy(() -> client.get(group, query))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @Nested
    @DisplayName("Sends new values")
    class SetTest {
        @Test
        @DisplayName("to a REST endpoint")
        void testSendQueryWithValue() {

            TestResult newValue = new TestResult();

            stubFor(post(urlMatching(".*")).willReturn(ok()));

            when(group.getName()).thenReturn("myGroup");
            when(query.externalize(context)).thenReturn("MY_QUERY");
            when(query.externalizeValue(newValue, context)).thenReturn("MY_VALUE");

            client.set(group, query, newValue);

//            WireMock.verify(patchRequestedFor(urlMatching("/myGroup"))
            WireMock.verify(postRequestedFor(urlMatching("/myGroup/update"))
                    .withRequestBody(equalToJson("{\"query\": \"MY_QUERY\", \"value\": \"MY_VALUE\"}")));

        }

        @Test
        @DisplayName("throwing Exceptions in case of errors")
        void testSetException(@Mock Entry entry) {

            TestResult newValue = new TestResult();

            stubFor(post(urlMatching(".*")).willReturn(serverError()));

            when(group.getName()).thenReturn("myGroup");
            when(query.externalize(context)).thenReturn("MY_QUERY");
            when(query.externalizeValue(newValue, context)).thenReturn("MY_VALUE");

            assertThatThrownBy(() -> client.set(group, query, newValue))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @Nested
    @DisplayName("Sends new entries")
    class AddEntryTest {
        @Test
        @DisplayName("to a REST endpoint")
        void testSendAddEntry(@Mock Entry entry) {

            stubFor(post(urlMatching(".*")).willReturn(ok()));

            Map<String, String> valueMap = new HashMap<>();
            valueMap.put("one", "1");
            valueMap.put("two", "22");
            when(entry.toMap()).thenReturn(valueMap);
            when(group.getName()).thenReturn("myGroup");

            client.addEntry(group, entry);

            WireMock.verify(postRequestedFor(urlMatching("/myGroup"))
                    .withRequestBody(equalToJson("{\"one\": \"1\", \"two\": \"22\"}")));

        }

        @Test
        @DisplayName("throwing Exceptions in case of errors")
        void testAddEntryException(@Mock Entry entry) {
            stubFor(post(urlMatching(".*")).willReturn(badRequest()));

            when(group.getName()).thenReturn("myGroup");

            assertThatThrownBy(() -> client.addEntry(group, entry))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private static class TestEntry implements EntryType {

    }

    private static class TestResult {

    }
}