package be.kwakeroni.parameters.management.rest.factory;

import be.kwakeroni.parameters.core.support.registry.DefaultRegistry;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptorBuilder;

class DefinitionDescriptorBuilderContext extends DefaultRegistry<DefinitionDescriptorBuilder> implements DefinitionVisitorContext<DefinitionDescriptor> {

    @Override
    public <V extends DefinitionVisitor<DefinitionDescriptor>> V getVisitor(Class<V> type) {
        return this.get(type).orElseThrow(() -> new IllegalStateException("No descriptor builder registered for " + type));
    }

}
