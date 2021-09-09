package xyz.liuyou;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/5 15:24
 * @decription
 **/
public class T extends Thread{

    static String a = "a";

    public static void main(String[] args) {
        T t = new T();
        t.p(a);
    }

    public void p(String a){
        System.out.println(a + "p");
        start();
    }

    @Override
    public void run() {
        System.out.println("===");
    }

}
