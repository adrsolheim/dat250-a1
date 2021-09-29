package no.hvl.dat110.rest.counters;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.post;

import com.google.gson.Gson;
import no.hvl.dat110.rest.dao.TodoDAO;
import no.hvl.dat110.rest.model.Todo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Optional;

/**
 * Hello world!
 *
 */
public class App {

	private static final String PERSISTENCE_UNIT_NAME = "todorest";
	private static EntityManagerFactory factory;
	//static Counters counters = null;
	
	public static void main(String[] args) {

		if (args.length > 0) {
			port(Integer.parseInt(args[0]));
		} else {
			port(8080);
		}
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		TodoDAO todoDao = new TodoDAO(em);


		//counters = new Counters();
		
		after((req, res) -> {
  		  res.type("application/json");
  		});

		post("/todo", (req, res) -> {
			Todo todo = new Gson().fromJson(req.body(), Todo.class);
			System.out.println(todo.toString());
			todoDao.save(todo);

			return "Todo successfully added!";
		});

		get("/todos", (req, res) -> new Gson().toJsonTree(todoDao.getAll()));

		get("/todos/:id", (req, res) -> {
			Gson gson = new Gson();
			try {
				int id = Integer.parseInt(req.params("id"));
				return gson.toJson(todoDao.get(id));

			} catch (Exception e) {
				e.printStackTrace();
				return gson.toJson(null);
			}
		});

		put("/todos/:id", (req, res) -> {
			try {
				int id = Integer.parseInt(req.params("id"));
				Todo todoUpdate = new Gson().fromJson(req.body(), Todo.class);
				Optional<Todo> dbTodo = todoDao.get(id);
				if (!dbTodo.isPresent()) {
					return "Unable to update Todo-item: Does not exist.";
				}
				Todo todo = dbTodo.get();
				todoDao.update(todo, new String[]{todoUpdate.getSummary(), todoUpdate.getDescription()});

				return new Gson().toJson(todoDao.get(id).get());

			} catch (Exception e) {
				return "Invalid id / invalid json.";
			}
		});

		/*
		get("/hello", (req, res) -> "Hello World!");

        get("/counters", (req, res) -> counters.toJson());
 
        get("/counters/red", (req, res) -> counters.getRed());

        get("/counters/green", (req, res) -> counters.getGreen());

        // TODO: put for green/red and in JSON
        // variant that returns link/references to red and green counter
        put("/counters", (req,res) -> {
        
        	Gson gson = new Gson();
        	
        	counters = gson.fromJson(req.body(), Counters.class);
        
            return counters.toJson();
        	
        });

		 */
    }
    
}
