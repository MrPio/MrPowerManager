package com.mrpio.mrpowermanager.Service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.users.FullAccount;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DropboxApi {
/*    final static String ENDPOINT_UPLOAD = "https://content.dropboxapi.com/2/files/upload";
    final static String ENDPOINT_DOWNLOAD = "https://content.dropboxapi.com/2/files/download";
    final static String ENDPOINT_LIST_FOLDER = "    https://api.dropboxapi.com/2/files/list_folder";
    private static final int BUFFER_SIZE = 1024;


    public static JSONObject uploadFile(File fileLocal, String pathCloud) {
        JSONObject result = null;
        pathCloud = pathCloud.replace('\\', '/');
        HttpURLConnection conn = null;
        try {
            //send request
            conn = (HttpURLConnection) new URL(ENDPOINT_UPLOAD).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + getToken());
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"" + pathCloud + fileLocal.getName() +
                    "\",\"mode\": \"overwrite\",\"autorename\": false,\"mute\": false,\"strict_conflict\": false}");
            conn.setDoOutput(true);
            IOUtils.copy(new FileInputStream(fileLocal), conn.getOutputStream());

            //read json response
            StringBuilder data = new StringBuilder();
            String line = "";
            try (InputStream in = conn.getInputStream();) {
                BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                while ((line = buf.readLine()) != null)
                    data.append(line);
            }
            result = (JSONObject) JSONValue.parseWithException(data.toString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            try {
                result = (JSONObject) new JSONParser().parse("{\"result\":\"IOException\"}");
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static boolean downloadFile(String pathCloud, String pathLocal) {
        pathCloud = pathCloud.replace('\\', '/');
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(ENDPOINT_DOWNLOAD).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + getToken());
            conn.setRequestProperty("Dropbox-API-Arg", "{\"path\": \"" + pathCloud + "\"}");
            conn.setDoOutput(true);

            InputStream inputStream = conn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(pathLocal);
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static ArrayList<String> getFilesInFolder(String folderPath) {
        ArrayList<String> result = new ArrayList<>();
        folderPath = folderPath.replace('\\', '/');
        HttpURLConnection conn = null;
        try {
            //send request
            conn = (HttpURLConnection) new URL(ENDPOINT_LIST_FOLDER).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + getToken());
            conn.setDoOutput(true);

            //send json request
            String jsonBody = "{\r\n\"path\":\"" + folderPath + "\"\r\n}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            //read json response
            StringBuilder data = new StringBuilder();
            String line = "";
            try (InputStream in = conn.getInputStream();) {
                BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                while ((line = buf.readLine()) != null)
                    data.append(line);
            }
            JSONObject response = (JSONObject) JSONValue.parseWithException(data.toString());

            //get list of files from response
            JSONArray fileList = (JSONArray) response.get("entries");
            for (Object jo : fileList)
                result.add(((JSONObject) jo).get("name").toString());

        } catch (IOException | ParseException e) {
            if (e.getMessage().contains("Server returned HTTP response code: 409"))
                System.err.println("folder not found!");
            else
                e.printStackTrace();
        }
        return result;
    }*/



    public static ArrayList<String> getFilesInFolder(String path) {
        DbxClientV2 client =new DropboxConfig().DropboxClient();

        ListFolderResult result = null;
        try {
            result = client.files().listFolder(path);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        var list=new ArrayList<String>();
        while (true) {
            for (Metadata metadata : result.getEntries())
                list.add(metadata.getName());

            if (!result.getHasMore())
                break;
            try {
                result = client.files().listFolderContinue(result.getCursor());
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void uploadFile(String fileLocal, String pathCloud)  {
        DbxClientV2 client =new DropboxConfig().DropboxClient();

        try (InputStream in = new FileInputStream(fileLocal)) {
            client.files().uploadBuilder(pathCloud).withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String pathCloud, String fileLocal) {
        DbxClientV2 client =new DropboxConfig().DropboxClient();

        try (OutputStream out = new FileOutputStream(fileLocal)) {
            client.files().downloadBuilder(pathCloud)
                    .download(out);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String pathCloud) {
        DbxClientV2 client =new DropboxConfig().DropboxClient();
        try {
            client.files().deleteV2(pathCloud);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
}