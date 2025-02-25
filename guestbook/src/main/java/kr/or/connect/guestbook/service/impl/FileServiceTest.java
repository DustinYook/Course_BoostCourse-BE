package kr.or.connect.guestbook.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import kr.or.connect.guestbook.config.ApplicationConfig;
import kr.or.connect.guestbook.dto.FileInfo;
import kr.or.connect.guestbook.service.FileService;

public class FileServiceTest 
{
	public static void main(String[] args) 
	{
		ApplicationContext ac = new AnnotationConfigApplicationContext(ApplicationConfig.class); // 스프링 컨테이너
		FileService fileService = ac.getBean(FileService.class); // Bean 얻어옴
		
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileId(1L);
		fileInfo.setFileName("connect.png");
		fileInfo.setSaveName("c:/tmp/connect.png");
		fileInfo.setContentType("image/png");
		fileInfo.setFileLength(1116303L);
		
		FileInfo result = fileService.addFileInfo(fileInfo);
		System.out.println(result);
		
		System.out.println(fileService.getFileInfo(1L));
	}
}