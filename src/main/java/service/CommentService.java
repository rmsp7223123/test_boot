package service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import domain.dto.CommentCreateRequest;
import domain.entity.Board;
import domain.entity.Comment;
import domain.entity.User;
import domain.enum_class.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import repository.BoardRepository;
import repository.CommentRepository;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	public void writeComment(Long boardId, CommentCreateRequest req, String loginId) {
		Board board = boardRepository.findById(boardId).get();
		User user = userRepository.findByLoginId(loginId).get();
		board.commentChange(board.getCommentCnt() + 1);
		commentRepository.save(req.toEntity(board, user));
	}

	public List<Comment> findAll(Long boardId) {
		return commentRepository.findAllByBoardId(boardId);
	}

	@Transactional
	public Long editComment(Long commentId, String newBody, String loginId) {
		Optional<Comment> optComment = commentRepository.findById(commentId);
		Optional<User> optUser = userRepository.findByLoginId(loginId);
		if (optComment.isEmpty() || optUser.isEmpty() || !optComment.get().getUser().equals(optUser.get())) {
			return null;
		}

		Comment comment = optComment.get();
		comment.update(newBody);

		return comment.getBoard().getId();
	}

	public Long deleteComment(Long commentId, String loginId) {
		Optional<Comment> optComment = commentRepository.findById(commentId);
		Optional<User> optUser = userRepository.findByLoginId(loginId);
		if (optComment.isEmpty() || optUser.isEmpty() || (!optComment.get().getUser().equals(optUser.get())
				&& !optUser.get().getUserRole().equals(UserRole.ADMIN))) {
			return null;
		}

		Board board = optComment.get().getBoard();
		board.commentChange(board.getCommentCnt() - 1);

		commentRepository.delete(optComment.get());
		return board.getId();
	}
}
