package com.mrpio.mrpowermanager.Service;

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
    final static String ENDPOINT_UPLOAD = "https://content.dropboxapi.com/2/files/upload";
    final static String ENDPOINT_DOWNLOAD = "https://content.dropboxapi.com/2/files/download";
    final static String ENDPOINT_LIST_FOLDER = "    https://api.dropboxapi.com/2/files/list_folder";
    private static final int BUFFER_SIZE = 1024;

    private static String getToken(){
        String first="sl.BMSiFWQA5SMrm38m2zToeQYPRix0eQicGtmEHEl-KntBbcPO5zJqnXVpX7TNrBKC0voheNA1bysWc65TSs01Jj";
        String second="kD2UPsZli3nWLt5loTnNVMg2nT6JnNznK_6H342f"+String.valueOf(4);
        return first+"-ssdiFFVE35-"+second;
    }

    /**
     * <strong>Method used to upload a file on dropbox cloud storage</strong>
     *
     * @param fileLocal an instance of {@link File} containing the location of local file to upload.
     * @param pathCloud the directory where to upload the file.
     * @return an instance of {@link JSONObject} containing the response of Dropbox api.
     * @see <a href="https://www.dropbox.com/developers/documentation/http/documentation#files-upload">Dropbox HTTP documentation</a>
     */
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

    /**
     * <strong>Method used to download a file on a provided path located on dropbox cloud storage </strong>
     *
     * @param pathCloud the directory where the file to download is located on cloud.
     * @param pathLocal the directory where to download the file.
     * @return a boolean value which notify if everything went fine.
     * @see <a href="https://www.dropbox.com/developers/documentation/http/documentation#files-download">Dropbox HTTP documentation</a>
     */
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

    /**
     * <strong>Method used to list the files inside a provided directory on cloud storage </strong>
     *
     * @param folderPath the directory on cloud to check
     * @return a boolean value which notify if everything went fine.
     * @see <a href="https://www.dropbox.com/developers/documentation/http/documentation#files-list_folder">Dropbox HTTP documentation</a>
     */
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
    }
}