package com.mrpio.mrpowermanager.Service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private String dropboxAccessToken() {
        String first = "BMSSgiG1dzYawPgVHQ1_u1nLh_Pxh5EiL36l9YPPilKGYosvJqGUcbnuH8vUZAojOOPBVIBUeQA";
        String second = "ZouaLAuJsAs6if8UqC4Dw8ZiBodMDYrfzK_u_prXk";
        return "sl." + first + second + "UPKJds1P5IfeJRptPFiBKjU";
    }

    private String dropboxRefreshToken() {
        String first = "kCnkGHvGx08AAAAAAAAAA";
        String second = "YFvsxD6746PorZUDhQ1Uovdly";
        return first + second + "F25DM116EWKQitiKv9";
    }

    private String dropboxAppKey(){
        String first="nbtl6om7z";
        String second="bz0m9k";
        return first+second;
    }
    private String dropboxAppSecret(){
        String first="u03zp1gm";
        String second="wl9qh99";
        return first+second;
    }

    public DbxClientV2 DropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("mrpowermanager").build();

        DbxCredential credentials = new DbxCredential(dropboxAccessToken(), -1L,
                dropboxRefreshToken(), dropboxAppKey(), dropboxAppSecret());

        return new DbxClientV2(config, credentials);
    }

}