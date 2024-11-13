package com.file_upload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    //Folder to store the files
    private static final String STORAGE_DIRECTORY = "D:\\Spring Boot\\Spring-Boot-File-Upload\\Storage";

    public void saveFile(MultipartFile file) throws IOException {

        if (file == null){
            throw new NullPointerException("file is null");
        }

        // Target file will be the location at which the multipart file will be saved in our storage directory
//        var targetFile = new File(STORAGE_DIRECTORY + "\"  + file.getOriginalFilename());
        /*
            the separator we used here only works in linux so we have to use File.separator,
            this will use the separator based on the platform we are using.
            file.getOriginalFilename() - this will return the original file name of the uploading file
        */
        var targetFile = new File(STORAGE_DIRECTORY + File.separator + file.getOriginalFilename());
        System.out.println("targetFile : " + targetFile);

        // Security Check
        // when we get the file name using getOriginalFilename(), if the filename consists any slash '\' it will create a new folder in the storage directory,
        // or the hacker can move forward or backward on the directory and create folders their, to avoid that we have to use a security check
        if (!Objects.equals(targetFile.getParent(), STORAGE_DIRECTORY)) {
            System.out.println("targetFile.getParent() : " + targetFile.getParent());
            throw new SecurityException("Unsupported filename!");
        }
        // Copy the Content of the multipart file into the target-file
        // StandardCopyOption.REPLACE_EXISTING - if the user upload two files with same name, we have to replace the existing file.
        // we can also add logic to add numbers, if the user upload two files with same name.
        Files.copy(file.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String filename) throws Exception {
        if(filename == null){
            throw new NullPointerException("Filename is null!");
        }
        var fileToDownload = new File(STORAGE_DIRECTORY + File.separator + filename);
        if (!Objects.equals(fileToDownload.getParent(), STORAGE_DIRECTORY)) {
            System.out.println("targetFile.getParent() : " + fileToDownload.getParent());
            throw new SecurityException("Unsupported filename!");
        }
        if (!fileToDownload.exists()){
            throw new FileNotFoundException("No file named : " + filename);
        }
        return fileToDownload;
    }
}
