package com.github.nestedview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void as3df() {
        double pow = Math.pow(2, 4);
        System.out.println("==="+pow);
    }
    @Test
    public void asdf() {
        Map<String,TestBean> map=new HashMap<String,TestBean>();
        List<TestBean> list=new ArrayList<>();
        TestBean bean=new TestBean();
        map.put("a",bean);
        list.add(bean);
        map.get("a").a="map";

        System.out.println("=========="+list.get(0).a);

    }
    public class TestBean{
        String a="1";
    }
}