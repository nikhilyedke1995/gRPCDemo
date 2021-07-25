package com.demo.grpc.greeting.server;

import com.proto.greet.*;
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

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver){
        String firstName = request.getGreeting().getFirstName();

        for(int i=0; i<10; i++){
            String result = "Hello " + firstName + ", response number : " + i;
            GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {


        StreamObserver<LongGreetRequest> streamObserver = new StreamObserver<LongGreetRequest>() {
            String result = "";
            @Override
            public void onNext(LongGreetRequest longGreetRequest) {
                //clients send message
                result += "Hello " + longGreetRequest.getGreeting().getFirstName() + "!  ";
            }

            @Override
            public void onError(Throwable throwable) {
                //clients sends an error
            }

            @Override
            public void onCompleted() {
                //client is done
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                //this is when we want to return a response (ResponseObserver)
                responseObserver.onCompleted();
            }
        };
        return streamObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestStreamObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest greetEveryoneRequest) {
                String response = "Hello "  + greetEveryoneRequest.getGreeting().getFirstName();
                GreetEveryoneResponse everyoneResponse = GreetEveryoneResponse.newBuilder()
                        .setResult(response)
                        .build();

                responseObserver.onNext(everyoneResponse);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }
}
