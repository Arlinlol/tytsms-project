package com.taiyitao.elasticsearch;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger log =  LoggerFactory.getLogger(JacksonUtils.class);

	public static String obj2Json(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.info("Object ==> JSON error "+ e.getMessage());
			throw new RuntimeException("Object ==> JSON error on : "
					+ obj.getClass().getName());
		}
	}

	public static <T> T json2Obj(String json, Class<T> valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (IOException e) {
			log.info("JSON ==> Object error :"+ e.getMessage());
			throw new RuntimeException("JSON ==> Object error on : " + json);
		}
	}
}
