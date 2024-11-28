package uz.medsu.controller;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.medsu.entity.Attachment;
import uz.medsu.repository.AttachmentRepository;
import uz.medsu.sevice.serviceImpl.UploadService;
import uz.medsu.utils.ResponseMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/image")
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/{id}/drug")
    public ResponseEntity<ResponseMessage> uploadDrug(@RequestPart(value = "file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(uploadService.uploadDrug(file, id));
    }

    @PostMapping("/{id}/user")
    public ResponseEntity<ResponseMessage> uploadUser(@RequestPart(value = "file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(uploadService.uploadUser(file, id));
    }

    @PostMapping("/{id}/article")
    public ResponseEntity<ResponseMessage> uploadArticle(@RequestPart(value = "file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(uploadService.uploadArticle(file, id));
    }
}

