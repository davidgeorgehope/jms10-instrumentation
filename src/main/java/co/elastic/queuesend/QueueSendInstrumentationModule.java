/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package co.elastic.queuesend;


import static java.util.Collections.singletonList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import java.util.List;
import net.bytebuddy.matcher.ElementMatcher;


@AutoService(InstrumentationModule.class)
public final class QueueSendInstrumentationModule extends InstrumentationModule {
    public QueueSendInstrumentationModule() {
        super("queuesend");
    }

    @Override
    public int order() {
        return 1;
    }
    @Override
    public List<String> getAdditionalHelperClassNames() {
        return List.of(QueueSendInstrumentation.class.getName(),
                QueueSendAdvice.class.getName(),
                ReflectiveTextMapSetter.class.getName(),
                "io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation");
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return AgentElementMatchers.hasClassesNamed("javax.jms.QueueSender");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new QueueSendInstrumentation());
    }
}