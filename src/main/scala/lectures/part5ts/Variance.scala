package lectures.part5ts

object Variance extends App {
  trait Animal
  class Dog       extends Animal
  class Cat       extends Animal
  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type subsitiution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]

  // val icage: ICage[Animal] = new ICage[Cat]
  // hell no - opposite = contravariance
  class XCage[-T]
  val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: T) // invariant

  //covariant positions
  class CovariantCage[+T](val animal: T) // COVARIANT POSITION

  // class ContravariantCage[-T](val animal: T)
  /* if it worked, that would happen:
   * val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  // class CovariantVariableCage[+T](var animal: T) // types of vars in CONTRAVARIANT POSTION
  /*
   * val ccage: CCage[Animal] = new CCage[Cat](new Cat)
   * ccage.animal = new Crocodile
   */

  // class ContravariantVariableCage[-T](var animal: T) // also in COVARIANT POSITION

  class InvariantVariableCage[T](var animal: T) // ok

  // trait AnotherCovariantCage[+T] {
  //   def addAnimal(animal: T) //  method arguments are in contravarient position
  // }

  class AnotherContravarientCage[-T] {
    def addAnimal(animal: T) = true
  }

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

  // return types

  // class PetShop[-T] {
  //   def get(isItaPoppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
  /*
   * val catShop = new PetShop[Animal] {
   *  def get(isItaPuppu): Animal = new Cat
   * }
   * cal dogShop: PetShop[Dog] = catShop
   */
  // c}

  /*
   * Big rule
   * - methd arguments are in contravariant position
   * - return types are in covariant position
   */

  /* EXERCISE
   * 1. Invariant, covariant, contravariant Parking[T](List[T])
   * - method park(vehicle: T)
   * - method impound(vehicles: List[T])
   * - method checkVehicles(conditions: String): List[T]
   * 2. used someone else's API: IList[T]
   * 3. Parking = monad!
   *    - flatMap
   */
  class Vehicle
  class Bike  extends Vehicle
  class Car   extends Vehicle
  class Motor extends Vehicle
  class IList[T]

  class IParking[T](vehicles: List[T]) {
    def park(vehicle: T): IParking[T]              = ???
    def impound(vehicles: List[T]): IParking[T]    = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  class CParking[+T](vehicles: List[T]) {
    def park[B >: T](vehicle: B): CParking[B]           = ???
    def impound[B >: T](vehicles: List[B]): CParking[B] = ???
    def checkVehicles(conditions: String): List[T]      = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  // List is covariant!
  class ContravariantParking[-T](vehicles: List[T]) {
    def park(vehicle: T): ContravariantParking[T]           = ???
    def impound(vehicles: List[T]): ContravariantParking[T] = ???
    def checkVehicles[B <: T](conditions: String): List[B]  = ???

    def flatMap[R <: T, S](f: R => ContravariantParking[S]): ContravariantParking[S] = ???
  }
  /*
 * rule of thumbs
 * 1: use covariance = COLLECTION OF THINGS
 * 2: use contravariance = GROUP OF ACTIONS
 */

}
