package xyz.liuyou;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/6 12:03
 * @decription
 **/
public class S {

    static int i = 1;

    public static void main(String[] args) throws InterruptedException {
        S s = new S();
        S s2 = new S();
        i = 2;
        System.out.println(i);
        System.out.println(s.i);
        System.out.println(s2.i);
        s.i = 3;
        System.out.println(i);
        System.out.println(s.i);
        System.out.println(s2.i);
        Thread t = new Thread(){
            @Override
            public void run() {
                i = 10;
//                System.out.println(i);
//                System.out.println(s.i);
//                System.out.println(s2.i);
            }
        };
        t.start();
        Thread.sleep(1000);
        System.out.println(i);
        System.out.println(s.i);
        System.out.println(s2.i);

    }
}
