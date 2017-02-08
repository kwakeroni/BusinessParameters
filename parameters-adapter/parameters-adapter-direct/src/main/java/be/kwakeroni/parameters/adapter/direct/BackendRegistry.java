package be.kwakeroni.parameters.adapter.direct;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BackendRegistry {

    private final Map<String, DirectBackendAdapter> backendsByGroupName = new HashMap<>();
    private final Collection<BusinessParametersBackend<?>> backends = new ArrayList<>();
    private final BackendWireFormatterContext wireFormatterContext;

    public BackendRegistry(BackendWireFormatterContext wireFormatterContext) {
        this.wireFormatterContext = wireFormatterContext;
    }

    public void register(BusinessParametersBackend<?> backend) {
        if (!backends.contains(backend)) {
            backends.add(backend);
        }
        registerGroups(new DirectBackendAdapter(backend, wireFormatterContext));
    }

    private void registerGroups(DirectBackendAdapter backend) {
        registerGroups(backend, backend.getGroupNames());
    }

    private void registerGroups(DirectBackendAdapter backend, Collection<String> backendGroups) {
        clearGroupCache(backend);
        for (String group : backendGroups) {
            register(group, backend);
        }
    }

    private void clearGroupCache(DirectBackendAdapter backend) {
        Iterator<DirectBackendAdapter> iter = backendsByGroupName.values().iterator();
        while (iter.hasNext()) {
            if (backend.equals(iter.next())) {
                iter.remove();
            }
        }
    }

    private void register(String groupName, DirectBackendAdapter backend) {
        this.backendsByGroupName.compute(groupName, (key, value) -> {
            if (value == null) {
                return backend;
            } else {
                throw new IllegalStateException("Multiple backends for " + key + ": " + value + " & " + backend);
            }
        });
    }

    public DirectBackendAdapter get(String groupName) {
        DirectBackendAdapter backend = this.backendsByGroupName.get(groupName);
        if (backend == null) {
            backend = findBackendForGroup(groupName);
        }
        if (backend != null) {
            return backend;
        } else {
            String message = (this.backends.isEmpty()) ? "No backends registered" : "No api found for group " + groupName;
            throw new IllegalStateException(message);
        }
    }

    private DirectBackendAdapter findBackendForGroup(String groupName) {
        for (BusinessParametersBackend<?> backend : this.backends) {
            Collection<String> backendGroups = backend.getGroupNames();
            if (backendGroups.contains(groupName)) {
                DirectBackendAdapter adapter = new DirectBackendAdapter(backend, wireFormatterContext);
                registerGroups(adapter, backendGroups);
                return adapter;
            }
        }
        return null;
    }

}
