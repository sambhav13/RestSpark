import javax.servlet.ServletContext
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json._
import org.scalatra.servlet.ScalatraListener

case class User(id: Int, name: String)

case class CreateUser(name: String)
case class UserCreated(id: Int)

case class UpdateUser(name: String)
case class UserUpdated(index: Int)

class UserController extends ScalatraServlet with JacksonJsonSupport {

      protected implicit val jsonFormats: Formats = DefaultFormats

      //This collection represents a simple in-memory data source (i.e. it is mutable and not thread-safe)
      var users: List[User]  = List(User(1, "John"), User(2, "Todd"), User(3, "Chris"))

      before() {
          contentType = formats("json")
      }

      get("/users") {
          users
      }

      get("/users/:id") {
          users.find(u => u.id == params("id").toInt) match {
              case Some(user) => Ok(user)
              case None       => NotFound("User not found")
          }
      }

      post("/users") {
          val userData = parsedBody.extract[CreateUser]
          val id = users.foldLeft (0) ( (maxId, u) => if (u.id > maxId) u.id else maxId ) + 1 
        
          users = User(id, userData.name) :: users

          UserCreated(id)
      }

      put("/users/:id") {
          val id = params("id").toInt
          val matchingIndex = users.zipWithIndex.collect { case (User(`id`, _), i) => i }
          if (matchingIndex.isEmpty) 
              NotFound("User not found")
          else {
              val index = matchingIndex.head
              val newUserData = parsedBody.extract[UpdateUser]
              
              users = users.updated(index, User(id, newUserData.name))
               
              Ok(UserUpdated(index))
          }
      }

      delete("/users/:id") {
        users = users.filter( u => u.id != params("id").toInt)
        Ok("Done") 
      }

}

class ScalatraBootstrap extends LifeCycle {
    override def init(context: ServletContext) {
        context mount (new UserController, "/*")
    }
}

object SimpleScalatraRestService extends App {
    val port = 8083
    val server = new Server(port)

    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase(".")
    context.setInitParameter(ScalatraListener.LifeCycleKey, "ScalatraBootstrap")
    context.setEventListeners(Array(new ScalatraListener))
    
    server.setHandler(context)
    server.start

    println("***** Supported operations *****")
    println("List users:   curl -v http://localhost:8080/users")
    println("User details: curl -v http://localhost:8080/users/<id>")
    println("Create user:  curl -v http://localhost:8080/users -X POST -d '{\"name\": \"<someName>\"}' -H 'Content-type: application/json'")
    println("Update user:  curl -v http://localhost:8080/users/<id> -X PUT -d '{\"name\": \"<newName>\"}' -H 'Content-type: application/json'")
    println("Delete user:  curl -v http://localhost:8080/users/<id> -X DELETE")
    println("********************************")
        
    server.join
}