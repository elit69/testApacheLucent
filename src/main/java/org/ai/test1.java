package org.ai;

import java.util.Random;

public class test1 {
	public static void main(String[] args) {
//		for (int i = 0; i <= 100; i++) {
//			System.out.println(randInt(1,6));
//		}
		System.out.println("sdfsdfasdfasdfasdfasdfasdf".substring(0,10));
	}

	public static int randInt(int min, int max) {
		return  new Random().nextInt((max - min) + 1) + min;
	}
	
}
