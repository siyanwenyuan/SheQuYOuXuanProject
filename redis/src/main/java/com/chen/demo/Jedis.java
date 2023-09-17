package com.chen.demo;

public class Jedis {
    public static void main(String[] args) {

        redis.clients.jedis.Jedis jedis = new redis.clients.jedis.Jedis("localhost", 6379);
       // jedis.auth("200409wc");
        System.out.println(jedis.ping());


    }
}
