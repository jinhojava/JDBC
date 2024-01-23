package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("memberV100", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember{}", member.equals(findMember));
        Assertions.assertThat(findMember).isEqualTo(member);
        //실행결과에 member객체의 참조값이 아니라 실제 데이터가 보이는 이유는 롬복의 @Data가 toString()을 적절히
        //오버라이딩해서 보여주기 때문이다. 로그값이 참인 이유도 마친가지.@Data는 해당 객체의 모든 필드를 사용하도록 equals()를 오버라이딩 하기 때문


        //update: money: 10000->20000
        repository.update(member.getMemberId(),20000);
        Member updateMember = repository.findById(member.getMemberId());
        Assertions.assertThat(updateMember.getMoney()).isEqualTo(20000);


        //delete
        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(()->repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
        //Assertions.assertThatThrownBy는 특정 코드 블록이 예외를 던지는지를 검증하는 메소드입니다.
        //해당예외가 발생해야 검증에 성공한다.
    }
}