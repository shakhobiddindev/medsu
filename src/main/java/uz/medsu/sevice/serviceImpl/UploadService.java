package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.medsu.entity.Article;
import uz.medsu.entity.Attachment;
import uz.medsu.entity.Drug;
import uz.medsu.entity.User;
import uz.medsu.enums.Roles;
import uz.medsu.repository.ArticleRepository;
import uz.medsu.repository.AttachmentRepository;
import uz.medsu.repository.DrugRepository;
import uz.medsu.repository.UserRepository;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final DrugRepository drugRepository;
    private final ArticleRepository articleRepository;
    @Value("${my_var.file_path}")
    private String mainPath;
    @Value("${my_var.appliation_url}")
    private String url;


    public ResponseMessage uploadDrug(MultipartFile file, Long id) {
        Drug drug = drugRepository.findById(id).orElseThrow();
        if (!Util.getCurrentUser().getProfession().equals(Roles.ADMIN)) throw new RuntimeException("You cannot change it!");
        System.out.println(drug);
        if (drug.getImageUrl() != null && !drug.getImageUrl().isBlank()) deleteFile(drug.getImageUrl());
        Attachment uploading = uploading(file);
        drug.setImageUrl(uploading.getUrl());
        drugRepository.save(drug);
        return ResponseMessage.builder().success(true).message("File uploaded successfully!").build();
    }

    public ResponseMessage uploadUser(MultipartFile file, Long id) {
        if (!Util.getCurrentUser().getId().equals(id)) throw new RuntimeException("You cannot change it!");
        User user = Util.getCurrentUser();
        if (user.getImageUrl() != null ) deleteFile(user.getImageUrl());
        Attachment uploading = uploading(file);
        user.setImageUrl(uploading.getUrl());
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message("File uploaded successfully!").build();
    }


    public ResponseMessage uploadArticle(MultipartFile file, Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("articleNotFound")));
        if (!article.getAuthor().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException("You cannot change it!");
        if (article.getImageUrl() != null) deleteFile(article.getImageUrl());
        Attachment uploading = uploading(file);
        article.setImageUrl(uploading.getUrl());
        articleRepository.save(article);
        return ResponseMessage.builder().success(true).message("File uploaded successfully!").build();
    }


    @SneakyThrows
    private Attachment uploading(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String id = UUID.randomUUID().toString();
        String fileName = mainPath + id + originalFilename.substring(originalFilename.lastIndexOf("."));

        // Ensure the directory exists
        Path path = Path.of(mainPath);
        Files.createDirectories(path);

        // Define the full path for the file
        Path filePath = Path.of(fileName);
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Attachment attachment = Attachment
                .builder()
                .id(id)
                .fullyName(fileName)
                .originalName(originalFilename)
                .size(multipartFile.getSize())
                .url(url+fileName)
                .build();

        attachmentRepository.save(attachment);
        return attachment;
    }

    @SneakyThrows
    private void deleteFile(String customPath) {
        Path path = Paths.get(customPath);
        Files.delete(path);
        Attachment attachment = attachmentRepository.findByFullyName(customPath).orElseThrow(RuntimeException::new);
        attachmentRepository.delete(attachment);
    }
}
