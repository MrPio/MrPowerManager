package com.mrpio.mrpowermanager.Service;

import java.io.*;

public class Serialization {
    private String path = "";
    private final String fileName;

    public Serialization(String fileName) {
        this.fileName = fileName;
    }

    public Serialization(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullPath() {
        return path + fileName;
    }

    public Object loadObject() {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(path + fileName)));) {
            return in.readObject();
        } catch (IOException e) {
            return ".dat not found!";
        } catch (ClassNotFoundException e) {
            System.err.println("cannot load the object!");
        }
        return null;
    }

    public <T extends Serializable> void saveObject(T obj) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(path + fileName)))) {
            out.writeObject(obj);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public boolean existFile() {
        File file = new File(path + fileName);
        return file.exists();
    }
}
