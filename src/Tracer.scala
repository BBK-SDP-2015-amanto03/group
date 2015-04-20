import akka.actor.Actor
import akka.actor.ActorRef

case class RenderLine(scene: Scene, line: Int, width: Int, height: Int, frustum: Double, coordinator: ActorRef)

class Tracer() extends Actor {
  
  def receive = {
    
    case RenderLine(scene, line, width, height, frustum, coordinator) => {

    val cosf = math.cos(frustum)
    val sinf = math.sin(frustum)
    val ss = Trace.AntiAliasingFactor

      for (x <- 0 until width) {
        var colour = Colour.black
        for (dx <- 0 until ss) {
          for (dy <- 0 until ss) {

            // Create a vector to the pixel on the view plane formed when
            // the eye is at the origin and the normal is the Z-axis.
            val dir = Vector(
              (sinf * 2 * ((x + dx.toFloat / ss) / width - .5)).toFloat,
              (sinf * 2 * (height.toFloat / width) * (.5 - (line + dy.toFloat / ss) / height)).toFloat,
              cosf.toFloat).normalized

            val c = scene.trace(Ray(scene.eye, dir)) / (ss * ss)
            colour += c
          }
        }

        if (Vector(colour.r, colour.g, colour.b).norm < 1)
          Trace.darkCount += 1
        if (Vector(colour.r, colour.g, colour.b).norm > 1)
          Trace.lightCount += 1

        coordinator ! SetPixel(x, line, colour)
        
      }

    }
    case _ => {
      println("I dont't know about this message.")
    }
  }
  
  
  
  
}