import akka.actor.Actor
import akka.actor.ActorRef

case class RenderLine(scene: Scene, width: Int, height: Int, frustum: Double, line: Int, coordinator: ActorRef)


class Tracer(scene: Scene) extends Actor {
  def receive = {
    case RenderLine(scene: Scene, width: Int, height: Int, frustum: Double, line: Int, coordinator: ActorRef) => {
      println("")
    } 
    case _ => {
     println("I dont't know about this message.") 
    }
  }
}