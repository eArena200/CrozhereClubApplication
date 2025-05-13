package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.service.exception.AuthServiceException;

public interface AuthService {
    void initAuth(InitAuthRequest initAuthRequest) throws AuthServiceException;
    VerifyAuthResponse verifyAuth(VerifyAuthRequest verifyAuthRequest) throws AuthServiceException;
}
