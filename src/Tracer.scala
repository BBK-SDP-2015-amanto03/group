import akka.actor.Actor
import akka.actor.ActorRef

case class RenderLine(scene: Scene, width: Int, height: Int, sinf: Double, cosf: Double, coordinator: ActorRef)

class Tracer() extends Actor {
  def receive = {
    case RenderLine(scene, width, height, sinf, cosf, coordinator) => {
      //println("")
    }
    case _ => {
      println("I dont't know about this message.")
    }
  }
}