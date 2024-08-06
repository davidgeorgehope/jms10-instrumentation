package co.elastic.queuesend;

import io.opentelemetry.context.propagation.TextMapSetter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ReflectiveTextMapSetter implements TextMapSetter<Object> {
    @Override
    public void set(Object carrier, String key, String value) {
        try {
            Method setStringPropertyMethod = carrier.getClass().getMethod("setStringProperty", String.class, String.class);
            setStringPropertyMethod.invoke(carrier, key, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to set property using reflection", e);
        }
    }
}
