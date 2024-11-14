package com.file_upload;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class FileManagerGuiController {

    @GetMapping("/uploader")
    public String uploader() {
        return "uploader";
    }

    @GetMapping("/list-files")
    public String listFiles(Model model) throws Exception {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream( new File(FileStorageService.STORAGE_DIRECTORY).toPath())){
            model.addAttribute("files",
                    StreamSupport.stream(stream.spliterator(), false)
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .collect(Collectors.toList()));
        }
        return "list-files";
    }

}
