package com.file_upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileManagerController {

    @Autowired
    private FileStorageService storageService;
    private static final Logger log = Logger.getLogger(FileManagerController.class.getName());

//    When we use MultipartFile we have to specify the maximum size of the multipartfile
    @PostMapping("/upload/file")
    public boolean uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            storageService.saveFile(file);
            return true;
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception during upload");
        }
        return false;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        // we will read the name of the file to be downloaded from the request using the get parameter file,
        // and we will return that file to the user for downloading

        try {
            var fileToDownload = storageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }
}
