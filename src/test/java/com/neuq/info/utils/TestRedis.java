package com.neuq.info.utils;

import com.neuq.info.common.utils.RedisUtil;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @AUTHOR lindexiang
 * @DATE 下午12:49
 */
public class TestRedis {

    private Jedis jedis;

    @Before
    public void setup() {
        jedis = RedisUtil.getJedis();
    }

    @Test
    public void testString() {
        //-----添加数据----------
        jedis.set("name","xinxin");//向key-->name中放入了value-->xinxin
        System.out.println(jedis.get("name"));//执行结果：xinxin
        jedis.append("name", " is my lover"); //拼接
        System.out.println(jedis.get("name"));
        jedis.del("name");  //删除某个键
        System.out.println(jedis.get("name"));
        //设置多个键值对
        jedis.mset("name","liuling","age","23","qq","476777XXX");
        jedis.incr("age"); //进行加1操作
        System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));
        jedis.set("eRSWjQIaantP20GV0fT1d3zP3FXSy3x9KUvfj7bYSSOxDM8Yibjqdnxfoh2znWyh", "123");
        jedis.get("eRSWjQIaantP20GV0fT1d3zP3FXSy3x9KUvfj7bYSSOxDM8Yibjqdnxfoh2znWyh");
        System.out.println(jedis.get("eRSWjQIaantP20GV0fT1d3zP3FXSy3x9KUvfj7bYSSOxDM8Yibjqdnxfoh2znWyh"));
    }
}
