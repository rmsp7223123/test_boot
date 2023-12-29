package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
	void deleteByUserLoginIdAndBoardId(String loginId, Long boardId);

	Boolean existsByUserLoginIdAndBoardId(String loginId, Long boardId);

	List<Like> findAllByUserLoginId(String loginId);
}
