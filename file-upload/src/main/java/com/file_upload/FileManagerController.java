package com.file_upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        log.log(Level.INFO, "[NORMAL] Download with /download");
        try {
            var fileToDownload = storageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath()))); //Converting the file into inputstream
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    // To download file faster
    // Multipart downloading, or downloading file by converting into multiple chunks, it increases the download speed
    // We can see that by using the Internet Download Manager (download it from internet)
    @GetMapping("/downloadFaster")
    public ResponseEntity<Resource> downloadFileFaster(@RequestParam("filename") String filename) {
        log.log(Level.INFO, "[FASTER] Download with /downloadFaster");
        try {
            var fileToDownload = storageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload)); // the only change from the above method
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }


}
