package com.heima.config;

import feign.RequestTemplate;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author peelsannaw
 * @create 15/11/2022 下午11:14
 */


@Configuration
public class FeignConfig {
//    @Bean
//    public Decoder feignDecoder() {
//
//        return new FeignSpringFormEncoder();
//    }
//
//    @Bean
//    public Encoder feignEncoder() {
//        return  new SpringEncoder(feignHttpMessageConverter());
//    }
//
//    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
//        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new GateWayMappingJackson2HttpMessageConverter());
//        return () -> httpMessageConverters;
//    }
//
//    public class GateWayMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
//        GateWayMappingJackson2HttpMessageConverter() {
//            List<MediaType> supportedMediaTypes = new ArrayList<>();
//            supportedMediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
//            supportedMediaTypes.add(MediaType.APPLICATION_JSON);
//            supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//            supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
//            supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
//            supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
//            supportedMediaTypes.add(MediaType.APPLICATION_PDF);
//            supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
//            supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
//            supportedMediaTypes.add(MediaType.APPLICATION_XML);
//            supportedMediaTypes.add(MediaType.IMAGE_GIF);
//            supportedMediaTypes.add(MediaType.IMAGE_JPEG);
//            supportedMediaTypes.add(MediaType.IMAGE_PNG);
//            supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
//            supportedMediaTypes.add(MediaType.TEXT_HTML);
//            supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
//            supportedMediaTypes.add(MediaType.TEXT_PLAIN);
//            supportedMediaTypes.add(MediaType.TEXT_XML);
//            setSupportedMediaTypes(supportedMediaTypes);
//        }
//    }

}

