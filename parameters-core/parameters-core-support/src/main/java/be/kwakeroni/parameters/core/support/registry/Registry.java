package be.kwakeroni.parameters.core.support.registry;

import java.util.Optional;

/**
 * Registers instances according to their type.
 * <p>
 * A Registry permits to register instances of a type {@code <T>} according to another of their supertypes.
 * </p>
 * <p>
 *     Example:<br />
 *     Given the following types:
 *     <ul>
 *         <li>{@code MappedObject}</li>
 *         <li>{@code Group}</li>
 *         <li>{@code MappedGroup extends MappedObject, Group}</li>
 *     </ul>
 *     A {@code Registry<Group>} will then permit to register an instance of {@code MappedGroup} as a {@code MappedObject}:
 *     <pre>registry.registerInstance(MappedObject.class, mappedGroup);</pre>
 *     Later on the object can be retrieved:
 *     <pre>Optional&lt;MappedObject&gt; group  = registry.get(MappedObject.class)</pre>
 *     The object {@code group} is known to be both a {@code MappedObject} and a {@code Group}.
 * </p>
 */
public interface Registry<T> {

    public <I extends T> void registerInstance(Class<? super I> type, I instance);

    public <I extends T> void unregisterInstance(Class<? super I> type, I instance);

    public <J> Optional<J> get(Class<J> type);

    public boolean isEmpty();

}
