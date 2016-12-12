package morphdom
import org.scalajs.dom
import scala.scalajs.js

@js.native
object morphdom extends js.Object {
  def apply(fromNode: dom.Node, toNode: dom.Node): dom.Node = js.native
  // toNode: dom.Node | String
  // options: various
}
