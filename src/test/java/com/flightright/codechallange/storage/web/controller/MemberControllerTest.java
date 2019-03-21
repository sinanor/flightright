package com.flightright.codechallange.storage.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightright.codechallange.com.flightright.codechallange.dto.MemberDTO;
import com.flightright.codechallange.entity.Member;
import com.flightright.codechallange.mapper.MemberMapper;
import com.flightright.codechallange.service.MemberService;
import com.flightright.codechallange.storage.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest

public class MemberControllerTest {

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper mapper;

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MemberDTO memberDTO = MemberDTO.builder().firstName("Jack").lastName("Andersom")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .postalCode("82731").build();
        MemberDTO memberDTOSaved = MemberDTO.builder().id("41828a52-44b9-44f5-8bbd-7665c2d7fe52")
                .firstName("Jack").lastName("Andersom")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .pathOfPicture("41828a52-44b9-44f5-8bbd-7665c2d7fe52.txt")
                .postalCode("82731").build();
        Member member = Member.builder().firstName("Jack").lastName("Andersom")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .postalCode("82731").build();
        Member memberSaved = Member.builder().id("41828a52-44b9-44f5-8bbd-7665c2d7fe52").firstName("Jack").lastName("Andersom")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .profilePictureType("txt")
                .postalCode("82731").build();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", objectMapper.writeValueAsString(memberDTO).getBytes());

        given(memberService.save(member)).willReturn(memberSaved);
        given(mapper.convertToDTO(memberSaved)).willReturn(memberDTOSaved);
        given(mapper.convertToEntity(memberDTO)).willReturn(member);

        mvc.perform(MockMvcRequestBuilders.multipart("/api/member")
                .file(multipartFile)
                .file(jsonFile))
                .andExpect(status().isOk());

        then(this.storageService).should().store(multipartFile, memberSaved.getId() + ".txt");
        then(this.memberService).should().save(member);
    }

    //TO DO mor unit test for controller
}
