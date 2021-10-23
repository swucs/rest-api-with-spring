package com.example.restapiwithspring.events;


import com.example.restapiwithspring.common.RestDocsConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith 애도테이션은 스프링 부트 2.1 출시되면서 Junit4에서 Junit5로 변경되면서 없어짐.
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test") //application.properties 파일을 공용으로 사용하고 application-test.properties에서 Override한다.
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
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self")
                                , linkWithRel("query-events").description("link to query events")
                                , linkWithRel("update-event").description("link to update an existing event")
                                , linkWithRel("profile").description("link to profile")
                        )
                        , requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        )
                        , requestFields(
                                fieldWithPath("name").description("Name of new event")
                                , fieldWithPath("description").description("description of new event")
                                , fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                , fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                , fieldWithPath("location").description("location of new event")
                                , fieldWithPath("basePrice").description("base price of new event")
                                , fieldWithPath("maxPrice").description("max price of new event")
                                , fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
                        )
                        , responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type : Hal Json Type")
                        )
                        , responseFields(
                                fieldWithPath("id").description("Identifier of new event")
                                , fieldWithPath("name").description("Name of new event")
                                , fieldWithPath("description").description("description of new event")
                                , fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                , fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                , fieldWithPath("location").description("location of new event")
                                , fieldWithPath("basePrice").description("base price of new event")
                                , fieldWithPath("maxPrice").description("max price of new event")
                                , fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
                                , fieldWithPath("free").description("It tells if this event is free or not")
                                , fieldWithPath("offline").description("It tells if this event is offline or not")
                                , fieldWithPath("eventStatus").description("eventStatus")
                                , fieldWithPath("_links.self.href").description("link to self")
                                , fieldWithPath("_links.query-events.href").description("link to query events")
                                , fieldWithPath("_links.update-event.href").description("link to update an existing event")
                                , fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
                ;

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
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }

    @ParameterizedTest()
    @MethodSource("paramsForTestOffline")
    public void testOffline(String location, boolean isOffline) {
        Event event = Event.builder()
                .location(location)
                .build();

        event.update();

        assertThat(event.isOffline()).isEqualTo(isOffline);

    }

    private static Object[] paramsForTestOffline() {
        return new Object[] {
                new Object[] {"강남역", true}
                , new Object[] {null, false}
                , new Object[] {"    ", false}
        };
    }
}