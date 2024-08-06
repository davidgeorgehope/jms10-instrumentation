/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package co.elastic.topicsend;


import co.elastic.queuesend.ReflectiveTextMapSetter;
import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static java.util.Collections.singletonList;


@AutoService(InstrumentationModule.class)
public final class TopicSendInstrumentationModule extends InstrumentationModule {
    public TopicSendInstrumentationModule() {
        super("jms10topicsend");
    }

    @Override
    public int order() {
        return 1;
    }
    @Override
    public List<String> getAdditionalHelperClassNames() {
        return List.of(TopicSendInstrumentation.class.getName(),
                TopicSendAdvice.class.getName(),
                ReflectiveTextMapSetter.class.getName(),
                "io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation");
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return AgentElementMatchers.hasClassesNamed("javax.jms.TopicPublisher");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new TopicSendInstrumentation());
    }
}