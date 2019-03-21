package com.flightright.codechallange.mapper;

import com.flightright.codechallange.com.flightright.codechallange.dto.MemberDTO;
import com.flightright.codechallange.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper implements Mapper<MemberDTO, Member> {

    @Override
    public Member convertToEntity(MemberDTO memberDTO) {
        return Member.builder().id(memberDTO.getId())
                .firstName(memberDTO.getFirstName())
                .lastName(memberDTO.getLastName())
                .dateOfBirth(memberDTO.getDateOfBirth())
                .postalCode(memberDTO.getPostalCode())
                .build();
    }

    @Override
    public MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder().id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .postalCode(member.getPostalCode())
                .pathOfPicture(member.getId() + "." + member.getProfilePictureType())
                .build();
    }
}
