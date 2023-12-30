package domain.dto;

import org.springframework.web.multipart.MultipartFile;

import domain.entity.Board;
import domain.entity.User;
import domain.enum_class.BoardCategory;
import lombok.Data;

@Data
public class BoardCreateRequest {

	private String title;
	private String body;
	private MultipartFile uploadImage;

	public Board toEntity(BoardCategory category, User user) { // DTO 객체를 Board 엔티티 객체로 변환
		return Board.builder().user(user).category(category).title(title).body(body).likeCnt(0).commentCnt(0).build();
	}
}
