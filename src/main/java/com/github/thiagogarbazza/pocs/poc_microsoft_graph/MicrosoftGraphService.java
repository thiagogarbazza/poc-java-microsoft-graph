package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import com.microsoft.graph.serviceclient.GraphServiceClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MicrosoftGraphService {

    private final GraphServiceClient graphServiceClient;
    private final MicrosoftGraphFileService microsoftGraphFileService;
    private final MicrosoftGraphFilePermissionService microsoftGraphFilePermissionService;
    private final MicrosoftGraphFolderService microsoftGraphFolderService;
    private final Properties properties;

    public MicrosoftGraphService() {
        this.properties = new Properties();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("microsoft-graph.properties")) {
            properties.load(new InputStreamReader(inputStream, UTF_8));
        } catch (Exception e) {
            throw new MicrosoftGraphException("Erro ao ler o arquivo microsoft-graph.properties", e);
        }

        this.graphServiceClient = GraphServiceClientFactory.create(this.properties);
        this.microsoftGraphFolderService = new MicrosoftGraphFolderService(graphServiceClient);
        this.microsoftGraphFileService = new MicrosoftGraphFileService(graphServiceClient);
        this.microsoftGraphFilePermissionService = new MicrosoftGraphFilePermissionService(graphServiceClient);
    }

    private String getDriveId() {
        return graphServiceClient.sites()
                .bySiteId(this.properties.getProperty("microsoft-graph.site-id"))
                .drives()
                .get()
                .getValue()
                .getFirst()
                .getId();
    }

    public void addPermission(final String driveItemId, final String emailUsuario, final String mensagem) {
        final String driveId = getDriveId();

        microsoftGraphFilePermissionService.add(driveId, driveItemId, emailUsuario, mensagem);
    }

    public void removePermission(final String driveItemId, final String emailUsuario) {
        final String driveId = getDriveId();

        microsoftGraphFilePermissionService.remove(driveId, driveItemId, emailUsuario);
    }

    public String createFile(final MicrosoftGraphFileCreateDTO microsoftGraphFileCreateDTO) {
        final String driveId = getDriveId();
        final String folderId = microsoftGraphFolderService.get(driveId, microsoftGraphFileCreateDTO.getPath());

        return microsoftGraphFileService.create(driveId, folderId, microsoftGraphFileCreateDTO);
    }

    public void deleteFile(final String driveItemId) {
        final String driveId = getDriveId();

        microsoftGraphFileService.delete(driveId, driveItemId);
    }

    public MicrosoftGraphFileDownloadDTO downloadFile(final String driveItemId) {
        final String driveId = getDriveId();

        return microsoftGraphFileService.download(driveId, driveItemId);
    }
}
