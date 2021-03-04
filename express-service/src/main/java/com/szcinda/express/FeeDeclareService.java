package com.szcinda.express;

import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryFeeDeclareParams;
import com.szcinda.express.persistence.FeeDeclare;

import java.util.List;

public interface FeeDeclareService {

    PageResult<FeeDeclareDto> query(QueryFeeDeclareParams params);

    void createDeclare(FeeDeclareCreateDto createDto);

    FeeDeclare getById(String id);

    void reApproval(String declareId);

    void passed(String declareId);

    void rejected(FeeDeclareRejectDto rejectDto);

    void modify(FeeDeclareModifyDto reApprovalDto);
}
