package demo.spring.service;

import hello.HelloWorldImpl;
import io.undertow.Undertow;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAutoConfiguration
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class Application extends SpringBootServletInitializer {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// Replaces the need for web.xml
	@Bean
	public ServletRegistrationBean servletRegistrationBean(ApplicationContext context) {
		return new ServletRegistrationBean(new CXFServlet(), "/api/*");
	}

	// Replaces cxf-servlet.xml
	@Bean
	// <jaxws:endpoint id="helloWorld" implementor="demo.spring.service.HelloWorldImpl" address="/HelloWorld"/>
	public EndpointImpl helloService() {
		Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
		Object implementor = new HelloWorldImpl();
		EndpointImpl endpoint = new EndpointImpl(bus, implementor);
		endpoint.publish("/hello");
		endpoint.getServer().getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
		endpoint.getServer().getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
		return endpoint;
	}

}