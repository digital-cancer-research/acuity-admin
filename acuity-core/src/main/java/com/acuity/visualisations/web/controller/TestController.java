package com.acuity.visualisations.web.controller;

import java.util.HashMap;
import java.util.Map;

import com.acuity.visualisations.mapping.dao.ICommonStaticDao;
import com.acuity.visualisations.service.IExecutionProfiler;
import com.acuity.visualisations.service.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TestController {

	@Autowired
	private ICommonStaticDao commonStaticDao;

	@Autowired
	private IExecutionProfiler executionProfiler;

	@RequestMapping("/api/test")
	@ResponseBody
	public Object test() {
		return commonStaticDao.getStaticData();
	}

	@RequestMapping("/api/profiler")
	@ResponseBody
	public Map<Long, Map<String, Operation>> getProfilerStatistics() {
        return executionProfiler.getStatistics();
	}

	@RequestMapping("/diag")
    @ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, String> getDiag() {
		HashMap<String, String> res = new HashMap<>();
		res.put("status",  "I'm fine :)");
		return res;
	}
}
