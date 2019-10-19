package io.bitchat.im.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.client.Client;
import io.bitchat.im.BaseResult;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.protocol.Packet;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author houyi
 */
@Slf4j
public class DefaultBaseFunc implements BaseFunc {

    private Client client;

    public DefaultBaseFunc(Client client) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
    }

    @Override
    public Payload request(Packet packet) {
        CompletableFuture<Packet> future = client.sendRequest(packet);
        Payload payload;
        try {
            payload = future.get(3, TimeUnit.SECONDS).getPayload();
        } catch (Exception e) {
            payload = PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), ResultCode.BIZ_FAIL.getMessage());
            log.error("request error,cause={}", e.getMessage(), e);
        }
        return payload;
    }

    @Override
    public BaseResult transferResult(Payload payload) {
        BaseResult baseResult = new BaseResult();
        baseResult.setSuccess(payload.isSuccess());
        baseResult.setErrorCode(payload.getCode());
        baseResult.setErrorMsg(payload.getMsg());
        return baseResult;
    }
}