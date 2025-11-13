package cn.iocoder.yudao.module.lowcode.integrator;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.*;
import cn.iocoder.yudao.module.lowcode.integrator.utils.IntegratorEncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;

/**
 * @author leo
 */
@Slf4j
public class IntegratorSyncClient {

    private final Long tenantId;
    private final RestTemplate restTemplate;
    private final String integrateEntry;
    private final String integrateKey;
    private IntegratorEntrySessionVO sessionVO;

    public IntegratorSyncClient(Long tenantId, String integrateEntry, String integrateKey) {
        this.tenantId = tenantId;
        this.restTemplate = new RestTemplateBuilder().connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10)).build();
        this.integrateEntry = integrateEntry.endsWith("/") ? integrateEntry.substring(0, integrateEntry.length() - 1) : integrateEntry;
        this.integrateKey = integrateKey;
    }

    private IntegratorEntryEncryptDataVO post(String url, IntegratorEntryEncryptDataVO dataVO) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("tenant-id", String.valueOf(tenantId));
        var response = restTemplate.exchange(
                integrateEntry + url,
                HttpMethod.POST,
                new HttpEntity<>(dataVO, headers),
                new ParameterizedTypeReference<CommonResult<IntegratorEntryEncryptDataVO>>() {
                }
        );
        var result = response.getBody();
        if (!Boolean.TRUE.equals(result.isSuccess())) {
            throw exception0(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

    private String getSessionId() {
        Assert.notNull(this.sessionVO, "Must invoke startSession method first");
        return this.sessionVO.getSessionId();
    }

    public IntegratorSyncClient startSession(long ttl) {
        var sessionResp = this.post("/session", IntegratorEncryptUtils.toSessionReq(integrateKey, ttl));
        this.sessionVO = IntegratorEncryptUtils.toDecryptSession(integrateKey, sessionResp);
        return this;
    }

    public IntegratorEntrySyncDataVO pullSyncData(IntegratorEntryPullReqVO reqVO) {
        var pullResp = this.post("/pull", IntegratorEncryptUtils.toEncryptObject(integrateKey, getSessionId(), reqVO));
        return IntegratorEncryptUtils.toDecryptObject(integrateKey, pullResp, IntegratorEntrySyncDataVO.class);
    }

    public IntegratorEntrySyncRespVO pushSyncData(IntegratorEntrySyncDataVO syncDataVO) {
        var pushResp = this.post("/push", IntegratorEncryptUtils.toEncryptObject(integrateKey, getSessionId(), syncDataVO));
        return IntegratorEncryptUtils.toDecryptObject(integrateKey, pushResp, IntegratorEntrySyncRespVO.class);
    }

}
