package co.elastic.receive;


import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.logging.Logger;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ReceiveInstrumentation implements TypeInstrumentation {


    private static Logger logger = Logger.getLogger(ReceiveInstrumentation.class.getName());


    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("javax.jms.MessageListener"));
    }

    @Override
    public void transform(TypeTransformer typeTransformer) {
        logger.info("TEST transform");
        typeTransformer.applyAdviceToMethod(namedOneOf("onMessage"),ReceiveAdvice.class.getName());
    }

}
