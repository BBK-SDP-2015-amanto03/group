import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

object Trace {

  val AntiAliasingFactor = 4
  val Width = 800
  val Height = 600

  var rayCount = 0
  var hitCount = 0
  var lightCount = 0
  var darkCount = 0

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println("usage: scala Trace input.dat output.png")
      System.exit(-1)
    }

    val (infile, outfile) = (args(0), args(1))
    val scene = Scene.fromFile(infile)
    render(scene, outfile, Width, Height)

  }
  
  import Messages._

  def render(scene: Scene, outfile: String, width: Int, height: Int) = {
    
    val image = new Image(width, height)
    val system = ActorSystem("TracerSystem")
    val coordinator = system.actorOf(Props(new Coordinator(image, outfile)), "coordinator")
    implicit val timeout = Timeout(30 seconds)
    val future = coordinator ? RenderScene(scene, width, height)
    val result = Await.result(future, timeout.duration)
    
    println(f"\t$rayCount%,d rays cast")
    println(f"\t$hitCount%,d rays hit")
    println(f"\t$lightCount%,d light")
    println(f"\t$darkCount%,d dark")    
    println(result)
    system.shutdown()
    
  }
}