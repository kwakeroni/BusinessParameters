package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.definition.builder.MappedDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.builder.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.builder.SimpleDefinitionBuilder;

/**
 * Created by kwakeroni on 10.04.17.
 */
public interface BasicGroup {

    public default SimpleDefinitionBuilder simple() {
        return new DefaultSimpleDefinitionBuilder();
    }

    public default MappedDefinitionBuilder<?, ?> mapped() {
        return new DefaultMappedDefinitionBuilder<>();
    }

    public default RangedDefinitionBuilder<?, ?> ranged() {
        return new DefaultRangedDefinitionBuilder<>();
    }

    public static BasicGroup builder() {
        return new BasicGroup() {
        };
    }

    public static MappedDefinitionBuilder<?, ?> mappedGroup() {
        return builder().mapped();
    }

    public static RangedDefinitionBuilder<?, ?> rangedGroup() {
        return builder().ranged();
    }

    public static SimpleDefinitionBuilder group() {
        return builder().simple();
    }
}
