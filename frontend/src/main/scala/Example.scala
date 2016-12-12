package example
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import dom.ext.Ajax

import scalatags.JsDom.all._
import upickle.default._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import shared._
import client._

import shapeless.{ HList, ::, HNil }
import shapeless.record._
import shapeless.labelled._
import shapeless.syntax.singleton._

import morphdom.morphdom

import app._

/*
Not worrying about tables yet.
// tableEditor[Person]

sealed trait Gender
case object male extends Gender
case object female extends Gender
case class Person(name: String, number: Int, gender: Gender)
case class DbEntry[T](id: Int, entry: T)
*/


case class A(string: String, number: Int)
case class B(a: String, b: Int, c: String, d: Int)

@JSExport
object ScalaJSExample {
  val api =
    "a" ->> "a".andThen(objectEditor[A]) ::
    "b" ->> "b".andThen(objectEditor[B]) ::
    HNil
  // andThens currently get nested, which is inconvenient

  val apiA = api("a")
  val apiB = api("b")
  //val apiC = api("c") - caught at compile time.

  val editorA = client(apiA, Request(basePath = "http://localhost:12344"))
  val editorB = client(apiB, Request(basePath = "http://localhost:12344"))
  //val apiClient = client(api, Request(basePath = "http://localhost:12344")) - would prefer.
  //val editorA = apiClient("a")
  //val editorB = apiClient("b")


  @JSExport
  def embed(node: dom.Node): Unit = {
    try {
      main(node)
    }
    catch {
      case ex: Exception => println(ex.toString)
    }
  }

  def main(node: dom.Node): Unit = {
    node.appendChild(h1("Hello!").render)

    val intEditor = implicitly[Editor[Int]]
    var state = intEditor.init(None)
    var tree: dom.Node = div().render
    node.appendChild(tree)

    def view(state: intEditor.State, send: intEditor.Action => Unit){
      div(
        h2("AAAAAA")
        //intEditor.view(state, handleUpdate _),
        //"Result: ", intEditor.result(state)
      )
    }

    def handleUpdate(action: intEditor.Action) {
      state = intEditor.update(state, action)
      tree = morphdom(tree, (intEditor.view(state, handleUpdate _)).render)
    }

    tree = morphdom(tree,view(state, handleUpdate _).render)
    //morphdom(tree, div(h2("aaaaa")).render)
    println("OK!")


    /*editorA.get().foreach(cfg => {
      val content = div(write(cfg))
      editorA.set(cfg.copy(number = cfg.number + 1))
      node.appendChild(content.render)
    })
    editorB.get().foreach(cfg => {
      val content = div(write(cfg))
      editorB.set(cfg.copy(b = cfg.b + 1))
      node.appendChild(content.render)
    })*/
  }
}
