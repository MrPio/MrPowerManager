package com.mrpio.mrpowermanager;

import com.mrpio.mrpowermanager.Service.DropboxApi;
import org.junit.jupiter.api.Test;

import java.io.File;

class DropboxApiTest {
    final String desktopPath = System.getProperty("user.home") + "/Desktop/";

    /**
     * Try to upload a file named "file.jpg" located on desktop.
     */
    @Test
    void uploadFile() {
        assert DropboxApi.uploadFile(
                new File(desktopPath + "invitation_code.txt"),
                "\\database\\").toString().contains("invitation_code.txt");
    }

    /**
     * Try to download a file named "file.jpg" inside the desktop folder.
     */
    @Test
    void downloadFile() {
        assert DropboxApi.downloadFile(
                "\\database\\invitation_code.txt",
                desktopPath + "code.txt");
    }
}
