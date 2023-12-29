package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findAllByBoardId(Long boardId);

	List<Comment> findAllByUserLoginId(String loginId);
}
