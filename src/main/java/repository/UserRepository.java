package repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import domain.entity.User;
import domain.enum_class.UserRole;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByLoginId(String loginId);
    Page<User> findAllByNicknameContains(String nickname, PageRequest pageRequest); // 닉네임이 String이 포함되어 있는지, 어드민이 user 검색시 사용
    Boolean existsByLoginId(String loginId); // 회원가입시 중복체크
    Boolean existsByNickname(String nickname); // 회원가입시 중복체크
    Long countAllByUserRole(UserRole userRole); // 해당 등급 체크
}
