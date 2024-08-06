package co.elastic.queuesend;

/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

// Import necessary libraries.
// ElementMatchers helps to define custom matchers for Byte Buddy's ElementMatcher interface.
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;

// OpenTelemetry libraries for tracing capabilities

// Libraries for Java agent instrumentation
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;

// Byte Buddy libraries for method intercepting
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

// Java logging library
import java.util.logging.Logger;

public class QueueSendInstrumentation implements TypeInstrumentation {
    private static Logger logger = Logger.getLogger(QueueSendInstrumentation.class.getName());


    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("javax.jms.MessageProducer"));
    }

    @Override
    public void transform(TypeTransformer typeTransformer) {
        typeTransformer.applyAdviceToMethod(namedOneOf("send").and(takesArgument(0, hasSuperType(named("javax.jms.Message")))), QueueSendAdvice.class.getName());
    }

}
