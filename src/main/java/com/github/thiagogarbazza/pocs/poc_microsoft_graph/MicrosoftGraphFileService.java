package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import com.microsoft.graph.core.models.UploadResult;
import com.microsoft.graph.core.tasks.LargeFileUploadTask;
import com.microsoft.graph.drives.item.items.item.createuploadsession.CreateUploadSessionPostRequestBody;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

import static java.text.MessageFormat.format;

@CommonsLog
@RequiredArgsConstructor
class MicrosoftGraphFileService {

    private final GraphServiceClient graphServiceClient;

    public void delete(final String driveId, final String driveItemId) {
        graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .delete();
    }

    public MicrosoftGraphFileDownloadDTO download(final String driveId, final String driveItemId) {
        final DriveItem driveItem = graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .get();

        final InputStream inputStream = graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .content()
                .get();

        return MicrosoftGraphFileDownloadDTO.builder()
                .name(driveItem.getName())
                .mimetype(driveItem.getFile().getMimeType())
                .inputStream(inputStream)
                .build();
    }

    public String create(final String driveId, final String parentDriveItemId, final MicrosoftGraphFileCreateDTO microsoftGraphFileCreateDTO) {
        final DriveItem driveItem = graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(parentDriveItemId)
                .children()
                .post(DriveItemFactory.createAFile(microsoftGraphFileCreateDTO));

        try {
            upload(driveId, driveItem.getId(), microsoftGraphFileCreateDTO);
        } catch (Exception e) {
            delete(driveId, driveItem.getId());
            throw new MicrosoftGraphException(format("Could not create item {0}", microsoftGraphFileCreateDTO.getName()), e);
        }

        return driveItem.getId();
    }

    private void upload(final String driveId, final String driveItemId, final MicrosoftGraphFileCreateDTO microsoftGraphFileCreateDTO) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InterruptedException {
        // Set body of the upload session request
        // This is used to populate the request to create an upload session
        final DriveItemUploadableProperties driveItemUploadableProperties = new DriveItemUploadableProperties();
        driveItemUploadableProperties.getAdditionalData().put("@microsoft.graph.conflictBehavior", "replace");

        // Finish setting up the request body
        final CreateUploadSessionPostRequestBody uploadSessionPostRequestBody = new CreateUploadSessionPostRequestBody();
        uploadSessionPostRequestBody.setItem(driveItemUploadableProperties);

        // Create the upload session
        final UploadSession uploadSession = graphServiceClient.drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(driveItemId)
                .createUploadSession().post(uploadSessionPostRequestBody);

        if (Objects.isNull(uploadSession)) {
            throw new MicrosoftGraphException(format("Could not create upload session for item {0}", driveItemId));
        }

        final LargeFileUploadTask largeFileUploadTask = new LargeFileUploadTask(
                graphServiceClient.getRequestAdapter(),
                uploadSession,
                microsoftGraphFileCreateDTO.getInputStream(),
                microsoftGraphFileCreateDTO.getLenght(),
                DriveItem::createFromDiscriminatorValue);

        if (Objects.isNull(largeFileUploadTask)) {
            throw new MicrosoftGraphException(format("Could not create upload task for item {0}", driveItemId));
        }

        // Do the upload
        final UploadResult uploadResult = largeFileUploadTask.upload(20, (final long current, final long max) ->
                log.debug(format("Upload report {0}:{1}", current, max)));
        if (Optional.ofNullable(uploadResult).map(UploadResult::isUploadSuccessful).orElse(false)) {
            log.debug(format("Uploaded successfully {0}", driveItemId));
        } else {
            throw new MicrosoftGraphException(format("Wasn´t uploaded successfully item {0}", driveItemId));
        }
    }
}
