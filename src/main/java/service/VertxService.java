package service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;


public class VertxService extends AbstractVerticle {
	VertxActions vertxActions;
	
	public static void main(String[]args) throws Exception{
		Vertx vertx = Vertx.vertx();
	    vertx.deployVerticle(VertxService.class.getName());
	    new VertxService();
	}
	
	
	@Override
	public void start(Future<Void> future) {
		vertxActions = new VertxActions();
		vertxActions.createSomeWhiskies();
	
		Router router = Router.router(vertx);
		//enables the reading of the request body(json data) for all routes under "/api/whiskies"
		router.route("/api/whiskies*").handler(BodyHandler.create());
		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));
	 
		
		router.get("/api/whiskies").handler(vertxActions::getAllWhiskies);
	 
		// maps POST requests on /api/whiskies to the addOne method
		router.post("/api/whiskies").handler(vertxActions::addOneWhiskie);
		 
		final String whiskiesId = "/api/whiskies/:id";
		router.get(whiskiesId).handler(vertxActions::getOneWhiskie);
		router.put(whiskiesId).handler(vertxActions::updateOneWhiskie);
		router.delete(whiskiesId).handler(vertxActions::deleteOneWhiskie);
	 
		
		 router.route("/").handler(routingContext -> {
		   HttpServerResponse response = routingContext.response();
		   response
		       .putHeader("content-type", "text/html")
		       .end("<h1>Hello from my first Vert.x 3 application</h1>");
		 });
	 

	
		 // Create the HTTP server and pass the "accept" method to the request handler.
		 vertx
		     .createHttpServer()
		     .requestHandler(router::accept)
		     .listen(
		         // Retrieve the port from the configuration, default to 8080.
		         config().getInteger("http.port", 8080),
		         result -> {
		           if (result.succeeded()) {
		             future.complete();
		           } else {
		             future.fail(result.cause());
		           }
		         }
		     );
		}
}
