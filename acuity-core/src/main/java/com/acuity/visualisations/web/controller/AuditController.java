package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.mapping.dao.IAuditDao;
import com.acuity.visualisations.mapping.entity.Audit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private IAuditDao auditDao;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Audits {
        private List<Audit> items;
        private int total;
    }

    @Getter
    @Setter
    public static class Page {
        private int pageNum;
        private int pageSize;
        private Audit.Field sortBy;
        private boolean sortReverse;
    }

    @RequestMapping("history")
    public Audits getHistory(@RequestBody Page page) {
        int total = auditDao.getTotalActions();
        List<Audit> items = auditDao.getActions(page.pageSize, (page.pageNum - 1) * page.pageSize, page.sortBy, page.sortReverse);
        return new Audits(items, total);
    }
}
