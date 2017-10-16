package be.kwakeroni.parameters.backend.api;

import java.util.Map;

/**
 * Created by kwakeroni on 16/10/17.
 */
public interface BackendEntry {

    public String getId();

    public Map<String, String> getParameters();
}
