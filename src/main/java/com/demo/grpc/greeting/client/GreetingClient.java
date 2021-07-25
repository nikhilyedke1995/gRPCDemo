package com.demo.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    ManagedChannel channel;


    public static void main(String[] args) {
        System.out.println("Hello I'm grpc client");
        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

    private void run(){
        channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();
        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        //doClientStreamingCall(channel);
        doBiDirectionalStreamingCall(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        //Unary Call starts
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Nikhil")
                .setLastName("YEDKE")
                .build();
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting).build();
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());
        //Unary call ends
    }

    private void doServerStreamingCall(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        //server streaming
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("NIKHIL").build()).build();
        greetClient.greetManyTimes(request)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }


    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                //we get response from server
                System.out.println("Received response from the server");
                System.out.println(longGreetResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                //we get an error from the server
            }

            @Override
            public void onCompleted() {
                //the server is done sending us data
                System.out.println("Server has completed sending us something");
                //onCompleted will be called right after onNext()
                latch.countDown();
            }
        });

        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("NIKHIL").build())
        .build());

        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("PRIYA").build())
                .build());

        System.out.println("Sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("SHUBHAM").build())
                .build());
        //we tell the server that client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDirectionalStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<GreetEveryoneRequest> requestStreamObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse greetEveryoneResponse) {
                System.out.println("Response From server " + greetEveryoneResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList("NIKHIL","PRIYA","SHUBHAM").forEach(
                name ->{
                    System.out.println("sending " + name);
                    requestStreamObserver.onNext(GreetEveryoneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(name)
                                    .build())
                            .build());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
