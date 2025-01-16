package com.github.thiagogarbazza.pocs.poc_microsoft_graph;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.experimental.UtilityClass;

import java.util.Properties;

@UtilityClass
class GraphServiceClientFactory {

    public static GraphServiceClient create(final Properties properties) {
        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
            .tenantId(properties.getProperty("microsoft-graph.tenant-id"))
            .clientId(properties.getProperty("microsoft-graph.client-id"))
            .clientSecret(properties.getProperty("microsoft-graph.client-secret"))
            .build();

        return new GraphServiceClient(clientSecretCredential, ".default");
    }
}
