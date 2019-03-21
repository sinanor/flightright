package com.flightright.codechallange.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightright.codechallange.com.flightright.codechallange.dto.MemberDTO;
import com.flightright.codechallange.entity.Member;
import com.flightright.codechallange.mapper.MemberMapper;
import com.flightright.codechallange.service.MemberService;
import com.flightright.codechallange.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberMapper memberMapper;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @PostMapping(value = "/member")
    public MemberDTO createMember(@RequestPart("file") MultipartFile file, @RequestPart("json") String memberDTOJSONString) throws IOException {
        MemberDTO memberDTO = objectMapper.readValue(memberDTOJSONString, MemberDTO.class);
        return saveMember(file, memberDTO);
    }

    @PutMapping(value = "/member")
    public MemberDTO updateeMember(@RequestPart("file") MultipartFile file, @RequestPart("json") String memberDTOJSONString) throws IOException {
        MemberDTO memberDTO = objectMapper.readValue(memberDTOJSONString, MemberDTO.class);
        if (!memberService.findById(memberDTO.getId()).isPresent()) {
            throw new ResourceNotFoundException();
        }
        return saveMember(file, memberDTO);
    }

    private MemberDTO saveMember(@RequestPart("file") MultipartFile file, MemberDTO memberDTO) {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        Member member = memberMapper.convertToEntity(memberDTO);
        member.setProfilePictureType(extension);
        MemberDTO dto = memberMapper.convertToDTO(memberService.save(member));
        storageService.store(file, dto.getId() + "." + extension);
        return dto;
    }

    @GetMapping("/member/{id}")
    public MemberDTO getMember(@PathVariable("id") String id) {
        return memberService.findById(id).map(memberMapper::convertToDTO).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/member")
    public List<MemberDTO> getAllMembers() {
        return memberService.findAll().stream().map(memberMapper::convertToDTO).collect(Collectors.toList());
    }

    @DeleteMapping("/member/{id}")
    public void deleteMember(@PathVariable("id") String id) {
        Member member = memberService.findById(id).orElseThrow(ResourceNotFoundException::new);
        String fileName = member.getId() + "." + member.getProfilePictureType();
        storageService.delete(fileName);
        memberService.deleteById(id);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
