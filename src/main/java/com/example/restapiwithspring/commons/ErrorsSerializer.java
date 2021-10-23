package com.example.restapiwithspring.commons;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

/**
 * Errors객체는 BeanSerializer을 사용한 자바빈 스펙을 준수하지 않는 객체이므로 JSON으로 변환하도록 ErrorSerializer를 생성한다.
 * @JsonComponent를 사용하면 스프링이 Errors를 직렬화 할때 ErrorsSerializer를 사용한다.
 */
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeFieldName("errors"); //스프링 부트 2.3으로 올라가면서 Jackson 라이브러리가 더이상 Array부터 만드는걸 허용하지 않으므로 추가했음.
        jsonGenerator.writeStartArray();

        errors.getFieldErrors().stream().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("field", e.getField());
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                if (e.getRejectedValue() != null)
                    jsonGenerator.writeStringField("rejectedValue", e.getRejectedValue().toString());
                jsonGenerator.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        //global error
        errors.getGlobalErrors().stream().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                jsonGenerator.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        jsonGenerator.writeEndArray();
    }
}
