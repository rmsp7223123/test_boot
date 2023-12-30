package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import domain.entity.Board;
import domain.entity.UploadImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import repository.BoardRepository;
import repository.UploadImageRepository;

@Service
@RequiredArgsConstructor
public class S3UploadService {
	
	//  Amazon S3(Simple Storage Service)를 사용하여 파일 업로드, 다운로드 및 삭제

	private final AmazonS3 amazonS3;
	private final UploadImageRepository uploadImageRepository;
	private final BoardRepository boardRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public UploadImage saveImage(MultipartFile multipartFile, Board board) throws IOException {
		
		// 파일 업로드
		
		if (multipartFile.isEmpty()) {
			return null;
		}

		String originalFilename = multipartFile.getOriginalFilename();

		// 원본 파일명 -> 서버에 저장된 파일명 (중복 X)
		// 파일명이 중복되지 않도록 UUID로 설정 + 확장자 유지
		String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		// S3에 파일 업로드
		amazonS3.putObject(bucket, savedFilename, multipartFile.getInputStream(), metadata);

		// 업로드된 파일의 메타데이터와 저장된 파일명을 데이터베이스에 저장하고, 그 정보를 UploadImage 엔티티로 반환
		return uploadImageRepository
				.save(UploadImage.builder().originalFilename(originalFilename).savedFilename(savedFilename).build());
	}

	// 확장자 추출
	private String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}

	public ResponseEntity<UrlResource> downloadImage(Long boardId) {
		
		// boardId를 받아 해당하는 게시글의 이미지를 S3에서 다운로드할 수 있는 URL로 만들어주고, 이를 ResponseEntity 형태로 반환
		
		// boardId에 해당하는 게시글이 없으면 null return
		Board board = boardRepository.findById(boardId).get();
		if (board == null || board.getUploadImage() == null) {
			return null;
		}

		UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket, board.getUploadImage().getSavedFilename()));

		// 업로드 한 파일명이 한글인 경우 아래 작업을 안해주면 한글이 깨질 수 있음
		String encodedUploadFileName = UriUtils.encode(board.getUploadImage().getOriginalFilename(),
				StandardCharsets.UTF_8);
		String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

		// header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
		// ResponseEntity를 사용하여 클라이언트에게 반환될 HTTP 응답을 생성
		// HttpHeaders.CONTENT_DISPOSITION 헤더를 사용하여 다운로드를 유도하는 설정을 추가
		// ok() 메서드로 응답 코드를 200 OK로 설정하고, header() 메서드로 다운로드를 유도하는 헤더를 추가
		// body() 메서드로 이미지의 URL을 ResponseEntity의 본문(body)에 설정하여 클라이언트에게 반환
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(urlResource);

	}

	// 해당 파일명에 해당하는 이미지의 S3 URL 주소 반환
	public String getFullPath(String filename) {
		return amazonS3.getUrl(bucket, filename).toString();
	}

	@Transactional
	public void deleteImage(UploadImage uploadImage) {
		uploadImageRepository.delete(uploadImage);
		amazonS3.deleteObject(bucket, uploadImage.getSavedFilename());
	}

}
