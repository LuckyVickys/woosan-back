package com.luckyvicky.woosan.testrepository.fileImg;

import com.luckyvicky.woosan.domain.fileImg.entity.FileImg;
import com.luckyvicky.woosan.domain.fileImg.repository.FileImgRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FileImgTests {

	@Autowired
	private FileImgRepository fileImgRepository;

	@Test
	void testInsert() {
		FileImg fileImg = FileImg.builder()
				.type("Image")
				.targetId(1L)
				.uuid("unique-uuid-1")
				.fileName("test-image.jpg")
				.ord(1)
				.path("/images")
				.build();
		fileImgRepository.save(fileImg); // DB에 즉시 반영

	}

	@Test
	void testDelete() {
		fileImgRepository.deleteById(6L);
	}

	@Test
	void testFindTargetImg() {
		List<FileImg> fileImgs = fileImgRepository.findByTypeAndTargetIdOrderByOrdAsc("Board", 1L);
		System.out.println("==========================================================");
		System.out.println(fileImgs);
		System.out.println("==========================================================");

	}

	@Test
	void testDeleteByTarget() {
		String type = "board";
		Long targetId = 2L;
		fileImgRepository.deleteByTypeAndTargetId(type, targetId);
		System.out.println("==========================================================");
		System.out.println("Target Deleted");
		System.out.println("==========================================================");

	}


}
