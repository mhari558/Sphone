package com.sip.softphone.test;

/**
 * Created by hari on 12/5/17.
 */

public class Smaple {

    void eat(){System.out.println("eating...");}
    }
    class Dog extends Smaple{
        void bark(){System.out.println("barking...");}
    }
    class Cat extends Dog{
        void meow(){System.out.println("meowing...");}
    }
    class TestInheritance3{
        public static void main(String args[]){
            Dog c=new Dog();
          //  c.meow();
            c.eat();
            c.bark();
//c.bark();//C.T.Error
        }
}
