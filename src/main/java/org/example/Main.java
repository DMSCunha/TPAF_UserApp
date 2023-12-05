package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import userappstubs.*;
import userappstubs.Void;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static int svcPort = 8500; public static String svcIp = "localhost";
    public static ManagedChannel channel;
    public static UserAppGrpc.UserAppBlockingStub blockingStub;
    public static UserAppGrpc.UserAppStub nonblockingStub;

    public static void main(String[] args) throws InterruptedException {

        channel = ManagedChannelBuilder.forAddress(svcIp,svcPort).usePlaintext().build();
        blockingStub = UserAppGrpc.newBlockingStub(channel);
        nonblockingStub = UserAppGrpc.newStub(channel);

        int option = 5;
        Scanner scan = new Scanner(System.in);
        while(option != 99){
            showMenu();
            option = Integer.parseInt(scan.nextLine());
            switch (option){
                case 0:

                    break;
                case 1:

                    break;
                case 2:
                    //"C:\\Users\\Diana\\Desktop\\CD\\TPAF\\result.jpg"
                    System.out.println("Path to save file\n");
                    String pathString = scan.nextLine();
                    Path path;
                    try{
                        path = Paths.get(pathString);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    nonblockingStub.getFile(
                            Void.newBuilder().build(),
                            new StreamObserverFile(path,pathString)
                    );

                    break;
                case 99:
                    System.out.println("Closing program...");
                    break;
                default:
                    System.out.println("Invalid value");
            }

        }

    }

    public static void showMenu(){
        System.out.println("\nMenu");
        System.out.println("\t0 - ");
        System.out.println("\t1 - ");
        System.out.println("\t2 - Get file");
    }
}