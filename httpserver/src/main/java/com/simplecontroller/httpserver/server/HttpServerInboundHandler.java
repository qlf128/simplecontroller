package com.simplecontroller.httpserver.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by lianfei.qu on 2017/12/4.
 */
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if(msg instanceof HttpRequest){
            request = (HttpRequest) msg;

            String uri = request.getUri();
            log.info("Uri:"+uri);
        }

        if(msg instanceof HttpContent){
            HttpContent content = (HttpContent)msg;
            ByteBuf buf = content.content();
            log.info(buf.toString(UTF_8));
            buf.release();

            String res = "I am OK";

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(res.getBytes("UTF-8"))
            );
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH,response.content().readableBytes());

            if(HttpHeaders.isKeepAlive(request)){
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
            ctx.flush();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error(cause.getMessage());
        ctx.close();
    }
}
