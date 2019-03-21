package com.flightright.codechallange.storage.mapper;

import com.flightright.codechallange.com.flightright.codechallange.dto.MemberDTO;
import com.flightright.codechallange.entity.Member;
import com.flightright.codechallange.mapper.MemberMapper;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class MemberMapperTest {

    @Test
    public void convertToEntityTest() {
        //given
        MemberDTO memberDTO = MemberDTO.builder().firstName("Mike").lastName("Zemon")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .postalCode("12845").build();

        MemberMapper mapper = new MemberMapper();
        //when
        Member member = mapper.convertToEntity(memberDTO);
        //then
        assertEquals(memberDTO.getFirstName(), member.getFirstName());
        assertEquals(memberDTO.getLastName(), member.getLastName());
        assertEquals(memberDTO.getPostalCode(), member.getPostalCode());
        assertEquals(memberDTO.getDateOfBirth(), member.getDateOfBirth());
    }

    @Test
    public void convertToDTOTest() {
        Member member = Member.builder().firstName("Mike").lastName("Zemon")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .postalCode("12845").profilePictureType("jpg").build();
        MemberMapper mapper = new MemberMapper();
        //when
        MemberDTO memberDTO = mapper.convertToDTO(member);
        //then
        assertEquals(memberDTO.getFirstName(), member.getFirstName());
        assertEquals(memberDTO.getLastName(), member.getLastName());
        assertEquals(memberDTO.getPostalCode(), member.getPostalCode());
        assertEquals(memberDTO.getDateOfBirth(), member.getDateOfBirth());
        assertEquals(memberDTO.getPathOfPicture(), member.getId() + "." + member.getProfilePictureType());
    }
}
