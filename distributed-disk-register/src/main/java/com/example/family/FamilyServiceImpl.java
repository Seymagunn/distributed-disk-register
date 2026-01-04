package com.example.family;

import family.Empty;
import family.FamilyServiceGrpc;
import family.FamilyView;
import family.NodeInfo;
import family.StoreRequest;
import family.StoreResponse;
import family.FetchRequest;
import family.FetchResponse;

import io.grpc.stub.StreamObserver;

public class FamilyServiceImpl extends FamilyServiceGrpc.FamilyServiceImplBase {

    private final NodeRegistry registry;
    private final NodeInfo self;
    private final DiskStore diskStore;

    public FamilyServiceImpl(NodeRegistry registry, NodeInfo self) {
        this.registry = registry;
        this.self = self;
        this.diskStore = new DiskStore(self.getPort());
        this.registry.add(self);
    }

    @Override
    public void join(NodeInfo request, StreamObserver<FamilyView> responseObserver) {
        registry.add(request);

        FamilyView view = FamilyView.newBuilder()
                .addAllMembers(registry.snapshot())
                .build();

        responseObserver.onNext(view);
        responseObserver.onCompleted();
    }

    @Override
    public void getFamily(Empty request, StreamObserver<FamilyView> responseObserver) {
        FamilyView view = FamilyView.newBuilder()
                .addAllMembers(registry.snapshot())
                .build();

        responseObserver.onNext(view);
        responseObserver.onCompleted();
    }

    @Override
    public void storeMessage(StoreRequest request,
                             StreamObserver<StoreResponse> responseObserver) {

        int messageId = request.getMessage().getMessageId();
        String text = request.getMessage().getText();

        diskStore.save(messageId, text);
        registry.registerMessage(messageId, self);

        responseObserver.onNext(
                StoreResponse.newBuilder()
                        .setSuccess(true)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void fetchMessage(FetchRequest request,
                             StreamObserver<FetchResponse> responseObserver) {

        int messageId = request.getMessageId();
        String text = diskStore.load(messageId);

        if (text == null) {
            responseObserver.onNext(
                    FetchResponse.newBuilder()
                            .setFound(false)
                            .build()
            );
        } else {
            responseObserver.onNext(
                    FetchResponse.newBuilder()
                            .setFound(true)
                            .setMessage(
                                    family.ChatMessage.newBuilder()
                                            .setMessageId(messageId)
                                            .setText(text)
                                            .build()
                            )
            );
        }

        responseObserver.onCompleted();
    }
}