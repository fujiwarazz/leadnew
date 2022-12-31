package com.test;

/**
 * @Author peelsannaw
 * @create 29/12/2022 15:07
 */

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 消费者
 */
public class ConsumerQuickStart {

    public static void main(String[] args) {
        //1.添加kafka的配置信息
        Properties properties = new Properties();
        //kafka的连接地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");
        //消费者组 一个组只有一个消费者能收到信息
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        //消息的反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        //2.消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        //3.订阅主题
        consumer.subscribe(Collections.singletonList("topic-1"));

        //当前线程一直处于监听状态
        while (true) {
            //4.获取消息
            try {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    System.out.println(consumerRecord.offset());
                }
                consumer.commitAsync();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("async异常");
            }finally {
                try{
                    consumer.commitSync();
                }finally {
                    consumer.close();
                }
            }

//            //同步提交
//            try {
//                consumer.commitSync();
//            } catch (CommitFailedException e) {
//                System.out.println("记录error");
//                e.printStackTrace();
//            }
//            //异步
//            consumer.commitAsync(new OffsetCommitCallback() {
//                @Override
//                public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
//                    if(e==null){
//                        System.out.println("异步异常");
//                    }
//                }
//            });

        }

    }

}