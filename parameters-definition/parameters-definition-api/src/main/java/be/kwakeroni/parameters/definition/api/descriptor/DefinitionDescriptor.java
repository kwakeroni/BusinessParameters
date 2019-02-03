package be.kwakeroni.parameters.definition.api.descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefinitionDescriptor {

    private static final String TYPE = "type";
    private static final String SUBGROUP = "subGroup";

    private final String type;
    private DefinitionDescriptor subGroup;
    private final Map<String, String> properties = new HashMap<>();

    private DefinitionDescriptor(String type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public static DefinitionDescriptor withType(String type) {
        return new DefinitionDescriptor(type);
    }

    public DefinitionDescriptor withSubGroup(DefinitionDescriptor subGroup) {
        this.subGroup = subGroup;
        return this;
    }

    public DefinitionDescriptor withProperty(String property, String value) {
        this.properties.put(property, value);
        return this;
    }

    public Map<String, Object> toBasicMap() {
        Map<String, Object> map = new HashMap<>(this.properties);
        map.put(TYPE, type);
        if (subGroup != null) {
            map.put(SUBGROUP, subGroup.toBasicMap());
        }
        return map;
    }
}
