package be.kwakeroni.test.assertion;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractInputStreamAssert;
import org.assertj.core.api.Assertions;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RestAssert extends AbstractAssert<RestAssert, RestAssert.RestCall> {

    private static final Client CLIENT = new Client();

    private ClientResponse clientResponse = null;

    public static RestAssert assertThat(RestCall restCall) {
        return new RestAssert(restCall);
    }

    public static RestCall get(String uri) {
        return new Call(uri, WebResource::get);
    }

    public static RestCall options(String uri) {
        return new Call(uri, WebResource::options);
    }

    public static RestCall head(String uri) {
        return new Call(uri, (resource, $class) -> resource.head());
    }

    private RestAssert(RestCall call) {
        super(call, RestAssert.class);
    }

    public ClientResponse getResponse() {
        if (this.clientResponse == null) {
            this.clientResponse = actual.call();
        }
        return this.clientResponse;
    }

    public String getResponseText() {
        return getResponse().getEntity(String.class);
    }

    public String getResponseHeader(String name) {
        List<String> values = getResponse().getHeaders().get(name);
        if (values.size() > 1) {
            throw new IllegalStateException("Multiple values for header " + name + ": " + values);
        }
        return values.get(0);
    }

    private void failWithMessage(String pattern, ClientResponse response) {
        failWithMessage(pattern,
                actual.getUri(),
                response.getStatus(),
                response.getEntity(String.class)
        );
    }

    public RestAssert isSuccess() {
        ClientResponse response = getResponse();
        if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            failWithMessage("Expected call to %s to be successful, but error code was returned: %s, with response content: %n%s", response);
        }
        return this;
    }

    public RestAssert redirectsTo(String uri) {
        ClientResponse response = getResponse();
        if (!Response.Status.Family.REDIRECTION.equals(response.getStatusInfo().getFamily())) {
            failWithMessage("Expected call to %s to redirect, but unexpected status was returned: %s, with response content: %n%s", response);
        }
        String redirectUrl = getResponseHeader(HttpHeaders.LOCATION);
        if (!redirectUrl.equals(uri)) {
            failWithMessage("Expected call to %s to redirect to %s but response redirects to %s", actual.getUri(), uri, redirectUrl);
        }
        return this;
    }

    public RestAssert isNotFound() {
        ClientResponse response = getResponse();
        if (response.getStatus() != 404) {
            failWithMessage("Expected call to %s to be 'not found', but unexpected status was returned: %s, with response content: %n%s", response);
        }
        return this;
    }

    public RestAssert hasStatus(Response.Status status) {
        return hasStatus(status.getStatusCode());
    }

    public RestAssert hasStatus(int status) {
        ClientResponse response = getResponse();
        if (response.getStatus() != status) {
            failWithMessage("Expected call to %s to have status %s, but unexpected status was returned: %s, with response content: %n%s", actual.getUri(), status, response.getStatus(), response.getEntity(String.class));
        }
        return this;
    }

    public RestAssert failsToConnect() {
        try {
            ClientResponse response = getResponse();
            failWithMessage("Expected connection to %s to fail, but response was returned with status: %s and content: %n%s", response);
        } catch (ClientHandlerException exc) {
            if (!(exc.getCause() instanceof ConnectException)) {
                failWithMessage("Expected connection to %s to fail, but unexpected exception was thrown with cause: %s", actual.getUri(), exc.getCause());
            }
        } catch (Exception exc) {
            failWithMessage("Expected connection to %s to fail, but unexpected exception was thrown: %s", actual.getUri(), exc);
        }
        return this;
    }

    public RestAssert hasContentType(MediaType type) {
        return hasContentType(type.getType());
    }

    public RestAssert hasContentType(String expectedType) {
        String actualType = getResponseHeader(HttpHeaders.CONTENT_TYPE);
        if (!expectedType.equals(actualType)) {
            failWithMessage("Expected connection to %s to have content-type %s, but actual type was %s", actual.getUri(), expectedType, actualType);
        }
        return this;
    }

    public AbstractCharSequenceAssert<?, String> andText() {
        return Assertions.assertThat(getResponseText());
    }

    public AbstractInputStreamAssert<?, ?> andResponse() {
        InputStream stream = getResponse().getEntityInputStream();
        return Assertions.assertThat(stream);
    }

    public RestAssert withResponse(Consumer<ClientResponse> action) {
        action.accept(getResponse());
        return this;
    }

    public RestAssert withText(Consumer<String> action) {
        action.accept(getResponseText());
        return this;
    }

    public RestAssert withHeaders(BiConsumer<String, String> action) {
        getResponse().getHeaders().forEach((key, values) -> action.accept(key, String.valueOf(values)));
        return this;
    }

    public interface RestCall {
        public String getUri();

        public ClientResponse call();
    }

    private static class Call implements RestCall {
        private final WebResource resource;
        private final BiFunction<WebResource, ? super Class<ClientResponse>, ClientResponse> caller;


        public Call(String uri, BiFunction<WebResource, ? super Class<ClientResponse>, ClientResponse> caller) {
            this.caller = caller;
            this.resource = CLIENT.resource(uri);
            this.resource.setProperty(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        }

        @Override
        public String getUri() {
            return resource.getURI().toString();
        }

        @Override
        public ClientResponse call() {
            return caller.apply(this.resource, ClientResponse.class);
        }
    }
}
