package com.example.restapiwithspring.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith 애도테이션은 스프링 부트 2.1 출시되면서 Junit4에서 Junit5로 변경되면서 없어짐.
@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .endEventDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED)));



    }


    @Test
    public void createEventBadRequest() throws Exception {

        Event event = Event.builder()
                .id(10)
                .name("spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .endEventDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;

    }

    @Test
    @DisplayName("입력값이 비어있는 경우 에러발생하는 테스트")
    public void createEventBadRequestEmptyInput() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF8")
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @DisplayName("입력값이 문제가 있는 경우 에러발생하는 테스트")
    public void createEventBadRequestWrongInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("description")
                //시작일이 종료일보다 늦은 시간이면 안됨.
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 19, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                .endEventDateTime(LocalDateTime.of(2018, 11, 11, 19, 0, 0))
                //basePrice > maxprice보다 크면 안됨
                .basePrice(100)
                .maxPrice(50)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF8")
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
}