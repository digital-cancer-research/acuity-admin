package com.acuity.visualisations.data.provider.azure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties("azure.storage")
@Profile("azure-storage")
public class AzureStorageProperties {
    private String protocol;
    private String account;
    private String key;

    public String connectionString() {
        return String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s", protocol, account, key);
    }

}
