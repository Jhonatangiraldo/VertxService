package org.gradle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/* Note:
 * All processing actions in Vert.x web are implemented as handler. 
 * To create a handler you always call the create method.
 */
public class First extends AbstractVerticle {
	
	public static void main(String[]args) throws Exception{
		Vertx vertx = Vertx.vertx();
	    vertx.deployVerticle(First.class.getName());
 	    //for initialize the start method
	    new First();
	}
	
	@Override
	public void start(Future<Void> fut) {
	 Router router = Router.router(vertx);

	 // Bind "/" to our hello message - so we are still compatible.
	 router.route("/").handler(routingContext -> {
	   HttpServerResponse response = routingContext.response();
	   response
	       .putHeader("content-type", "text/html")
	       .end("<h1>Hello from my first Vert.x 3 application</h1>");
	 });
	 
	 // Serve static resources from the /assets directory
	 router.route("/assets/*").handler(StaticHandler.create("assets"));

	 // Create the HTTP server and pass the "accept" method to the request handler.
	 vertx
	     .createHttpServer()
	     .requestHandler(router::accept)
	     .listen(
	         // Retrieve the port from the configuration,
	         // default to 8080.
	         config().getInteger("http.port", 8080),
	         result -> {
	           if (result.succeeded()) {
	             fut.complete();
	           } else {
	             fut.fail(result.cause());
	           }
	         }
	     );
	}
	
	
}
