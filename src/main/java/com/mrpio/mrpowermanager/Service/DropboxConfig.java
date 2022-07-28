package com.mrpio.mrpowermanager.Service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private String dropboxAccessToken = "sl.BMQAm2Pm_OXjJjSDSled7BSE7yQ5"+"eQYbT_Tv-JkIwyRVCIo"+"MExZeu8PhbSTnVz0xS37SLeLVnp7ndYMkpQQRrHDbX2OM08pQ-MnuldBVCBWesIhynqz_efjZ4FF5ojDCatRp5w8M4M4";

    private String dropboxRefreshToken = "ghwZirufsZwAAAAAAA"+"AAAZz0FJA84rnt9j-3T5DVpB85tsLIFPWR-vqSmSyenZwr";

    private String dropboxAppKey = "nbtl6om7z"+"bz0m9k";

    private String dropboxAppSecret = "u03zp1gm"+"wl9qh99";

    public DbxClientV2 DropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("mrpowermanager").build();

        DbxCredential credentials = new DbxCredential(dropboxAccessToken, -1L, dropboxRefreshToken, dropboxAppKey, dropboxAppSecret);

        return new DbxClientV2(config, credentials);
    }

}