package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.File;
import com.microsoft.graph.models.Folder;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
class DriveItemFactory {

    public static DriveItem createAFolder(final String name) {
        final DriveItem driveItem = new DriveItem();
        driveItem.setName(name);

        final Folder folder = new Folder();
        driveItem.setFolder(folder);

        final Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("@microsoft.graph.conflictBehavior", "fail");
        driveItem.setAdditionalData(additionalData);

        return driveItem;
    }

    public static DriveItem createAFile(final MicrosoftGraphFileCreateDTO microsoftGraphFileCreateDTO) {
        final DriveItem driveItem = new DriveItem();
        driveItem.setName(microsoftGraphFileCreateDTO.getName());

        final File file = new File();
        file.setMimeType(microsoftGraphFileCreateDTO.getMimeType());
        driveItem.setFile(file);

        final Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("@microsoft.graph.conflictBehavior", "fail");
        driveItem.setAdditionalData(additionalData);

        return driveItem;
    }
}
