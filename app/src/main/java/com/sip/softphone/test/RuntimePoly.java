package com.sip.softphone.test;

/**
 * Created by hari on 12/5/17.
 */

public class RuntimePoly {

        void run(){System.out.println("running");}
    }
    class Splender extends RuntimePoly{
        void run(){System.out.println("running safely with 60km");}

        public static void main(String args[]){
            RuntimePoly b = new Splender();//upcasting
            b.run();
        }
    }

