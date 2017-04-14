package edu.ustc.cs.alg;

import java.util.Scanner;

/**
 * Created by zyb on 2017/3/21.
 */
public class Main {
    /*  第一题
    public static void main(String[] args) {
        char[][] star = new char[15][21];

        for(int i = 0; i < 4; i++){
            for(int j = 10 - i; j <= 10 + i; j++){
                star[i][j] = '*';
            }
        }

        for(int i = 4; i <= 7; i++){
            for(int j = i - 4; j <= 24 - i ; j++){
                star[i][j] = '*';
            }
        }

        for(int i = 8; i < 11; i++){
            for(int j = 10 - i; j < 11 + i ; j++){
                star[i][j] = '*';
            }
        }

        for(int i = 11; i < 15; i++){
            for(int j = i - 4; j <= 24 - i ; j++){
                star[i][j] = '*';
            }
        }

        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 21; j++){
                System.out.print(star[i][j]);
            }
            System.out.println();
        }
    }
    */
    /* 第二题
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int limit = scanner.nextInt();
        for(int i = 2; i < limit; i++){
            if(isPrimeNumber(i)){
                System.out.print(i + " ");
            }
        }
    }
    //判断num是否是质数
    public static boolean isPrimeNumber(int num){
        if(num < 2){
            return false;
        }
        if(num == 2){
            return true;
        }
        for(int i = 2; i < Math.sqrt(num); i++){
            if(num % i == 0){
                return false;
            }
        }
        return true;
    }*/

//    第三题

    public static void main(String[] args) {
        //判断5个数字是否存在有两树相等用到的数组
        boolean[] flag = new boolean[10];
        for(int i = 10; i < 99; i++){
            for(int j = 1; j < 10; j++){
                int product = i * j;
                if(product < 100){
                    flag[i % 10] = true;
                    flag[i / 10] = true;
                    flag[j] = true;
                    flag[product % 10] = true;
                    flag[product / 10] = true;
                    if(hasConnectedNum(flag,5)){
                        System.out.println(i + "*" + j + "=" + product);
                    }
                    reset(flag);
                }
            }
        }
    }

    private static void reset(boolean[] flag) {
        for(int i = 0; i < flag.length; i++){
            flag[i] = false;
        }
    }

    public static boolean hasConnectedNum(boolean[] flag, int num){
        int count = 0;
        for(int i= 0; i < flag.length; i++){
            if(flag[i]){
                count ++;
            }
        }
        return count == num;
    }


}
