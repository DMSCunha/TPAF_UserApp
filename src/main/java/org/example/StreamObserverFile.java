package org.example;

import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Path;
import io.grpc.stub.StreamObserver;
import userappstubs.file;

public class StreamObserverFile implements StreamObserver<file> {

    //private final Path path;
    private String pathString;
    public StreamObserverFile(String pathString){
        this.pathString = pathString;
        //this.path = path;
    }


    @Override
    public void onNext(file file) {
        String d = file.getDataChunk().toStringUtf8();

        try (FileOutputStream fos = new FileOutputStream(pathString)){
            //Files.write(path,file.toByteArray());
            //fos.write(file.toByteArray());
            fos.write(d.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        System.out.println("Stream completed!");
    }
}
