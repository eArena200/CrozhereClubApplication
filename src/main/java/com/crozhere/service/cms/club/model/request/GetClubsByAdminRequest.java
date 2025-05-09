package com.crozhere.service.cms.club.model.request;

import com.crozhere.service.cms.club.repository.Club;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetClubsByAdminRequest {
    private String clubAdminId;
}
