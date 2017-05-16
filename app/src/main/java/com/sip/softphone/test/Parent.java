package com.sip.softphone.test;

public class Parent {
    private int a1;
    private int b1;


    public Parent(int a, int b){
        this.a1 = a;
        this.b1 = b;
    }

    public void print() {
        System.out.println("a1= " + this.a1 + " b1= " + this.b1);
    }

}