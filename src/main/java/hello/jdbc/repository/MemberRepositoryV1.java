package hello.jdbc.repository;


import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/*
* JDBC -DataSource 사용, jdbcUtils 사용
* */

@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null; //연결객체
        PreparedStatement pstmt = null; //이것을 사용해 데이터베이스 쿼리날림

        try {
            con = getConnection();//연결을 얻어온다.
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());//?,?에 값 바인딩하기
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();//실제전달메소드  데이터변경할때 //해당메소드는 쿼리를 실행하고 영향받은 row수를 반환한다.
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);   //다 끝나면 닫아줘야한다.
        }
    }


    //주어진 memberId에 해당하는 회원 정보를 데이터베이스에서 조회하고, 그 결과를 Member 객체에 저장하는 메소드
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt =null;
        ResultSet rs = null;

        try{
            con=getConnection();
            pstmt =con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();//select쿼리 결과를 담고있는 통
            if(rs.next()){//데이터가 있냐없냐물어본다.최초한번은 실행해야 내부의 커서가 최초의 데이터를 가리키고 잇냐없냐를 판단
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else {
                throw new NoSuchElementException("member not found memberid"+memberId);
            }
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql ="update member set money=? where member_id=?";
        Connection con =null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);//?에 값 바인딩하기
            pstmt.setString(2,memberId );
            int resultSize = pstmt.executeUpdate();//데이터변경 //동시에 영향받은 줄 수를 반환받을 수 있다. 100줄변경시 100반환
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);   //다 끝나면 닫아줘야한다.
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con =null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId );
            pstmt.executeUpdate();//데이터변경 //동시에 영향받은 줄 수를 반환받을 수 있다. 100줄변경시 100반환
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);   //다 끝나면 닫아줘야한다.
        }
    }



    //Statement는 sql을 그대로 넣는거고  prepareStatement는 파라미터 바인딩이 가능 기능더많음
    //prepareStatement는 Statement를 상속받음
    //닫는 메소드, 생성과 반대로 역순으로
    private void close(Connection con, Statement stmt, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
