package object shared {
  // JsonObjectEditor, or whatever.
  case class ObjectEndpoint[T]()
  def objectEditor[T] = ObjectEndpoint[T]()

  case class RouteThen[Route,Endpoint](route: Route, end: Endpoint)
  trait WithRouteThen {
    def andThen[Endpoint](end: Endpoint) = RouteThen(this, end)
  }

  // or, just use "String" directly.
  case class PathRoute(str: String) extends WithRouteThen
  implicit class StringPathThen(str: String) {
    def andThen[Endpoint](end: Endpoint) = RouteThen(PathRoute(str), end)
  }

  case class Request(
    basePath: String
  )

  val defaultRequest = Request(
    basePath = "/"
  )
}

package object client {
  import shared._
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext
  import upickle.default._

  import org.scalajs.dom
  import dom.ext.Ajax

  trait Client[Api] {
    type Impl
    def client(api: Api, req: Request): Impl
  }

  def client[Api](api: Api, baseReq: Request = defaultRequest)(implicit impl: Client[Api]) = {
    impl.client(api, baseReq)
  }

  trait ObjectEditor[T] {
    def get(): Future[T]
    def set(value: T): Unit
  }

  implicit def ObjectClient[T](implicit rd: Reader[T], wt: Writer[T], ec: ExecutionContext) =
    new Client[ObjectEndpoint[T]] {
      type Impl = ObjectEditor[T]

      // I should be making an internal api
      // "get" ->> getMethod.andThen(jsonResponse[T])
      // "set" ->> postMethod.andThen(jsonBody[T].andThen(ok))
      // and then this one uses those methods.
      def client(api: ObjectEndpoint[T], req: Request) = new ObjectEditor[T] {
        def get() = Ajax.get(req.basePath).map(res => read[T](res.responseText))
        def set(value: T) = Ajax.put(
          req.basePath,
          write(value),
          headers = Map("Content-Type" -> "application/json")
        )
      }
    }

  implicit def PathClient[Endpoint](implicit impl: Client[Endpoint]) =
    new Client[RouteThen[PathRoute,Endpoint]] {
      type Impl = impl.Impl
      def client(api: RouteThen[PathRoute,Endpoint], req: Request) = {
        impl.client(api.end, req.copy(basePath = req.basePath + "/" + api.route.str))
      }
    }

  // I'd prefer a more general way of doing these three
  // "shapeless/typeclass.scala"
  // ... also works on case-classes, apparently.
  // Will need more for "Editor[T]"
  import shapeless.{ HList, ::, HNil, Lazy}
  import shapeless.record._
  import shapeless.labelled._
  import shapeless.syntax.singleton._

  implicit object HNilClient extends Client[HNil.type] {
    type Impl = HNil
    def client(api: HNil.type, req: Request) = HNil
  }

  // I don't seem to be able to separate "FieldType" out.
  // =:= here means "if tailImpl.Impl is an HList", so that returning ::(head, tail) accepts it.
  /*
  "Diverging implicit expansion". Ick.
  implicit def RecordHConsClient[Key, Endpoint, Tail <: HList, C <: Client[Tail], CI <: HList]
    (implicit headImpl: Client[Endpoint], tailImpl: C, evidence: C#Impl =:= CI) =
    new Client[FieldType[Key,Endpoint] :: Tail] {
      type Impl = FieldType[Key, headImpl.Impl] :: CI
      def client(api: FieldType[Key,Endpoint] :: Tail, req: Request) = {
        //::(field[Key](headImpl.client(api.head, req)), tailImpl.client(api.tail, req))
        val tailClient: CI = tailImpl.client(api.tail, req)
        field[Key](headImpl.client(api.head, req)) :: tailClient
      }
    }
    */

/*
  // =:= here means "if tailImpl.Impl is an HList"
  implicit def HConsClient[Endpoint, Tail <: HList, C <: Client[Tail], CI <: HList]
    (implicit headImpl: Client[Endpoint], tailImpl: C, evidence: =:=[C#Impl,CI]) =
    new Client[Endpoint :: Tail] {
      type Impl = headImpl.Impl :: CI
      def client(api: Endpoint :: Tail, req: Request): Impl = {
        val tail: CI = tailImpl.client(api.tail, req)
        headImpl.client(api.head, req) :: tail
      }
    }
    */

/*
  implicit def RecordClient[Key <: Symbol,Endpoint](implicit impl: Client[Endpoint]): Client[FieldType[Key,Endpoint]] =
    new Client[FieldType[Key,Endpoint]] {
      type Impl = FieldType[Key, impl.Impl]
      def client(api: FieldType[Key,Endpoint], req: Request): Impl = {
        val cl: impl.Impl = impl.client(api, req)
        new FieldBuilder[Key].apply(cl)
        // Key ->> ...
      }
    }
  */
}

// app(objectEditor[Person])

// app("person".andThen(objectEditor[Person])) (api compat.)

// app("Person" ->> ... :: ...) (Menus. Submenus?)
// app(HNil)

// json-server:
// ?q=*full text search*
// ?aaa= or ?aaa_ne, _gte, _lte, _like
// ?id=1&id=2 (does this get 0 or 2 things?)
// ?id_gte=1&id_lte=10 should get 1-10, etc
