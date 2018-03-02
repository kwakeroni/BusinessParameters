package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.evelyn.client.DefaultClientTable;
import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Creates Client Tables")
@ExtendWith(MockitoExtension.class)
class GroupTableFactoryTest {

    @Mock
    private InMemoryGroup group;
    @Mock
    private DatabaseAccessor accessor;
    @Mock
    private Function<String, DatabaseAccessor> accessorSupplier;
    private GroupTableFactory factory;

    @BeforeEach
    public void setupFactory() {
        when(accessorSupplier.apply(any())).thenReturn(accessor);
        this.factory = new GroupTableFactory(accessorSupplier);
    }

    @Test
    @DisplayName("Of the expected implementation type")
    public void testType() {
        ClientTable<EntryData> table = factory.createTable(group);
        assertThat(table).isNotNull()
                .isExactlyInstanceOf(DefaultClientTable.class);
    }

    @Test
    @DisplayName("Representing a group")
    public void test() {
        when(group.getName()).thenReturn("my.group");
        when(accessor.getDatabaseName()).thenReturn("my.group.by-accessor");

        ClientTable<EntryData> table = factory.createTable(group);

        verify(accessorSupplier).apply("my.group");
        assertThat(table.getName()).isEqualTo("my.group.by-accessor");
    }


}