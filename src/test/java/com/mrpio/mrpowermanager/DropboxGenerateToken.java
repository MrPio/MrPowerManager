package com.mrpio.mrpowermanager;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.IncludeGrantedScopes;
import com.dropbox.core.TokenAccessType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class DropboxGenerateToken {
    private static String dropboxAppKey(){
        String first="nbtl6om7z";
        String second="bz0m9k";
        return first+second;
    }
    private static String dropboxAppSecret(){
        String first="u03zp1gm";
        String second="wl9qh99";
        return first+second;
    }
    public static void main(String[] args) throws IOException, DbxException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("myexamplefolder").build();

        String dropboxAppKey = dropboxAppKey();
        String dropboxAppSecret = dropboxAppSecret();

        DbxAppInfo appInfo = new DbxAppInfo(dropboxAppKey, dropboxAppSecret);

        DbxAuthFinish authFinish = scopeAuthorize(appInfo, config);

        System.out.println("Authorization complete.");
        System.out.println("- User ID: " + authFinish.getUserId());
        System.out.println("- Account ID: " + authFinish.getAccountId());
        System.out.println("- Access Token: " + authFinish.getAccessToken());
        System.out.println("- Expires At: " + authFinish.getExpiresAt());
        System.out.println("- Refresh Token: " + authFinish.getRefreshToken());
        System.out.println("- Scope: " + authFinish.getScope());
    }

    private static DbxAuthFinish scopeAuthorize(DbxAppInfo appInfo, DbxRequestConfig requestConfig) throws IOException, DbxException {
        DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);

        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withNoRedirect()
                .withTokenAccessType(TokenAccessType.OFFLINE)
                // Define here the scopes that you need in your application - and the app created in Dropbox has
                .withScope(Arrays.asList("files.content.read", "files.content.write"))
                .withIncludeGrantedScopes(IncludeGrantedScopes.USER)
                .build();

        String authorizeUrl = webAuth.authorize(webAuthRequest);
        System.out.println("1. Go to " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first).");
        System.out.println("3. Copy the authorization code.");
        System.out.print("Enter the authorization code here: ");

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (code == null) {
            System.exit(1);
        }
        code = code.trim();

        DbxAuthFinish authFinish = webAuth.finishFromCode(code);

        System.out.println("Successfully requested scope " + authFinish.getScope());
        return authFinish;
    }

}