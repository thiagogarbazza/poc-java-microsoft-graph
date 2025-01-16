package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@Getter
@Builder
@RequiredArgsConstructor
public class MicrosoftGraphFileCreateDTO {

    final String path;
    final String mimeType;
    final String name;
    final long lenght;
    final InputStream inputStream;
}
