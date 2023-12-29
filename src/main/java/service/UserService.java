package service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import domain.dto.UserCntDto;
import domain.dto.UserDto;
import domain.dto.UserJoinRequest;
import domain.entity.Comment;
import domain.entity.Like;
import domain.entity.User;
import domain.enum_class.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import repository.CommentRepository;
import repository.LikeRepository;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final CommentRepository commentRepository;
	private final BCryptPasswordEncoder encoder;

	public BindingResult joinValid(UserJoinRequest req, BindingResult bindingResult) {

		// 회원 가입, 정보 수정 시 비어있는지, 글자수가 초과하는지, 중복되는지 등을
		// 상황과 변수에 맞게 판단 후 하나라도 조건을 만족하지 못하면 BindingResult를 return 함으로써 회원가입, 정보 수정을 못하게
		// 함

		if (req.getLoginId().isEmpty()) {
			bindingResult.addError(new FieldError("req", "loginId", "아이디가 비어있습니다."));
		} else if (req.getLoginId().length() > 10) {
			bindingResult.addError(new FieldError("req", "loginId", "아이디가 10자가 넘습니다."));
		} else if (userRepository.existsByLoginId(req.getLoginId())) {
			bindingResult.addError(new FieldError("req", "loginId", "아이디가 중복됩니다."));
		}

		if (req.getPassword().isEmpty()) {
			bindingResult.addError(new FieldError("req", "password", "비밀번호가 비어있습니다."));
		}

		if (!req.getPassword().equals(req.getPasswordCheck())) {
			bindingResult.addError(new FieldError("req", "passwordCheck", "비밀번호가 일치하지 않습니다."));
		}

		if (req.getNickname().isEmpty()) {
			bindingResult.addError(new FieldError("req", "nickname", "닉네임이 비어있습니다."));
		} else if (req.getNickname().length() > 10) {
			bindingResult.addError(new FieldError("req", "nickname", "닉네임이 10자가 넘습니다."));
		} else if (userRepository.existsByNickname(req.getNickname())) {
			bindingResult.addError(new FieldError("req", "nickname", "닉네임이 중복됩니다."));
		}

		return bindingResult;
	}

	public void join(UserJoinRequest req) {
		userRepository.save(req.toEntity(encoder.encode(req.getPassword())));
	}

	public User myInfo(String loginId) {
		return userRepository.findByLoginId(loginId).get();
	}

	public BindingResult editValid(UserDto dto, BindingResult bindingResult, String loginId) {

		// 회원 가입, 정보 수정 시 비어있는지, 글자수가 초과하는지, 중복되는지 등을
		// 상황과 변수에 맞게 판단 후 하나라도 조건을 만족하지 못하면 BindingResult를 return 함으로써 회원가입, 정보 수정을 못하게
		// 함

		User loginUser = userRepository.findByLoginId(loginId).get();

		if (dto.getNowPassword().isEmpty()) {
			bindingResult.addError(new FieldError("dto", "nowPassword", "현재 비밀번호가 비어있습니다."));
		} else if (!encoder.matches(dto.getNowPassword(), loginUser.getPassword())) {
			bindingResult.addError(new FieldError("dto", "nowPassword", "현재 비밀번호가 틀렸습니다."));
		}

		if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
			bindingResult.addError(new FieldError("dto", "newPasswordCheck", "비밀번호가 일치하지 않습니다."));
		}

		if (dto.getNickname().isEmpty()) {
			bindingResult.addError(new FieldError("dto", "nickname", "닉네임이 비어있습니다."));
		} else if (dto.getNickname().length() > 10) {
			bindingResult.addError(new FieldError("dto", "nickname", "닉네임이 10자가 넘습니다."));
		} else if (!dto.getNickname().equals(loginUser.getNickname())
				&& userRepository.existsByNickname(dto.getNickname())) {
			bindingResult.addError(new FieldError("dto", "nickname", "닉네임이 중복됩니다."));
		}

		return bindingResult;
	}

	@Transactional
	public void edit(UserDto dto, String loginId) {
		User loginUser = userRepository.findByLoginId(loginId).get();

		if (dto.getNewPassword().equals("")) {
			loginUser.edit(loginUser.getPassword(), dto.getNickname());
		} else {
			loginUser.edit(encoder.encode(dto.getNewPassword()), dto.getNickname());
		}
	}

	@Transactional
	public Boolean delete(String loginId, String nowPassword) {

		// DB에서 삭제하기 전 해당 유저가 추가한 댓글, 좋아요를 조회하여 Board의 likeCnt, commentCnt를 직접 수정

		User loginUser = userRepository.findByLoginId(loginId).get();

		if (encoder.matches(nowPassword, loginUser.getPassword())) {
			List<Like> likes = likeRepository.findAllByUserLoginId(loginId);
			for (Like like : likes) {
				like.getBoard().likeChange(like.getBoard().getLikeCnt() - 1);
			}

			List<Comment> comments = commentRepository.findAllByUserLoginId(loginId);
			for (Comment comment : comments) {
				comment.getBoard().commentChange(comment.getBoard().getCommentCnt() - 1);
			}

			userRepository.delete(loginUser);
			return true;
		} else {
			return false;
		}
	}

	public Page<User> findAllByNickname(String keyword, PageRequest pageRequest) {
		return userRepository.findAllByNicknameContains(keyword, pageRequest);
	}

	@Transactional
	public void changeRole(Long userId) {
		User user = userRepository.findById(userId).get();
		user.changeRole();
	}

	public UserCntDto getUserCnt() {
		return UserCntDto.builder().totalUserCnt(userRepository.count())
				.totalAdminCnt(userRepository.countAllByUserRole(UserRole.ADMIN))
				.totalBronzeCnt(userRepository.countAllByUserRole(UserRole.BRONZE))
				.totalSilverCnt(userRepository.countAllByUserRole(UserRole.SILVER))
				.totalGoldCnt(userRepository.countAllByUserRole(UserRole.GOLD))
				.totalBlacklistCnt(userRepository.countAllByUserRole(UserRole.BLACKLIST)).build();
	}
}
