package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class TCPServer {
    private final ChannelInitializer<SocketChannel> initializer;
    private EventLoopGroup worker, boss;
    private Channel channel;

    public TCPServer(ChannelInitializer<SocketChannel> initializer) {
        this.initializer = initializer;
    }

    public void start(InetSocketAddress address, int workerThreads) throws InterruptedException {
        if (channel != null && channel.isOpen()) throw new RuntimeException("Is already running");

        try {
            ChannelFuture result;

            if (Epoll.isAvailable()) {
                this.boss = new EpollEventLoopGroup(1);
                this.worker = new EpollEventLoopGroup(workerThreads);
                result = new ServerBootstrap()
                        .group(boss, worker)
                        .channel(EpollServerSocketChannel.class)
                        .childHandler(initializer)
                        .bind(address).sync();
            } else {
                this.boss = new NioEventLoopGroup(1);
                this.worker = new NioEventLoopGroup(workerThreads);
                result = new ServerBootstrap()
                        .group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(initializer)
                        .bind(address).sync();
            }

            if (result.isSuccess()) {
                this.channel = result.channel();
            } else {
                throw new RuntimeException(result.cause());
            }

        } catch (RuntimeException e) {
            this.boss.shutdownGracefully();
            this.worker.shutdownGracefully();
            throw e;
        }
    }

    public void stop() throws InterruptedException {
        if (channel.isOpen()) channel.close().await();
        if (!boss.isShutdown()) boss.shutdownGracefully().await();
        if (!worker.isShutdown()) worker.shutdownGracefully().await();
    }

    public void addLastToChild(Channel channel, ChannelHandler h) {
        channel.pipeline().addLast(h);
    }

    public void removeLastFromChild(Channel channel) {
        channel.pipeline().removeLast();
    }

    public ChannelFuture closeChild(Channel channel) {
        if (channel.isActive()) {
            return channel.close();
        }

        return channel.newSucceededFuture();
    }
}
