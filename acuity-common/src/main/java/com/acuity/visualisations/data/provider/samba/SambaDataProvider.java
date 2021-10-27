package com.acuity.visualisations.data.provider.samba;

import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.SmbData;
import com.acuity.visualisations.data.provider.DataProvider;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile("smb-storage")
@Order(2)
public class SambaDataProvider implements DataProvider {

    private static final String SMB_PROTO_PREFIX = "smb://";

    static {
        jcifs.Config.setProperty("jcifs.smb.client.responseTimeout", "750000");
        jcifs.Config.setProperty("jcifs.smb.client.soTimeout", "800000");
    }

    @Value("${smb-storage.client.domain}")
    private String domain;
    @Value("${smb-storage.client.username}")
    private String username;
    @Value("${smb-storage.client.password}")
    private String password;

    private NtlmPasswordAuthentication defaultAuth;

    @PostConstruct
    private void init() {
        this.defaultAuth = new NtlmPasswordAuthentication(domain, username, password);
    }

    @Override
    public boolean match(@NonNull String location) {
        return convertWindowsPathToSmb(location).startsWith(SMB_PROTO_PREFIX);
    }

    @Override
    @SneakyThrows
    public Data get(@NonNull String location) {
        location = convertWindowsPathToSmb(location);

        SmbFile file = new SmbFile(location);

        if ("GUEST".equals(file.getPrincipal().getName())) {
            file = new SmbFile(location, defaultAuth);
        }

        return new SmbData(file);
    }

    private String convertWindowsPathToSmb(String fullPathToFile) {
        String res = fullPathToFile.replace("\\", "/");
        if (!res.startsWith(SMB_PROTO_PREFIX) && res.startsWith("//")) {
            res = "smb:".concat(res);
        }
        return res;
    }
}
