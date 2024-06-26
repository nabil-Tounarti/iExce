package com.example.ipfsdemon;

import io.ipfs.api.IPFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IPFSConfig {
    IPFS ipfs;

    public IPFSConfig() {
        ipfs = new IPFS("ipfs-daemon", 5001);
    }

}
