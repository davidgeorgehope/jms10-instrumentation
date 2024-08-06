package co.elastic.receive;

import io.opentelemetry.context.propagation.TextMapGetter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectiveTextMapGetter implements TextMapGetter<Object> {
    @Override
    public String get(Object carrier, String key) {
        try {
            Method getStringPropertyMethod = carrier.getClass().getMethod("getStringProperty", String.class);
            Object result = getStringPropertyMethod.invoke(carrier, key);
            return result != null ? result.toString() : null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to get property using reflection", e);
        }
    }

    @Override
    public Iterable<String> keys(Object carrier) {
        return null;
    }
}
