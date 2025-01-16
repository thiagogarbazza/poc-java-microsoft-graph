package com.github.thiagogarbazza.pocs;

import com.github.thiagogarbazza.pocs.poc_microsoft_graph.MicrosoftGraphFileCreateDTO;
import com.github.thiagogarbazza.pocs.poc_microsoft_graph.MicrosoftGraphFileDownloadDTO;
import com.github.thiagogarbazza.pocs.poc_microsoft_graph.MicrosoftGraphService;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Application {

    public static void main(String[] args) throws URISyntaxException, FileNotFoundException {
        final String emailUser = "alguem@my-company.com";
        final MicrosoftGraphService microsoftGraphService = new MicrosoftGraphService();

        LocalDateTime now = LocalDateTime.now();
        final File file = new File(Thread.currentThread().getContextClassLoader().getResource("example.txt").toURI());
        final String driveItemId = microsoftGraphService.createFile(MicrosoftGraphFileCreateDTO.builder()
                .path("/test/2024/12")
                .mimeType("text/plain")
                .name(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".txt")
                .lenght(file.length())
                .inputStream(new FileInputStream(file))
                .build());

        microsoftGraphService.addPermission(driveItemId, emailUser, "${message-to-user}");

        microsoftGraphService.removePermission(driveItemId, emailUser);

        final MicrosoftGraphFileDownloadDTO microsoftGraphFileDownloadDTO = microsoftGraphService.downloadFile(driveItemId);
        saveFile(microsoftGraphFileDownloadDTO);

        microsoftGraphService.deleteFile(driveItemId);
    }

    private static void saveFile(final MicrosoftGraphFileDownloadDTO microsoftGraphFileDownloadDTO) {
        final String pathtxt = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        final Path path = new File(pathtxt + microsoftGraphFileDownloadDTO.getName()).toPath();

        try (final InputStream inputStream = microsoftGraphFileDownloadDTO.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao realizar download do arquivo.", e);
        }
    }

}
