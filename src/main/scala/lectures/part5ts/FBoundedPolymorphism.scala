package lectures.part5ts

object FBoundedPolymorphism extends App {

  // trait Animal {
  //   def breed: List[Animal]
  // }
  // class Cat extends Animal {
  //   override def breed: List[Animal] = ??? // we like to have list of Cats!
  // }
  //

  //1; Naive
  // trait Animal {
  //   def breed: List[Animal]
  // }
  // class Cat extends Animal {
  //   override def breed: List[Cat] = ???
  // }
  // class Dog extends Animal {
  //   override def breed: List[Cat] = ??? // we like to have list of Dogs!
  // }

  // solution 2
  // trait Animal[A <: Animal[A]] { // recursive type: F-Bounded polymorphism
  //   def breed: List[Animal[A]]
  // }
  // class Cat extends Animal[Cat] {
  //   override def breed: List[Animal[Cat]] = ???
  // }
  // class Dog extends Animal[Dog] {
  //   override def breed: List[Animal[Dog]] = ??? // we like to have list of Dogs!
  // }

  // class Person extends Comparable[Person] { // F-Bounded polymorphism
  //   override def compareTo(x: Person): Int = ???
  // }

  // class Crocodile extends Animal[Dog] {
  //   override def breed: List[Animal[Dog]] = ??? // List[Dog]!!
  // }
  //  Solution 3 - FBP + self-types

  trait Animal[A <: Animal[A]] { self: A => // recursive type: F-Bounded polymorphism
    def breed: List[Animal[A]]
  }
  class Cat extends Animal[Cat] {
    override def breed: List[Animal[Cat]] = ???
  }
  class Dog extends Animal[Dog] {
    override def breed: List[Animal[Dog]] = ??? // we like to have list of Dogs!
  }

  // class Crocodile extends Animal[Dog] {
  //   override def breed: List[Animal[Dog]] = ??? // List[Dog]!!
  // }

  trait Fish extends Animal[Fish]
  class Shark extends Fish {
    override def breed: List[Animal[Fish]] = ??? // this is the fundamental limitation of the FBP
  }

  // solution 4

  // trait Animal2
  // trait CanBreed[A] {
  //   def breed(a: A): List[A]
  // }
  // class Dog2 extends Animal2
  // object Dog2 {
  //   implicit object DogsCanBreed extends CanBreed[Dog] {
  //     override def breed(a: Dog): List[Dog] = Nil
  //   }
  // }
  // implicit class CanBreedOps[A](animal: A) {
  //   def breed(implicit canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
  // }
  // val dog = new Dog2
  // // dog.breed

  // class Cat2 extends Animal2
  // object Cat2 {
  //   implicit object CatsCanBreed extends CanBreed[Dog] {
  //     def breed(a: Dog): List[Dog] = Nil
  //   }
  // }
  // val cat = new Cat2
  // // cat.breed

  trait Animal3[A] {
    def breed(animal: A): List[A]
  }

  class Dog3
  object Dog3 {
    implicit object DogAnimal extends Animal3[Dog3] {
      override def breed(animal: Dog3): List[Dog3] = Nil
    }
  }
  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal3[A]) = animalTypeClassInstance.breed(animal)
  }
  val dog3 = new Dog3

  dog3.breed
}
