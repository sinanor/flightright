package com.flightright.codechallange.storage.service;

import com.flightright.codechallange.entity.Member;
import com.flightright.codechallange.repository.MemberRepository;
import com.flightright.codechallange.service.MemberService;
import com.flightright.codechallange.service.MemberServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;

    @Test
    public void findByIdTest() {
        memberService.findById("41828a52-44b9-44f5-8bbd-7665c2d7fe52");
        Mockito.verify(memberRepository).findById("41828a52-44b9-44f5-8bbd-7665c2d7fe52");
    }

    @Test
    public void deleteByIdTest() {
        memberService.deleteById("41828a52-44b9-44f5-8bbd-7665c2d7fe52");
        Mockito.verify(memberRepository).deleteById("41828a52-44b9-44f5-8bbd-7665c2d7fe52");
    }

    @Test
    public void findAllTest() {
        memberService.findAll();
        Mockito.verify(memberRepository).findAll();
    }

    @Test
    public void saveTest() {
        Member member = Member.builder().build();
        memberService.save(member);
        Mockito.verify(memberRepository).save(member);
    }

    @TestConfiguration
    static class DeveloperServiceImplTestContextConfiguration {
        @Bean
        public MemberService memberService() {
            return new MemberServiceImpl();
        }
    }
}
