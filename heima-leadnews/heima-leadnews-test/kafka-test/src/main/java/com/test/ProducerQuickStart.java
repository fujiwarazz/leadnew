package com.test;

/**
 * @Author peelsannaw
 * @create 29/12/2022 15:07
 */
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 生产者
 */
public class ProducerQuickStart {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.kafka的配置信息
        Properties properties = new Properties();
        //kafka的连接地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");
        //发送失败，失败的重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,5);
        //消息key的序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //消息value的序列化器
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //ack配置  0:不接受确认信息 1:接受leader的确认信息 2:接受leader和所有的replicant
        properties.put(ProducerConfig.ACKS_CONFIG,1);
        //配置压缩算法 gzip lz4 snappy
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"gzip");
        //2.生产者对象
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        //封装发送的消息
        ProducerRecord<String,String> record = new ProducerRecord<>("topic-1", "100001", "hello kafka");

        //3.发送消息
        //同步发送
        //RecordMetadata recordMetadata = producer.send(record).get();
        //异步发送
        producer.send(record, (recordMetadata, e) -> {
            if(e!=null){
                System.out.println("记录异常");
            }else{
                System.out.println(recordMetadata.offset());
            }
        });
        //4.关闭消息通道，必须关闭，否则消息发送不成功
        producer.close();
    }
}