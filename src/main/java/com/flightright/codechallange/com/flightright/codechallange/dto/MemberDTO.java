package com.flightright.codechallange.com.flightright.codechallange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String postalCode;
    private String pathOfPicture;

}
