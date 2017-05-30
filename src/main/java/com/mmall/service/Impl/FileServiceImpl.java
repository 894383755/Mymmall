package com.mmall.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mmall.service.IFileService;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
	
	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Override
	public String upload(MultipartFile file, String path){
		String fileName = file.getOriginalFilename();
		//扩展名字
		String fileExtensionName = fileName.substring(fileName.lastIndexOf('.')+1);
		String uploadFileName = UUID.randomUUID().toString() + '.' + fileExtensionName;
		logger.info("开始上传文件，上传文件名：{}，上传路径{}，新文件名字{}",fileName,path,uploadFileName);
		File fileDir = new File(path);
		if(!fileDir.exists()){
			fileDir.setWritable(true);
			fileDir.mkdirs(); 
		}
		File targeFile = new File(path,uploadFileName);
		try {
			file.transferTo(targeFile);
		} catch (IllegalStateException | IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return null;
		}
		return targeFile.getName();
	}
}
