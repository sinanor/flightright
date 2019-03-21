package com.flightright.codechallange.service;

import com.flightright.codechallange.entity.Member;

import java.util.List;
import java.util.Optional;


public interface MemberService {
    Optional<Member> findById(String id);

    void deleteById(String id);

    Member save(Member member);

    List<Member> findAll();
}
