package service;

import org.springframework.stereotype.Service;

import domain.entity.Board;
import domain.entity.Like;
import domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import repository.BoardRepository;
import repository.LikeRepository;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final LikeRepository likeRepository;
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	@Transactional
	public void addLike(String loginId, Long boardId) {
		Board board = boardRepository.findById(boardId).get();
		User loginUser = userRepository.findByLoginId(loginId).get();
		User boardUser = board.getUser();

		// 자신이 누른 좋아요가 아니라면
		if (!boardUser.equals(loginUser)) {
			boardUser.likeChange(boardUser.getReceivedLikeCnt() + 1);
		}
		board.likeChange(board.getLikeCnt() + 1);

		likeRepository.save(Like.builder().user(loginUser).board(board).build());
	}

	@Transactional
	public void deleteLike(String loginId, Long boardId) {
		Board board = boardRepository.findById(boardId).get();
		User loginUser = userRepository.findByLoginId(loginId).get();
		User boardUser = board.getUser();

		// 자신이 누른 좋아요가 아니라면
		if (!boardUser.equals(loginUser)) {
			boardUser.likeChange(boardUser.getReceivedLikeCnt() - 1);
		}
		board.likeChange(board.getLikeCnt() - 1);

		likeRepository.deleteByUserLoginIdAndBoardId(loginId, boardId);
	}

	public Boolean checkLike(String loginId, Long boardId) {
		return likeRepository.existsByUserLoginIdAndBoardId(loginId, boardId);
	}
}
