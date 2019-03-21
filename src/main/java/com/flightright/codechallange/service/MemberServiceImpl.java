package com.flightright.codechallange.service;

import com.flightright.codechallange.entity.Member;
import com.flightright.codechallange.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        memberRepository.deleteById(id);
    }

    @Override
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
