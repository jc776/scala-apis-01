import org.scalajs.dom
import scalatags.JsDom.all._

package object app {
  // "Auto-App" from an endpoint:
    // - is an elm-program? (init, view, update, Action, State)
    // - anything else?

  // "Auto-App" for e.g. objectEditor without a specifically built one:
  // using the *internal* endpoints?


  trait Editor[T] {
    // not sure if opaque?
    type State
    type Action

    def init(input: Option[T]): State
    def update(current: State, action: Action): State
    def view(state: State, send: Action => Unit): Tag

    def result(state: State): Option[T]
  }

  implicit object IntEditor extends Editor[Int] {
    type State = Int
    type Action = Int
    def init(input: Option[Int]) = input.getOrElse(0)
    def update(current: Int, action: Int) = action
    def view(state: Int, send: Int => Unit) = {
      def handleChange(e: dom.Event) = {
        send(e.target.asInstanceOf[dom.html.Input].value.toInt)
      }

      input(value := state, onchange := handleChange _)
    }

    def result(state: Int) = Some(state)
  }

  // Editor[Int]
  // Editor[String]
  // Editor[List] (!)
  // Editor[CaseClass from LabelledGeneric] (!!)


  // ObjectEditorProgram[T] from Editor[T] & endpoint "objectEditor"
  // - view
  // - edit
  // - save
}
