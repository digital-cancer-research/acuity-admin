package com.acuity.visualisations.data.provider.azure;

import com.acuity.visualisations.data.provider.azure.properties.AzureStorageProperties;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.file.CloudFileClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Profile("azure-storage")
@Configuration
public class AzureStorageConfiguration {

    @Autowired
    private AzureStorageProperties properties;

    @SneakyThrows
    @Bean
    public CloudStorageAccount storageAccount() {
        return CloudStorageAccount.parse(properties.connectionString());
    }

    @Bean
    public CloudBlobClient blobClient(CloudStorageAccount storageAccount) {
        return storageAccount.createCloudBlobClient();
    }

    @Bean
    public CloudFileClient fileClient(CloudStorageAccount storageAccount) {
        return storageAccount.createCloudFileClient();
    }

    @Bean
    @Order(1)
    public AzureBlobStorageDataProvider blobStorageDataProvider(CloudBlobClient blobClient) {
        return new AzureBlobStorageDataProvider(blobClient);
    }

    @Bean
    @Order(0)
    public AzureFileStorageDataProvider fileStorageDataProvider(CloudFileClient fileClient) {
        return new AzureFileStorageDataProvider(fileClient);
    }

}
