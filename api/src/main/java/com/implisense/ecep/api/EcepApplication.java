package com.implisense.ecep.api;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.implisense.ecep.api.config.EcepConfig;
import com.implisense.ecep.api.resources.DataResource;
import com.implisense.ecep.api.resources.SearchResource;
import io.dropwizard.Application;
import io.dropwizard.jersey.errors.EarlyEofExceptionMapper;
import io.dropwizard.jersey.errors.LoggingExceptionMapper;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.ConstraintViolationExceptionMapper;
import io.dropwizard.server.AbstractServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class EcepApplication extends Application<EcepConfig> {
    public static void main(String[] args) throws Exception {
        new EcepApplication().run(args);
    }

    @Override
    public String getName() {
        return "paragraph-classification-service";
    }

    @Override
    public void initialize(Bootstrap<EcepConfig> bootstrap) {
    }

    @Override
    public void run(EcepConfig configuration, Environment environment) {

        AbstractServerFactory sf = (AbstractServerFactory) configuration.getServerFactory();
        // disable all default exception mappers
        sf.setRegisterDefaultExceptionMappers(Boolean.FALSE);
        // explicit register default exception mappers
        environment.jersey().register(new ConstraintViolationExceptionMapper());
        environment.jersey().register(new LoggingExceptionMapper<Throwable>() {});
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(new EarlyEofExceptionMapper());

        // SNI should be deactivated for the crawler
        System.setProperty("jsse.enableSNIExtension", "false");

        // Create Guice injector
        Injector injector = Guice.createInjector(new EcepGuiceModule(configuration, environment));

        // Enable multipart form uploads
        environment.jersey().register(MultiPartFeature.class);

        // Add resources
        environment.jersey().register(injector.getInstance(DataResource.class));
        environment.jersey().register(injector.getInstance(SearchResource.class));
    }
}
