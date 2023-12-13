package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import userappstubs.*;
import userappstubs.Void;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    //public static int svcPort = 8500; public static String svcIp = "localhost";
    public static int svcPort; public static String svcIp;
    public static ManagedChannel channel;
    public static UserAppGrpc.UserAppBlockingStub blockingStub;
    public static UserAppGrpc.UserAppStub nonblockingStub;


    public static ack ack_resume_request = ack.newBuilder().setStatus(false).build();
    public static ack ack_resume_is_done = ack.newBuilder().setStatus(false).build();

    public static void main(String[] args) throws InterruptedException {

        //.. svcIP:svcPort
        if(args.length == 1){
            svcIp = setSvcIp(args[0]);
            svcPort = setSvcPort(args[0]);
        }else {
            System.out.println("Insufficient arguments");
            System.exit(0);
        }

        channel = ManagedChannelBuilder.forAddress(svcIp,svcPort).usePlaintext().build();
        blockingStub = UserAppGrpc.newBlockingStub(channel);
        nonblockingStub = UserAppGrpc.newStub(channel);

        String name = readline("User name:");



        int option = 5;
        Scanner scan = new Scanner(System.in);
        while(option != 99){
            showMenu();
            option = Integer.parseInt(scan.nextLine());
            switch (option){
                case 0:
                    System.out.println("Sending resume request...");

                    ack_resume_request = blockingStub.resume(askResume.newBuilder()
                            .setUserName(name)
                            .setResumeType("CASA")
                            .build());

                    System.out.println("Resume request send.");

                    System.out.println("Waiting for resume...");


                    while (!ack_resume_is_done.getStatus()) {
                        ack_resume_is_done = blockingStub.isDone(askResume.newBuilder()
                                .setUserName(name)
                                .setResumeType("CASA")
                                .build());
                        Thread.sleep(200);
                        System.out.println("Resume is " + ack_resume_is_done.getStatus());
                    }

                    System.out.println("Path to save file:");
                    String pathStringCasa = scan.nextLine();

                    nonblockingStub.getFile(askResume.newBuilder()
                                    .setUserName(name)
                                    .setResumeType("CASA")
                                    .build(),
                                    new StreamObserverFile(pathStringCasa));
                    break;

                case 1:

                    System.out.println("Sending resume request to alimentar...");

                    ack_resume_request = blockingStub.resume(askResume.newBuilder()
                            .setUserName(name)
                            .setResumeType("ALIMENTAR")
                            .build());

                    System.out.println("Request send.");

                    System.out.println("Waiting for resume...");

                    //enquanto o ack do resume is ready == false vai tentando ate ser true
                    while (!ack_resume_is_done.getStatus()) {
                        ack_resume_is_done = blockingStub.isDone(askResume.newBuilder()
                                .setUserName(name)
                                .setResumeType("ALIMENTAR")
                                .build());
                        Thread.sleep(200);
                        System.out.println("Resume is " + ack_resume_is_done.getStatus());
                    }

                    System.out.println("Path to save file\n");
                    String pathStringAlimentar = scan.nextLine();


                    nonblockingStub.getFile(askResume.newBuilder()
                                    .setUserName(name)
                                    .setResumeType("ALIMENTAR")
                                    .build(),
                                    new StreamObserverFile(pathStringAlimentar));

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
        System.out.println("\t0 - Get CASA resume");
        System.out.println("\t1 - Get ALIMENTAR resume");
    }

    private static String readline(String msg) {
        Scanner scaninput = new Scanner(System.in);
        System.out.println(msg);
        return scaninput.nextLine();
    }


    public static String setSvcIp(String daemonIP){
        return daemonIP.substring(0,daemonIP.indexOf(":"));
    }

    public static int setSvcPort(String daemonPort) {
        return Integer.parseInt(daemonPort.substring(daemonPort.indexOf(":")+1));
    }

}