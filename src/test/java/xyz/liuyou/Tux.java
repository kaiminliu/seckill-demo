package xyz.liuyou;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/6 11:38
 * @decription
 **/
public class Tux extends Thread{

    static String sName = "vandeleur";
    public static void main(String argv[]) throws InterruptedException {
        Tux t = new Tux();
        t.piggy(sName);
        t.start();
        sleep(10000);
        System.out.println(sName);

    }
    public void piggy(String sName){
        sName = sName + " wiggy";
//        start();
    }
    @Override
    public void run(){

        for(int i=0;i  <  4; i++){
            sName = sName + " " + i;
//            System.out.println(sName);
        }
    }

}
