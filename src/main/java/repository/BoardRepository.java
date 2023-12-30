package repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entity.Board;
import domain.enum_class.BoardCategory;
import domain.enum_class.UserRole;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

	Page<Board> findAllByCategoryAndUserUserRoleNot(BoardCategory category, UserRole userRole, PageRequest pageRequest);

	Page<Board> findAllByCategoryAndTitleContainsAndUserUserRoleNot(BoardCategory category, String title,
			UserRole userRole, PageRequest pageRequest);

	Page<Board> findAllByCategoryAndUserNicknameContainsAndUserUserRoleNot(BoardCategory category, String nickname,
			UserRole userRole, PageRequest pageRequest);

	List<Board> findAllByUserLoginId(String loginId);

	List<Board> findAllByCategoryAndUserUserRole(BoardCategory category, UserRole userRole);

	Long countAllByUserUserRole(UserRole userRole);

	Long countAllByCategoryAndUserUserRoleNot(BoardCategory category, UserRole userRole);
}
