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
		String fileName = file.getOriginalFilename();//文件名
		String fileExtensionName = fileName.substring(fileName.lastIndexOf('.')+1);//扩展名字
		String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;//存储的随机名字
		logger.info("开始上传文件，上传文件名：{}，上传路径{}，新文件名字{}",fileName,path,uploadFileName);
		File fileDir = new File(path);
		if(!fileDir.exists()){//创建目录
			fileDir.setWritable(true);
			fileDir.mkdirs(); 
		}
		File targeFile = new File(path,uploadFileName);
		try {
			file.transferTo(targeFile);
			//targeFile.delete();//删除文件
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			logger.error("文件上传失败");
			return null;
		}
		return targeFile.getName();
	}
	
}
