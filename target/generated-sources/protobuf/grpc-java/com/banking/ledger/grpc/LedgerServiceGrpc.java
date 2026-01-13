package com.banking.ledger.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.60.0)",
    comments = "Source: ledger.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class LedgerServiceGrpc {

  private LedgerServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.banking.ledger.LedgerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.banking.ledger.grpc.CreateAccountRequest,
      com.banking.ledger.grpc.AccountResponse> getCreateAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateAccount",
      requestType = com.banking.ledger.grpc.CreateAccountRequest.class,
      responseType = com.banking.ledger.grpc.AccountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.banking.ledger.grpc.CreateAccountRequest,
      com.banking.ledger.grpc.AccountResponse> getCreateAccountMethod() {
    io.grpc.MethodDescriptor<com.banking.ledger.grpc.CreateAccountRequest, com.banking.ledger.grpc.AccountResponse> getCreateAccountMethod;
    if ((getCreateAccountMethod = LedgerServiceGrpc.getCreateAccountMethod) == null) {
      synchronized (LedgerServiceGrpc.class) {
        if ((getCreateAccountMethod = LedgerServiceGrpc.getCreateAccountMethod) == null) {
          LedgerServiceGrpc.getCreateAccountMethod = getCreateAccountMethod =
              io.grpc.MethodDescriptor.<com.banking.ledger.grpc.CreateAccountRequest, com.banking.ledger.grpc.AccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.CreateAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.AccountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LedgerServiceMethodDescriptorSupplier("CreateAccount"))
              .build();
        }
      }
    }
    return getCreateAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.banking.ledger.grpc.GetBalanceRequest,
      com.banking.ledger.grpc.BalanceResponse> getGetBalanceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBalance",
      requestType = com.banking.ledger.grpc.GetBalanceRequest.class,
      responseType = com.banking.ledger.grpc.BalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.banking.ledger.grpc.GetBalanceRequest,
      com.banking.ledger.grpc.BalanceResponse> getGetBalanceMethod() {
    io.grpc.MethodDescriptor<com.banking.ledger.grpc.GetBalanceRequest, com.banking.ledger.grpc.BalanceResponse> getGetBalanceMethod;
    if ((getGetBalanceMethod = LedgerServiceGrpc.getGetBalanceMethod) == null) {
      synchronized (LedgerServiceGrpc.class) {
        if ((getGetBalanceMethod = LedgerServiceGrpc.getGetBalanceMethod) == null) {
          LedgerServiceGrpc.getGetBalanceMethod = getGetBalanceMethod =
              io.grpc.MethodDescriptor.<com.banking.ledger.grpc.GetBalanceRequest, com.banking.ledger.grpc.BalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetBalance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.GetBalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.BalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LedgerServiceMethodDescriptorSupplier("GetBalance"))
              .build();
        }
      }
    }
    return getGetBalanceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.banking.ledger.grpc.PostTransactionRequest,
      com.banking.ledger.grpc.TransactionResponse> getPostTransactionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PostTransaction",
      requestType = com.banking.ledger.grpc.PostTransactionRequest.class,
      responseType = com.banking.ledger.grpc.TransactionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.banking.ledger.grpc.PostTransactionRequest,
      com.banking.ledger.grpc.TransactionResponse> getPostTransactionMethod() {
    io.grpc.MethodDescriptor<com.banking.ledger.grpc.PostTransactionRequest, com.banking.ledger.grpc.TransactionResponse> getPostTransactionMethod;
    if ((getPostTransactionMethod = LedgerServiceGrpc.getPostTransactionMethod) == null) {
      synchronized (LedgerServiceGrpc.class) {
        if ((getPostTransactionMethod = LedgerServiceGrpc.getPostTransactionMethod) == null) {
          LedgerServiceGrpc.getPostTransactionMethod = getPostTransactionMethod =
              io.grpc.MethodDescriptor.<com.banking.ledger.grpc.PostTransactionRequest, com.banking.ledger.grpc.TransactionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PostTransaction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.PostTransactionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.TransactionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LedgerServiceMethodDescriptorSupplier("PostTransaction"))
              .build();
        }
      }
    }
    return getPostTransactionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.banking.ledger.grpc.GetHistoryRequest,
      com.banking.ledger.grpc.TransactionHistoryResponse> getGetTransactionHistoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTransactionHistory",
      requestType = com.banking.ledger.grpc.GetHistoryRequest.class,
      responseType = com.banking.ledger.grpc.TransactionHistoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.banking.ledger.grpc.GetHistoryRequest,
      com.banking.ledger.grpc.TransactionHistoryResponse> getGetTransactionHistoryMethod() {
    io.grpc.MethodDescriptor<com.banking.ledger.grpc.GetHistoryRequest, com.banking.ledger.grpc.TransactionHistoryResponse> getGetTransactionHistoryMethod;
    if ((getGetTransactionHistoryMethod = LedgerServiceGrpc.getGetTransactionHistoryMethod) == null) {
      synchronized (LedgerServiceGrpc.class) {
        if ((getGetTransactionHistoryMethod = LedgerServiceGrpc.getGetTransactionHistoryMethod) == null) {
          LedgerServiceGrpc.getGetTransactionHistoryMethod = getGetTransactionHistoryMethod =
              io.grpc.MethodDescriptor.<com.banking.ledger.grpc.GetHistoryRequest, com.banking.ledger.grpc.TransactionHistoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTransactionHistory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.GetHistoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.banking.ledger.grpc.TransactionHistoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LedgerServiceMethodDescriptorSupplier("GetTransactionHistory"))
              .build();
        }
      }
    }
    return getGetTransactionHistoryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LedgerServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LedgerServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LedgerServiceStub>() {
        @java.lang.Override
        public LedgerServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LedgerServiceStub(channel, callOptions);
        }
      };
    return LedgerServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LedgerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LedgerServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LedgerServiceBlockingStub>() {
        @java.lang.Override
        public LedgerServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LedgerServiceBlockingStub(channel, callOptions);
        }
      };
    return LedgerServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LedgerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LedgerServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LedgerServiceFutureStub>() {
        @java.lang.Override
        public LedgerServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LedgerServiceFutureStub(channel, callOptions);
        }
      };
    return LedgerServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Create a new account for a user
     * </pre>
     */
    default void createAccount(com.banking.ledger.grpc.CreateAccountRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.AccountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateAccountMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get current balance for an account
     * </pre>
     */
    default void getBalance(com.banking.ledger.grpc.GetBalanceRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.BalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetBalanceMethod(), responseObserver);
    }

    /**
     * <pre>
     * Post a new transaction (transfer, deposit, withdrawal)
     * </pre>
     */
    default void postTransaction(com.banking.ledger.grpc.PostTransactionRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.TransactionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPostTransactionMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get transaction history for an account
     * </pre>
     */
    default void getTransactionHistory(com.banking.ledger.grpc.GetHistoryRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.TransactionHistoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTransactionHistoryMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service LedgerService.
   */
  public static abstract class LedgerServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return LedgerServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service LedgerService.
   */
  public static final class LedgerServiceStub
      extends io.grpc.stub.AbstractAsyncStub<LedgerServiceStub> {
    private LedgerServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LedgerServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LedgerServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new account for a user
     * </pre>
     */
    public void createAccount(com.banking.ledger.grpc.CreateAccountRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.AccountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get current balance for an account
     * </pre>
     */
    public void getBalance(com.banking.ledger.grpc.GetBalanceRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.BalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetBalanceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Post a new transaction (transfer, deposit, withdrawal)
     * </pre>
     */
    public void postTransaction(com.banking.ledger.grpc.PostTransactionRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.TransactionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPostTransactionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get transaction history for an account
     * </pre>
     */
    public void getTransactionHistory(com.banking.ledger.grpc.GetHistoryRequest request,
        io.grpc.stub.StreamObserver<com.banking.ledger.grpc.TransactionHistoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTransactionHistoryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service LedgerService.
   */
  public static final class LedgerServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<LedgerServiceBlockingStub> {
    private LedgerServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LedgerServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LedgerServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new account for a user
     * </pre>
     */
    public com.banking.ledger.grpc.AccountResponse createAccount(com.banking.ledger.grpc.CreateAccountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateAccountMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get current balance for an account
     * </pre>
     */
    public com.banking.ledger.grpc.BalanceResponse getBalance(com.banking.ledger.grpc.GetBalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetBalanceMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Post a new transaction (transfer, deposit, withdrawal)
     * </pre>
     */
    public com.banking.ledger.grpc.TransactionResponse postTransaction(com.banking.ledger.grpc.PostTransactionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPostTransactionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get transaction history for an account
     * </pre>
     */
    public com.banking.ledger.grpc.TransactionHistoryResponse getTransactionHistory(com.banking.ledger.grpc.GetHistoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTransactionHistoryMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service LedgerService.
   */
  public static final class LedgerServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<LedgerServiceFutureStub> {
    private LedgerServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LedgerServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LedgerServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new account for a user
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.banking.ledger.grpc.AccountResponse> createAccount(
        com.banking.ledger.grpc.CreateAccountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateAccountMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get current balance for an account
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.banking.ledger.grpc.BalanceResponse> getBalance(
        com.banking.ledger.grpc.GetBalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetBalanceMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Post a new transaction (transfer, deposit, withdrawal)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.banking.ledger.grpc.TransactionResponse> postTransaction(
        com.banking.ledger.grpc.PostTransactionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPostTransactionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get transaction history for an account
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.banking.ledger.grpc.TransactionHistoryResponse> getTransactionHistory(
        com.banking.ledger.grpc.GetHistoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTransactionHistoryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_ACCOUNT = 0;
  private static final int METHODID_GET_BALANCE = 1;
  private static final int METHODID_POST_TRANSACTION = 2;
  private static final int METHODID_GET_TRANSACTION_HISTORY = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_ACCOUNT:
          serviceImpl.createAccount((com.banking.ledger.grpc.CreateAccountRequest) request,
              (io.grpc.stub.StreamObserver<com.banking.ledger.grpc.AccountResponse>) responseObserver);
          break;
        case METHODID_GET_BALANCE:
          serviceImpl.getBalance((com.banking.ledger.grpc.GetBalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.banking.ledger.grpc.BalanceResponse>) responseObserver);
          break;
        case METHODID_POST_TRANSACTION:
          serviceImpl.postTransaction((com.banking.ledger.grpc.PostTransactionRequest) request,
              (io.grpc.stub.StreamObserver<com.banking.ledger.grpc.TransactionResponse>) responseObserver);
          break;
        case METHODID_GET_TRANSACTION_HISTORY:
          serviceImpl.getTransactionHistory((com.banking.ledger.grpc.GetHistoryRequest) request,
              (io.grpc.stub.StreamObserver<com.banking.ledger.grpc.TransactionHistoryResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getCreateAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.banking.ledger.grpc.CreateAccountRequest,
              com.banking.ledger.grpc.AccountResponse>(
                service, METHODID_CREATE_ACCOUNT)))
        .addMethod(
          getGetBalanceMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.banking.ledger.grpc.GetBalanceRequest,
              com.banking.ledger.grpc.BalanceResponse>(
                service, METHODID_GET_BALANCE)))
        .addMethod(
          getPostTransactionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.banking.ledger.grpc.PostTransactionRequest,
              com.banking.ledger.grpc.TransactionResponse>(
                service, METHODID_POST_TRANSACTION)))
        .addMethod(
          getGetTransactionHistoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.banking.ledger.grpc.GetHistoryRequest,
              com.banking.ledger.grpc.TransactionHistoryResponse>(
                service, METHODID_GET_TRANSACTION_HISTORY)))
        .build();
  }

  private static abstract class LedgerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LedgerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.banking.ledger.grpc.LedgerProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LedgerService");
    }
  }

  private static final class LedgerServiceFileDescriptorSupplier
      extends LedgerServiceBaseDescriptorSupplier {
    LedgerServiceFileDescriptorSupplier() {}
  }

  private static final class LedgerServiceMethodDescriptorSupplier
      extends LedgerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    LedgerServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (LedgerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LedgerServiceFileDescriptorSupplier())
              .addMethod(getCreateAccountMethod())
              .addMethod(getGetBalanceMethod())
              .addMethod(getPostTransactionMethod())
              .addMethod(getGetTransactionHistoryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
