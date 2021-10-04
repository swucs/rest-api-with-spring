package com.example.restapiwithspring.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getMaxPrice() < eventDto.getBasePrice() && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue", "baseprice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong.");
        }

        if (eventDto.getCloseEnrollmentDateTime().isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong value");
        }
    }
}
