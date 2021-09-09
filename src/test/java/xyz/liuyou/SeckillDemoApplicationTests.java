package xyz.liuyou;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.liuyou.seckill.SeckillDemoApplication;

import java.awt.*;
import java.io.File;

@SpringBootTest(classes = SeckillDemoApplication.class)
class SeckillDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        System.out.println(Math.round(-4.7));
        Long nine = new Long(9);
        Integer ten = new Integer(10);
        long l = ten + nine;
        System.out.println(nine + ten);
        int i = 1;
        System.out.println(i + ten);
    }


}
