package com.demo.grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServerImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String result = "Hello " + firstName;
        //create the response
        GreetResponse greetResponse = GreetResponse.newBuilder()
                .setResult(result)
                .build();
        //send the response
        responseObserver.onNext(greetResponse);
        //complete RPC call
        responseObserver.onCompleted();
    }
}
