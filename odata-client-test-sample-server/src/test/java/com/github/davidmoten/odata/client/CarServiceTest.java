package com.github.davidmoten.odata.client;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.server.sample.CarsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.junit.Test;

import olingo.odata.sample.container.Container;
import olingo.odata.sample.entity.Car;

public class CarServiceTest {

    @Test
    public void test() throws Exception {
        StdErrLog logger = new StdErrLog();
        logger.setDebugEnabled(false);
        Log.setLog(logger);

        Server server = new Server(8090);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setSessionHandler(new SessionHandler());
        handler.addServlet(CarsServlet.class, "/cars.svc/*");
        server.setHandler(handler);

        server.start();
        Container c = new Container(new Context(Serializer.DEFAULT,
                Service.create(new Path("http://localhost:8090/cars.svc", PathStyle.IDENTIFIERS_IN_ROUND_BRACKETS))));

        // test get collection
        List<Car> list = c.cars().get().toList();
        list.stream().forEach(car -> System.out.println(car.getModel().orElse("") + " at $"
                + car.getCurrency().orElse("") + " " + car.getPrice().map(BigDecimal::toString).orElse("?")));
        assertEquals(5, list.size());
        assertEquals("F1 W03", list.get(0).getModel().orElse(null));

        // get single entity
        {
            Car car = c.cars().id("1").get();
            assertEquals("F1 W03", car.getModel().get());
            
            // test patch
            car.withPrice(Optional.of(BigDecimal.valueOf(123456))).patch();
            car = c.cars().id("1").get();
            assertEquals(123456, car.getPrice().get());
        }

        server.stop();
    }

}