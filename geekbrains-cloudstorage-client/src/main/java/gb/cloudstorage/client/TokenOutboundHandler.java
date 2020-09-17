package gb.cloudstorage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import gb.cloudstorage.utils.messages.AbstractMessage;

public class TokenOutboundHandler extends ChannelOutboundHandlerAdapter {
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        AbstractMessage message = (AbstractMessage) msg;
        message.setToken(Network.getToken());
        ctx.write(message, promise);
    }
}
