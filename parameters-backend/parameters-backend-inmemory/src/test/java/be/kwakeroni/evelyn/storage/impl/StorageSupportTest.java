package be.kwakeroni.evelyn.storage.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.InOrder;
import org.mockito.Spy;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class StorageSupportTest {

    private static final String VERSION = "2.c";
    private static final String[] DATA = new String[]{
            "!evelyn-db",
            "!version=" + VERSION,
            "!name=myDb",
            "!data",
            "ABC",
            "DEF"
    };

    @Spy
    private TestStorage storage = new TestStorage();

    @Test
    @DisplayName("Supplies a data stream")
    public void testRead() {
        assertThat(storage.read(Charset.defaultCharset())).containsExactly(DATA);
    }

    @Test
    @DisplayName("Reads the version of the data stream")
    public void testReadVersion() throws Exception {
        assertThat(storage.readVersion()).isEqualTo(VERSION);
    }

    @Test
    @DisplayName("Writes the header to the data stream")
    public void testWriteHeader() throws Exception {
        storage.writeHeader("k.3");

        assertThat(storage.appendedContent).containsExactly(
                "!evelyn-db",
                "!version=k.3"
        );
    }

    @Test
    @DisplayName("Initializes the storage before writing the header")
    public void testInitialize() throws Exception {
        storage.writeHeader("k.3");

        assertThat(storage.initialized).isTrue();

        InOrder inOrder = inOrder(storage);
        inOrder.verify(storage).initialize();
        inOrder.verify(storage, atLeastOnce()).append(anyString());
    }

    private static class TestStorage extends StorageSupport {

        private boolean initialized = false;
        private final List<String> appendedContent = new ArrayList<>();

        @Override
        public String getReference() {
            return "test";
        }

        @Override
        public Stream<String> read(Charset charset) {
            return Arrays.stream(DATA);
        }

        @Override
        public void append(String data) {
            appendedContent.add(data);
        }

        @Override
        protected void initialize() {
            this.initialized = true;
        }
    }
}
