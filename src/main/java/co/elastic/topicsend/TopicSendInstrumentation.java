package co.elastic.topicsend;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.logging.Logger;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class TopicSendInstrumentation implements TypeInstrumentation {
    private static Logger logger = Logger.getLogger(TopicSendInstrumentation.class.getName());


    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("javax.jms.MessageProducer"));
    }

    @Override
    public void transform(TypeTransformer typeTransformer) {
        typeTransformer.applyAdviceToMethod(namedOneOf("publish").and(takesArgument(0, hasSuperType(named("javax.jms.Message")))), TopicSendAdvice.class.getName());
    }

}
