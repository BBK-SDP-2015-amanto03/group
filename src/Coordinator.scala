import scala.annotation.elidable
import scala.annotation.elidable.ASSERTION

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala

case class RenderScene(scene: Scene, width: Int, height: Int)
case class SetPixel(x: Int, y: Int, colour: Colour)

class Coordinator(image: Image, outfile: String) extends Actor {

  // Number of pixels we're waiting for to be set.
  private var waiting = image.width * image.height
  
  // Are we currently rendering?
  private var running = false

  // Who asked us to start rendering?
  private var renderRequester: Option[ActorRef] = None

  // TODO: make set a message
  def set(x: Int, y: Int, c: Colour) = {
    image(x, y) = c
    waiting -= 1
  }

  def print = {
    assert(waiting == 0)
    image.print(outfile)
  }

  override def receive = {
    case RenderScene(scene, width, height) =>
      if (running) {
        println("Already running.")
      } else {
        println("Render started")
        running = true
        renderRequester = Some(sender)
        scene.traceImage(width, height, context, self)
      }
    case SetPixel(x, y, colour) =>
      image(x, y) = colour
      waiting -= 1
      if (waiting == 0) {
        print
        println("Render finished")
        renderRequester.map(_ ! "Done")
      }
  }

}