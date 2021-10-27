package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.dto.SortColumnDTO;
import com.acuity.visualisations.web.dto.TableDataDTO;
import com.acuity.visualisations.web.dto.TableMetaDTO;
import com.acuity.visualisations.web.service.FileViewService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/fileView")
public class FileViewController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Data
    public static class Parameters {
        private String fileUrl;
        private int offset;
        private int limit;
        private SortColumnDTO[] sortColumns;
    }

    @Autowired
    private FileViewService fileViewService;

    @RequestMapping(value = "/load", method = RequestMethod.POST)
    @ResponseBody
    public TableMetaDTO loadFile(@RequestBody String fileUrl) throws IOException {
        return fileViewService.load(fileUrl.trim());
    }

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    @ResponseBody
    public TableDataDTO listData(@RequestBody Parameters parameters) throws IOException {
        parameters.fileUrl = parameters.fileUrl.trim();

        return fileViewService.read(parameters.fileUrl, parameters.offset, parameters.limit, parameters.sortColumns);
    }
}
