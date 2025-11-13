package cn.iocoder.yudao.module.lowcode.integrator.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorEntryEncryptDataVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorEntryEncryptMsgVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorEntrySessionVO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.*;

/**
 * @author leo
 */
@Slf4j
public class IntegratorEncryptUtils {

     private static final int DEFAULT_TTL = 10 * 1000;

    private static final String INTEGRATE_SESSION_PREFIX = "INTEGRATE_SESSION:";

    private static final String INTEGRATE_NONCE_PREFIX = "INTEGRATE_NONCE:";

    private static RedisTemplate<String, Object> getRedisTemplate() {
        return SpringUtils.getBean("redisTemplate");
    }

    private static String getSessionKey(String sessionId) {
        return String.valueOf(getRedisTemplate().opsForValue().get(INTEGRATE_SESSION_PREFIX + sessionId));
    }

    private static void setSessionKey(String sessionId, String sessionKey, Long ttl) {
        getRedisTemplate().opsForValue().set(INTEGRATE_SESSION_PREFIX + sessionId, sessionKey, ttl, TimeUnit.SECONDS);
    }

    private static Boolean getNonce(String nonce) {
        return (Boolean) getRedisTemplate().opsForValue().get(INTEGRATE_NONCE_PREFIX + nonce);
    }

    private static void setNonce(String nonce) {
        getRedisTemplate().opsForValue().set(INTEGRATE_NONCE_PREFIX + nonce, Boolean.TRUE, DEFAULT_TTL, TimeUnit.MILLISECONDS);
    }

    public static String genKey() {
        return Base64.encode(SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded());
    }

    private static IntegratorEntryEncryptMsgVO sign(String key1, String key2, IntegratorEntryEncryptMsgVO msgVO) {
        String signStrBuilder = msgVO.getJson() + "|" + key1 + "|" + key2 + "|" + msgVO.getNonce() + "|" + msgVO.getTimestamp();
        msgVO.setSign(SecureUtil.md5(signStrBuilder));
        return msgVO;
    }

    private static boolean signValid(String key1, String key2, IntegratorEntryEncryptMsgVO msgVO) {
        var tempMsg = sign(key1, key2, BeanUtils.toBean(msgVO, IntegratorEntryEncryptMsgVO.class));
        return tempMsg.getSign().equals(msgVO.getSign());
    }

    private static String encrypt(String key, IntegratorEntryEncryptMsgVO msgVO) {
        String json = JSON.toJSONString(msgVO);
        return SecureUtil.aes(Base64.decode(key)).encryptBase64(json);
    }

    private static IntegratorEntryEncryptMsgVO decrypt(String key, String encryptData) {
        try {
            String json = SecureUtil.aes(Base64.decode(key)).decryptStr(encryptData);
            return JSON.parseObject(json, IntegratorEntryEncryptMsgVO.class);
        } catch (Exception ex) {
            log.error("解密失败", ex);
            throw exception(INTEGRATOR_DECRYPT_ERROR);
        }
    }

    public static IntegratorEntryEncryptMsgVO genMsg(Object obj) {
        var msg = new IntegratorEntryEncryptMsgVO();
        msg.setTimestamp(System.currentTimeMillis());
        msg.setNonce(IdUtil.fastSimpleUUID());
        msg.setJson(JSON.toJSONString(obj));
        return msg;
    }

    public static void validation(String key1, String key2, IntegratorEntryEncryptMsgVO msgVO) {
        if (!signValid(key1, key2, msgVO)) {
            throw exception(INTEGRATOR_SIGN_VALID_ERROR);
        }
        if (msgVO.getTimestamp() == null || System.currentTimeMillis() - msgVO.getTimestamp() > DEFAULT_TTL) {
            throw exception(INTEGRATOR_REQ_EXPIRED_ERROR);
        }
        if (Boolean.TRUE.equals(getNonce(msgVO.getNonce()))) {
            throw exception(INTEGRATOR_REQ_DUPLICATE_ERROR);
        }
        setNonce(msgVO.getNonce());
    }

    public static IntegratorEntryEncryptDataVO toSessionReq(String key, Long ttl) {
        Assert.notNull(key);
        Assert.notNull(ttl);
        // 生成 sessionId 和 sessionKey
        var sessionVO = new IntegratorEntrySessionVO();
        sessionVO.setSessionId(IdUtil.fastUUID());
        sessionVO.setSessionKey(IntegratorEncryptUtils.genKey());
        sessionVO.setSessionTtl(ttl);
        // 返回
        var dataVO = new IntegratorEntryEncryptDataVO();
        dataVO.setSessionId(sessionVO.getSessionId());
        dataVO.setEncryptData(encrypt(key, sign(key, sessionVO.getSessionId(), genMsg(sessionVO))));
        return dataVO;
    }

    public static IntegratorEntrySessionVO toDecryptSession(String key, IntegratorEntryEncryptDataVO dataVO) {
        Assert.notNull(key);
        Assert.notNull(dataVO);
        var msgVO = decrypt(key, dataVO.getEncryptData());
        validation(key, dataVO.getSessionId(), msgVO);
        var sessionVO = JSON.parseObject(msgVO.getJson(), IntegratorEntrySessionVO.class);
        if(sessionVO.getSessionTtl() == null) {
            throw exception(INTEGRATOR_SESSION_TTL_ERROR);
        }
        //保存会话key
        setSessionKey(sessionVO.getSessionId(), sessionVO.getSessionKey(), sessionVO.getSessionTtl());
        return sessionVO;
    }

    public static IntegratorEntryEncryptDataVO toSessionResp(String key, Long ttl) {
        Assert.notNull(key);
        Assert.notNull(ttl);
        //重新生成 sessionId 和 sessionKey
        var sessionVO = new IntegratorEntrySessionVO();
        sessionVO.setSessionId(IdUtil.fastUUID());
        sessionVO.setSessionKey(IntegratorEncryptUtils.genKey());
        sessionVO.setSessionTtl(ttl);
        //保存会话key
        setSessionKey(sessionVO.getSessionId(), sessionVO.getSessionKey(), ttl);
        // 返回
        var dataVO = new IntegratorEntryEncryptDataVO();
        dataVO.setSessionId(sessionVO.getSessionId());
        dataVO.setEncryptData(encrypt(key, sign(key, sessionVO.getSessionId(), genMsg(sessionVO))));
        return dataVO;
    }

    public static IntegratorEntryEncryptDataVO toEncryptObject(String key, String sessionId, Object obj) {
        Assert.notNull(key);
        Assert.notNull(sessionId);
        Assert.notNull(obj);
        //加密
        var sessionKey = getSessionKey(sessionId);
        var dataVO = new IntegratorEntryEncryptDataVO();
        dataVO.setSessionId(sessionId);
        dataVO.setEncryptData(encrypt(sessionKey, sign(key, sessionId, genMsg(obj))));
        return dataVO;
    }

    public static <T> T toDecryptObject(String key, IntegratorEntryEncryptDataVO reqVO, Class<T> clazz) {
        Assert.notNull(key);
        Assert.notNull(reqVO);
        Assert.notNull(reqVO.getSessionId());
        Assert.notNull(clazz);
        //解密
        var sessionKey = getSessionKey(reqVO.getSessionId());
        var msgVO = decrypt(sessionKey, reqVO.getEncryptData());
        validation(key, reqVO.getSessionId(), msgVO);
        return JSON.parseObject(msgVO.getJson(), clazz);
    }

}
