package com.sip.softphone.test;

public class Child extends Parent {

    public Child(int c1, int d1){
        super(c1,d1);
    }

    private int a1;
    private int b1;

    /*public Child(int a, int b){
        this.a1 = a;
        this.b1 = b;
    }*/

    /*public void print() {
        System.out.println("ina1= " + this.a1 + " inb1= " + this.b1);
    }*/

    public static void main(String[] args) {
        Parent pa = new Child(1,2);
        pa.print();
        Child ch = new Child(5,6);
        ch.print();
    }

}