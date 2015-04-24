import akka.actor.Actor

class Tracer() extends Actor {
  
  import Messages._

  def receive = {
    
    case RenderLine(scene, line, width, height, frustum, coordinator) => 

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

            val c = scene.trace(dir) / (ss * ss)
            colour += c
          }
        }

        if (Vector(colour.r, colour.g, colour.b).norm < 1)
          Trace.incrementDarkCount
        if (Vector(colour.r, colour.g, colour.b).norm > 1)
          Trace.incrementLightCount

        coordinator ! SetPixel(x, line, colour)
        
      }

    case unexpected => 
      println(s"ERROR: Unknown message ${unexpected}.")

  }
  
}