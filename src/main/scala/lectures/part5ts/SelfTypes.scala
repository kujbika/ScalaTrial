package lectures.part5ts

object SelfTypes extends App {
  // requiring a type to be mixed in
  trait InstrumentList {
    def play(): Unit
  }

  trait Singer { self: InstrumentList => // SELF TYPE (you can call it anythin) whoever implements Singer to implement InstrumentList
    def sing(): Unit
  }

  class LeadSinger extends Singer with InstrumentList {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  // class VocalList extends Singer {
  //   override def sing(): Unit = ???
  // }

  val jamesHetfield = new Singer with InstrumentList {
    override def sing(): Unit = ???

    override def play(): Unit = ???

  }

  class Guitarist extends InstrumentList {
    override def play(): Unit = println("(quitar solo)")
  }
  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }
  // vs inheritance
  class A
  class B extends A // B is an A
  trait T
  trait S { self: T => } // S requires T

  // CAKE PATTERN => "dependency injection!"
  // classical dependency injection
  class Component {
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val compontent: Component)
  // CAKE PATTERN - this is checked compile time
  trait ScalaComponent {

    def action(x: Int): String
  }
  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + "this rocks"
  }

  trait ScalaApplication { self: ScalaDependentComponent => }
  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats   extends ScalaComponent

  // layer 2
  trait Profile   extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3
  trait AnalyticsApp extends ScalaApplication with Analytics

  // cyclical dependencies
  // class X extends Y
  // class Y extends X NOT COMPILE
  trait X { self: Y => } // sister components
  trait Y { self: X => }
}
