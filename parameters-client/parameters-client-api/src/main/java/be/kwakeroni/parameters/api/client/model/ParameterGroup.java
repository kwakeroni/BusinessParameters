package be.kwakeroni.parameters.api.client.model;

/**
 * Represents a Group of Business Parameters.
 * <p>
 *     A group consists of related parameters.
 *     An entry consists of a value for each of these parameters.
 *     The way to identify one specific entry from along the complete set of entries of a group
 *     depends on the <em>type</em> of entry. This type is fixed for a Group.
 * </p>
 * @param <ET> The entry type of the group
 */
public interface ParameterGroup<ET extends EntryType> {

    public String getName();

}
