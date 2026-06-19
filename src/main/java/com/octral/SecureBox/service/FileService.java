package com.octral.SecureBox.service;

import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.FileRepository;
import com.octral.SecureBox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;

    public FileMetaData uploadFile(MultipartFile file, Long id) {
        User user=userRepository.findById(id).orElseThrow();

        FileMetaData meta=new FileMetaData();
        meta.setFileName(file.getOriginalFilename());
        meta.setFileSize(file.getSize());
        meta.setUser(user);

        return fileRepository.save(meta);
    }
}
