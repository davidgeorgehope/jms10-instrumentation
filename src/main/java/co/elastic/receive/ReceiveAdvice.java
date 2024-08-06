package co.elastic.receive;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReceiveAdvice {

    public static String getDestinationName(Object input) {
        if (input == null) {
            return null;
        }

        try {
            Method getJMSDestinationMethod = input.getClass().getMethod("getJMSDestination");
            Object destination = getJMSDestinationMethod.invoke(input);

            if (destination != null) {
                try {
                    Method getQueueNameMethod = destination.getClass().getMethod("getQueueName");
                    Object queueNameObj = getQueueNameMethod.invoke(destination);
                    if (queueNameObj instanceof String) {
                        return (String) queueNameObj;
                    }
                } catch (NoSuchMethodException e) {
                    try {
                        Method getTopicNameMethod = destination.getClass().getMethod("getTopicName");
                        Object topicNameObj = getTopicNameMethod.invoke(destination);
                        if (topicNameObj instanceof String) {
                            return (String) topicNameObj;
                        }
                    } catch (NoSuchMethodException ex) {
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // Handle exceptions
            e.printStackTrace();
        }

        return null;
    }

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static Scope onEnter(@Advice.Argument(value = 0) Object input, @Advice.Local("otelSpan") Span span) {
            Tracer tracer = GlobalOpenTelemetry.getTracer("instrumentation-library-name", "semver:1.0.0");
            TextMapGetter<Object> GETTER = new ReflectiveTextMapGetter();

            io.opentelemetry.context.Context extractedContext = GlobalOpenTelemetry.get().getPropagators().getTextMapPropagator()
                    .extract(io.opentelemetry.context.Context.current(), input, GETTER);

            span = tracer.spanBuilder("JMS Receive")
                    .setSpanKind(SpanKind.CONSUMER)
                    .setParent(extractedContext)
                    .setAttribute("messaging.system", "mq")
            .setAttribute("messaging.destination",  getDestinationName(input))
            .setAttribute("messaging.destination.name",  getDestinationName(input))
            .setAttribute("messaging.destination_kind", "queue")
                    .startSpan();

            Scope scope = span.makeCurrent();

            return scope;
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void onExit(
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelSpan") Span span,
                @Advice.Enter Scope scope) {

            scope.close();

            if (throwable != null) {
                span.setStatus(StatusCode.ERROR, "Exception thrown in method");
            }

            span.end();
        }

}
