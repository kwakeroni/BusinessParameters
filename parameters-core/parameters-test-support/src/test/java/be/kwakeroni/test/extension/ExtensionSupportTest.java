package be.kwakeroni.test.extension;

import be.kwakeroni.test.extension.ExtensionSupport.InstanceField;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.InjectMocks;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(ExtensionSupportTest.TestExtension.class)
class ExtensionSupportTest {

    Object oneField = Integer.valueOf("18");
    @InjectMocks
    String secondField = "FIELD";

    @Test
    void extensionInjected(TestExtension extension) {
        assertThat(extension).isNotNull();
    }

    @Test
    void getsNoParentTestInstance(TestExtension extension, ExtensionContext context){
        assertThat(extension.getParentTestInstance(context)).isNotPresent();
    }

    @Test
    void getsFields(TestExtension extension) {
        assertThat(
                extension.getFields(this)
                    .map(ExtensionSupportTest::toString)
        ).contains("ExtensionSupportTest:oneField", "ExtensionSupportTest:secondField");
    }


    @Test
    void checksAnnotatedBy(TestExtension extension) {
        assertThat(extension.getFields(this)
                .filter(ExtensionSupport.annotatedBy(InjectMocks.class))
                .map(ExtensionSupportTest::toString))
                .containsExactly("ExtensionSupportTest:secondField");
    }

    @Test
    void injectsIntoFields(TestExtension extension) {
        Object expected = new Object();

        assertThat(oneField).isNotSameAs(expected);

        extension.getFields(this)
                    .filter(field -> "oneField".equals(field.getName()))
                    .forEach(field -> field.set(expected));

        assertThat(oneField).isSameAs(expected);
    }

    @Test
    void consumesFieldValues(TestExtension extension) {
        Object expected = this.oneField;

        Optional<Object> actual = extension.getFields(this)
                .filter(field -> "oneField".equals(field.getName()))
                .findAny()
                .map(InstanceField::get);

        assertThat(actual).hasValue(expected);
    }

    @Nested
    class NestedTest {
        private Object nestedField = "NESTED";

        @Test
        void getsParentTestInstance(TestExtension extension, ExtensionContext context){
            assertThat(extension.getParentTestInstance(context)).hasValue(ExtensionSupportTest.this);
        }

        @Test
        void getsAllFields(TestExtension extension) {
            assertThat(
                    extension.getFields(this)
                            .filter(field -> ! field.field.isSynthetic())
                            .map(ExtensionSupportTest::toString)
            ).containsExactlyInAnyOrder("ExtensionSupportTest:oneField", "ExtensionSupportTest:secondField", "NestedTest:nestedField");
        }
    }


    @Nested
    class NestedTestWithInheritance extends NestedTest {
        private Object nestedFieldInSubclass = "NESTED";

        @Test
        void getsParentTestInstance(TestExtension extension, ExtensionContext context){
            assertThat(extension.getParentTestInstance(context)).hasValue(ExtensionSupportTest.this);
        }

        @Test
        void getsAllFields(TestExtension extension) {
            assertThat(
                    extension.getFields(this)
                            .filter(field -> ! field.field.isSynthetic())
                            .map(ExtensionSupportTest::toString)
            ).containsExactlyInAnyOrder("ExtensionSupportTest:oneField", "ExtensionSupportTest:secondField", "NestedTestWithInheritance:nestedField", "NestedTestWithInheritance:nestedFieldInSubclass");
        }
    }

    @Nested
    class NestedTestWithAmbiguousParent {
        private Object nestedField = "NESTED";
        private ExtensionSupportTest ambiguousParent = ExtensionSupportTest.this;

        @Test
        void refusesAmbiguousParentTestInstance(TestExtension extension, ExtensionContext context){
            assertThat(extension.getParentTestInstance(context)).isEmpty();
        }

    }

    private static String toString(InstanceField field){
        return field.instance.getClass().getSimpleName()+":"+field.getName();
    }

    public static class TestExtension extends ExtensionSupport implements ParameterResolver {
        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return Arrays.asList(TestExtension.class, ExtensionContext.class).contains(
                    parameterContext.getParameter().getType());
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            Class<?> type = parameterContext.getParameter().getType();
            if (type == TestExtension.class) {
                return this;
            } else if (type == ExtensionContext.class) {
                return extensionContext;
            } else {
                throw new IllegalArgumentException("Cannot inject parameter " + parameterContext.getParameter());
            }
        }
    }

}