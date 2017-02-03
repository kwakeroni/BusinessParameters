package be.kwakeroni.parameters.adapter.direct;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;

import java.util.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BackendRegistry {

    private Map<String, BusinessParametersBackend> backendsByGroupName = new HashMap<>();
    private Collection<BusinessParametersBackend> backends = new ArrayList<>();

    public void register(BusinessParametersBackend backend) {
        if (!backends.contains(backend)) {
            backends.add(backend);
        }
        registerGroups(backend);
    }

    private void registerGroups(BusinessParametersBackend backend) {
        registerGroups(backend, backend.getGroupNames());
    }

    private void registerGroups(BusinessParametersBackend backend, Collection<String> backendGroups) {
        clearGroupCache(backend);
        for (String group : backendGroups) {
            register(group, backend);
        }
    }

    private void clearGroupCache(BusinessParametersBackend backend) {
        Iterator<BusinessParametersBackend> iter = backendsByGroupName.values().iterator();
        while (iter.hasNext()) {
            if (backend.equals(iter.next())) {
                iter.remove();
            }
        }
    }

    private void register(String groupName, BusinessParametersBackend backend) {
        this.backendsByGroupName.compute(groupName, (key, value) -> {
            if (value == null) {
                return backend;
            } else {
                throw new IllegalStateException("Multiple backends for " + key + ": " + value + " & " + backend);
            }
        });
    }

    public BusinessParametersBackend get(String groupName) {
        BusinessParametersBackend backend = this.backendsByGroupName.get(groupName);
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

    private BusinessParametersBackend findBackendForGroup(String groupName) {
        for (BusinessParametersBackend backend : this.backends) {
            Collection<String> backendGroups = backend.getGroupNames();
            if (backendGroups.contains(groupName)) {
                registerGroups(backend, backendGroups);
                return backend;
            }
        }
        return null;
    }

}
