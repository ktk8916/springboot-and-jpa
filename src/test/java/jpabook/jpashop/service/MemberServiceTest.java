package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("회원가입")
    public void 회원가입() throws Exception{
        Member member = new Member();
        member.setName("memberA");

        Long saveId = memberService.join(member);

        em.flush();
        Assertions.assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    @DisplayName("중복 회원은 예외를 터트린다")
    public void 중복_회원_예외() throws Exception{
        Member member1 = new Member();
        member1.setName("memberA");
        Member member2 = new Member();
        member2.setName("memberA");

        memberService.join(member1);

        Assertions.assertThrows(IllegalStateException.class,
                ()->memberService.join(member2));

        //Assertions.fail("예외가 발생해야 한다");
    }

}