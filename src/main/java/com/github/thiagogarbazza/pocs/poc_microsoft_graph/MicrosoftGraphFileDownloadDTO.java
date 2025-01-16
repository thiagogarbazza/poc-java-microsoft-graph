package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class MicrosoftGraphFileDownloadDTO {

    private final String name;
    private final String mimetype;
    private final InputStream inputStream;
}
