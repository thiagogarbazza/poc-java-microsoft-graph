package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
class MicrosoftGraphFolderService {

    private final GraphServiceClient graphServiceClient;

    public String get(String driveId, final String path) {
        String driveItemId = graphServiceClient.drives().byDriveId(driveId).root().get().getId();
        for (final String name : path.replaceAll("^/", "").split("/")) {
            driveItemId = getOrCreate(driveId, driveItemId, name).getId();
        }

        return driveItemId;
    }

    private DriveItem getOrCreate(final String driveId, final String parentDriveItemId, final String name) {
        final List<DriveItem> items = graphServiceClient
                .drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(parentDriveItemId)
                .children()
                .get()
                .getValue();

        return items.stream()
                .filter(i -> Objects.nonNull(i.getFolder()))
                .filter(i -> i.getName().equals(name))
                .findFirst()
                .orElseGet(() -> create(driveId, parentDriveItemId, name));
    }

    private DriveItem create(final String driveId, final String parentDriveItemId, final String name) {
        return graphServiceClient.drives()
                .byDriveId(driveId)
                .items()
                .byDriveItemId(parentDriveItemId)
                .children()
                .post(DriveItemFactory.createAFolder(name));
    }
}
