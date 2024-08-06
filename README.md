# JMS v1.0 OpenTelemetry Instrumentation for Older Apps

- built with the JMS 1.0 API Spec: https://docs.oracle.com/cd/E17802_01/products/products/jms/javadoc-102a/index.html
- build wih maven: mvn clean install
- run with example: -javaagent:/Users/davidhope/Downloads/opentelemetry-javaagent.jar
-Dotel.resource.attributes=service.name=JMSSender,service.version=0.0.1,deployment.environment=production
-Dotel.exporter.otlp.endpoint=
-Dotel.exporter.otlp.headers=
-Dotel.instrumentation.jms.enabled=false
-Dotel.javaagent.extensions=/Users/davidhope/IdeaProjects/jms10-instrumentation/target/jms10-instrumentation-1.0-SNAPSHOT.jar
-Dotel.javaagent.debug=true
-Dotel.traces.sampler=always_on
-Dotel.traces.sampler.arg=1.0

should look like:


![image](https://github.com/user-attachments/assets/d7c36823-6fe1-4218-b081-535259d8ca75)
