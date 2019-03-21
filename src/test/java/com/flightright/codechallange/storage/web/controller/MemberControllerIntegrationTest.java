package com.flightright.codechallange.storage.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightright.codechallange.com.flightright.codechallange.dto.MemberDTO;
import com.flightright.codechallange.entity.Member;
import com.flightright.codechallange.repository.MemberRepository;
import com.flightright.codechallange.storage.StorageService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerIntegrationTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private StorageService storageService;
    @Autowired
    private MemberRepository repository;
    @LocalServerPort
    private int port;
    private Member member;
    private String imageName;
    private ClassPathResource imageResourceDefault;

    @Before
    public void resetDb() throws IOException {
        storageService.deleteAll();
        storageService.init();
        repository.deleteAll();
        createMember();
        imageName = member.getId() + ".jpg";
        imageResourceDefault = new ClassPathResource("whale.jpg", getClass());
        Files.copy(imageResourceDefault.getInputStream(), storageService.getRootLocation().resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void shouldCreateMember() throws Exception {
        //given
        ClassPathResource imageResource = new ClassPathResource("testupload.jpg", getClass());
        ClassPathResource jsonResource = new ClassPathResource("jsondata", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", imageResource);
        map.add("json", jsonResource);

        //when
        ResponseEntity<MemberDTO> response = this.restTemplate.postForEntity("/api/member", map,
                MemberDTO.class);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MemberDTO memberDTOOutput = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertNotNull(memberDTOOutput.getId());

        String input = new BufferedReader(new InputStreamReader(jsonResource.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
        MemberDTO memberDTOInput = objectMapper.readValue(input, MemberDTO.class);
        //check rest response
        checkRestResponse(memberDTOInput, memberDTOOutput);

        //check picture of member
        assertNotNull(memberDTOOutput.getPathOfPicture());
        assertTrue(Files.exists(storageService.load(memberDTOOutput.getPathOfPicture())));

        //check database
        Member member = repository.findById(memberDTOOutput.getId()).get();
        assertDatabaseAndResponse(memberDTOInput, member);
    }

    @Test
    public void shouldUpdateMember() throws Exception {
        //given existing user
        ClassPathResource imageResource = new ClassPathResource("testupload.jpg", getClass());
        ClassPathResource jsonResourceUpdate = new ClassPathResource("jsondataupdate", getClass());

        MemberDTO updatedMemberDTO = MemberDTO.builder().id(member.getId())
                .firstName("James")
                .lastName("Brandon")
                .dateOfBirth(LocalDate.of(1989, 03, 17))
                .postalCode("23284")
                .build();

        try (PrintWriter out = new PrintWriter(jsonResourceUpdate.getFile())) {
            out.println(objectMapper.writeValueAsString(updatedMemberDTO));
        }

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", imageResource);
        map.add("json", jsonResourceUpdate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
                map, headers);

        //when
        ResponseEntity<MemberDTO> response = this.restTemplate.exchange("/api/member", HttpMethod.PUT, requestEntity, MemberDTO.class, map);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MemberDTO memberDTOOutput = response.getBody();

        Member updated = repository.findById(member.getId()).get();
        //check response
        checkRestResponse(updatedMemberDTO, memberDTOOutput);

        //check image is updated
        assertEquals((storageService.load(updated.getId() + ".jpg").toFile()).length(), imageResource.getFile().length());
        assertNotEquals(storageService.load(updated.getId() + ".jpg"), imageResourceDefault.getFile().length());

        //check database
        Member updatedMember = repository.findById(memberDTOOutput.getId()).get();
        assertDatabaseAndResponse(updatedMemberDTO, updatedMember);
    }

    @Test
    public void shouldDeleteMember() throws Exception {
        //given existing user
        assertTrue(repository.findById(member.getId()).isPresent());
        assertTrue(Files.exists(storageService.load(imageName)));

        //when
        this.restTemplate.delete("/api/member/" + member.getId());

        //then
        assertFalse(repository.findById(member.getId()).isPresent());
        assertTrue(Files.notExists(storageService.load(imageName)));
    }

    @Test
    public void shouldGetMember() throws Exception {
        //given existing user
        assertTrue(repository.findById(member.getId()).isPresent());
        assertTrue(Files.exists(storageService.load(imageName)));

        //when
        ResponseEntity<MemberDTO> response = this.restTemplate.getForEntity("/api/member/" + member.getId(), MemberDTO.class);

        //then
        //check rest response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MemberDTO memberDTOOutput = response.getBody();
        assertDatabaseAndResponse(memberDTOOutput, member);
        assertEquals(imageName, memberDTOOutput.getPathOfPicture());
    }

    @Test
    public void shouldListMember() throws Exception {
        //given existing user
        assertTrue(repository.findById(member.getId()).isPresent());
        assertTrue(Files.exists(storageService.load(imageName)));

        //when
        ResponseEntity<List<MemberDTO>> response = this.restTemplate.exchange("/api/member", HttpMethod.GET, null, new ParameterizedTypeReference<List<MemberDTO>>() {
        });
        List<MemberDTO> memberDTOList = response.getBody();
        assertEquals(1, memberDTOList.size());
        MemberDTO memberDTOOutput = memberDTOList.get(0);
        assertDatabaseAndResponse(memberDTOOutput, member);
        assertEquals(imageName, memberDTOOutput.getPathOfPicture());
    }

    private void createMember() {
        Member newMember = Member.builder().firstName("Mike").lastName("Zemon")
                .dateOfBirth(LocalDate.of(1980, 06, 06))
                .postalCode("12845").build();
        Member memberSaved = repository.save(newMember);
        memberSaved.setProfilePictureType("jpg");
        member = repository.save(memberSaved);
    }

    private void assertDatabaseAndResponse(MemberDTO memberDTOInput, Member member) {
        assertEquals(memberDTOInput.getFirstName(), member.getFirstName());
        assertEquals(memberDTOInput.getLastName(), member.getLastName());
        assertEquals(memberDTOInput.getPostalCode(), member.getPostalCode());
        assertEquals(memberDTOInput.getDateOfBirth(), member.getDateOfBirth());
    }

    private void checkRestResponse(MemberDTO memberDTOInput, MemberDTO memberDTOOutput) {
        assertEquals(memberDTOInput.getFirstName(), memberDTOOutput.getFirstName());
        assertEquals(memberDTOInput.getLastName(), memberDTOOutput.getLastName());
        assertEquals(memberDTOInput.getPostalCode(), memberDTOOutput.getPostalCode());
        assertEquals(memberDTOInput.getDateOfBirth(), memberDTOOutput.getDateOfBirth());
    }
}
