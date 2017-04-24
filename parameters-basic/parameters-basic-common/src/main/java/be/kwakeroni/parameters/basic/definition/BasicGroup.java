package be.kwakeroni.parameters.basic.definition;

/**
 * Created by kwakeroni on 10.04.17.
 */
public interface BasicGroup {

    public default SimpleDefinitionBuilder group(String name) {
        return DefaultSimpleDefinition.builder(name);
    }

    public default MappedDefinitionBuilder mapped() {
        return DefaultMappedDefinition.builder();
    }

    public default RangedGroupBuilder ranged() {
        return DefaultRangedDefinition.builder();
    }

    public static BasicGroup builder() {
        return new BasicGroup() {
        };
    }
}
