package co.elastic.topicsend;

import co.elastic.queuesend.ReflectiveTextMapSetter;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class TopicSendAdvice {

    public static String getDestinationName(Object input) {
        if (input == null) {
            return null;
        }

        try {
            Method getQueueMethod = input.getClass().getMethod("getTopic");
            Object destination = getQueueMethod.invoke(input);
            if (destination != null) {
                    Method getQueueNameMethod = destination.getClass().getMethod("getTopicName");
                    Object queueNameObj = getQueueNameMethod.invoke(destination);
                    if (queueNameObj instanceof String) {
                        return (String) queueNameObj;
                    }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static Scope onEnter(@Advice.This Object thisObject, @Advice.Argument(value = 0) Object input, @Advice.Local("otelSpan") Span span) {
            Tracer tracer = GlobalOpenTelemetry.getTracer("instrumentation-library-name", "semver:1.0.0");
            TextMapSetter<Object> SETTER = new ReflectiveTextMapSetter();

            Span currentSpan = Span.current();
            Scope scope = null;
            Span span2 = null;

            if (!currentSpan.getSpanContext().isValid()) {
                span2 = tracer.spanBuilder("Start JMS Application").setSpanKind(SpanKind.INTERNAL).startSpan();
                scope = span2.makeCurrent();
            }

                Context parentContext = Context.current();

                span = tracer.spanBuilder("JMS Send")
                        .setSpanKind(SpanKind.PRODUCER)
                        .setParent(parentContext)
                        .setAttribute("messaging.system", "mq")
                        .setAttribute("messaging.destination", getDestinationName(thisObject))
                        .setAttribute("messaging.destination.name", getDestinationName(thisObject))
                        .setAttribute("messaging.destination_kind", "topic")
                        .startSpan();

                Context context = parentContext.with(span);
                GlobalOpenTelemetry.get().getPropagators().getTextMapPropagator().inject(context, input, SETTER);
                if (scope != null) {
                    scope.close();
                }
                if (span2 != null) {
                    span2.end();
                }
                return span.makeCurrent();


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
