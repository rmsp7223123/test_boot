package domain.dto;

import java.time.LocalDateTime;

import domain.entity.User;
import domain.enum_class.UserRole;
import lombok.Data;

@Data
public class UserJoinRequest { // 회원가입시 사용되는 DTO
	private String loginId;
	private String password;
	private String passwordCheck;
	private String nickname;

	public User toEntity(String encodedPassword) {
		return User.builder().loginId(loginId).password(encodedPassword).nickname(nickname).userRole(UserRole.BRONZE)
				.createdAt(LocalDateTime.now()).receivedLikeCnt(0).build();
	}

}
