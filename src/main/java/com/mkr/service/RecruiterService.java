
package com.mkr.service;

import java.util.List;

import com.mkr.dto.RecruiterResponse;
import com.mkr.dto.RegisterRecruiterRequest;
import com.mkr.enums.RecruiterStatus;

public interface RecruiterService {

    RecruiterResponse registerRecruiter(RegisterRecruiterRequest request);

    RecruiterResponse getRecruiterByUserId(Long userId);

    List<RecruiterResponse> getRecruitersByStatus(RecruiterStatus status);

    RecruiterResponse approveRecruiter(Long userId);

    RecruiterResponse rejectRecruiter(Long userId);
}
