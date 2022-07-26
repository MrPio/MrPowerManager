package com.mrpio.mrpowermanager;

import com.dropbox.core.DbxException;
import com.mrpio.mrpowermanager.Service.DropboxApi;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.time.temporal.ChronoUnit.MINUTES;

class DropboxApiTest {
    final String desktopPath = System.getProperty("user.home") + "/Desktop/";

    /**
     * Try to upload a file named "file.jpg" located on desktop.
     */
    @Test
    void uploadFile() throws DbxException {
        DropboxApi.uploadFile(
                desktopPath + "test.txt",
                "/database/test.txt");
    }

    /**
     * Try to download a file named "file.jpg" inside the desktop folder.
     */
    @Test
    void downloadFile() throws DbxException {
        DropboxApi.downloadFile(
                "/database/test.txt",
                desktopPath + "test2.txt");
    }
    @Test
    void dateTest(){
        var now = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(10);
        System.out.println(MINUTES.between(LocalDateTime.now(ZoneOffset.UTC),now));
    }
}
