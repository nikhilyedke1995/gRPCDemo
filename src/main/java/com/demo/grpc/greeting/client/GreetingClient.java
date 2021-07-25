package com.demo.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm grpc client");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Nikhil")
                .setLastName("YEDKE")
                .build();
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting).build();
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
