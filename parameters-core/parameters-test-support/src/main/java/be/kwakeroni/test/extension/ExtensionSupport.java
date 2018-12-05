package be.kwakeroni.test.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExtensionSupport {

    protected Optional<Object> getParentTestInstance(ExtensionContext context) {
        return context.getTestInstance()
                .flatMap(this::getEncapsulatingInstance);
    }

    private Optional<Object> getEncapsulatingInstance(Object instance) {
        Class<?> encapsClass = instance.getClass().getEnclosingClass();
        if (encapsClass == null) {
            return Optional.empty();
        }
        List<Field> fields = classFields(instance.getClass())
                .filter(field -> field.getType() == encapsClass)
                .collect(Collectors.toList());

        if (fields.size() == 1) {
            return Optional.of(getField(fields.get(0), instance));
        }
        if (fields.size() > 1) {
            System.out.println("[WARN] Encountered multiple fields of encapsulating class: " + fields);
        }

        return Optional.empty();
    }

    protected Stream<InstanceField> getFields(Object testInstance) {
        return recursiveStream(testInstance, this::getEncapsulatingInstance)
                .flatMap(this::instanceFields);
    }

    private Stream<InstanceField> instanceFields(Object testInstance) {
        return recursiveStream(testInstance.getClass(), this::getSuperClassBelowObject)
                .flatMap(this::classFields)
                .map(field -> new InstanceField(field, testInstance));
    }

    private Stream<Field> classFields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields());
    }

    private Optional<Class<?>> getSuperClassBelowObject(Class<?> clazz) {
        return Optional.ofNullable(clazz.getSuperclass())
                .filter((Object o) -> o != Object.class)
                .map(Function.identity());

    }


    private <T> Stream<T> recursiveStream(T seed, Function<T, Optional<T>> function) {
        return recursiveStream(seed, Stream::of, function);
    }


    private <S, T> Stream<T> recursiveStream(S seed, Function<S, Stream<T>> streamSupplier, Function<S, Optional<S>> function) {
        return Stream.of(seed)
                .flatMap(s -> concatWithParents(s, streamSupplier, function));
    }

    private <S, T> Stream<T> concatWithParents(S seed, Function<S, Stream<T>> streamSupplier, Function<S, Optional<S>> function) {
        return Stream.concat(
                streamSupplier.apply(seed),
                function.apply(seed)
                        .map(parent -> recursiveStream(parent, streamSupplier, function))
                        .orElseGet(Stream::empty));
    }

    protected static void setField(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private static Object getField(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected static Predicate<AnnotatedElement> annotatedBy(Class<? extends Annotation> annotationType) {
        return element -> element.isAnnotationPresent(annotationType);
    }

    protected static Consumer<Field> inject(Object instance, Function<Class<?>, Object> value) {
        return field -> setField(field, instance, value.apply(field.getType()));
    }

    protected static Consumer<Field> accept(Object instance, Consumer<Object> valueConsumer) {
        return field -> valueConsumer.accept(getField(field, instance));
    }


    static class InstanceField implements AnnotatedElement {
        final Field field;
        final Object instance;

        private InstanceField(Field field, Object instance) {
            this.field = field;
            this.instance = instance;
        }

        void set(Object value) {
            setField(field, instance, value);
        }

        Object get() {
            return getField(field, instance);
        }

        Class<?> getType() {
            return field.getType();
        }

        String getName() {
            return field.getName();
        }


        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
            return field.isAnnotationPresent(annotationClass);
        }

        @Override
        public Annotation[] getAnnotations() {
            return field.getAnnotations();
        }

        @Override
        public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
            return field.getDeclaredAnnotation(annotationClass);
        }

        @Override
        public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
            return field.getDeclaredAnnotationsByType(annotationClass);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return field.getAnnotation(annotationClass);
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return field.getDeclaredAnnotations();
        }

        @Override
        public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
            return field.getAnnotationsByType(annotationClass);
        }
    }
}
