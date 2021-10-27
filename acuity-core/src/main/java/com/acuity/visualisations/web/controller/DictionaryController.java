/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.dao.DictinaryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Search, count and returns distinct values from table column
 * Initially created for RCT-1572 (Autocomplete and check if value exists)
 */

@Controller
@RequestMapping("/api/dict")
public class DictionaryController {

    @Autowired
    private DictinaryDao dao;

    @RequestMapping("/pt/search")
    @ResponseBody
    public List<String> searchPt(@RequestParam(value = "term", required = false) String search) {
        return dao.search(DictinaryDao.Table.result_event_type, DictinaryDao.Field.evt_pt, search);
    }

    @RequestMapping("/pt/count")
    @ResponseBody
    public List<DictinaryDao.TermCount> countPt(@RequestParam(value = "term") List<String> terms) {
        return dao.count(DictinaryDao.Table.result_event_type, DictinaryDao.Field.evt_pt, terms);
    }

    @RequestMapping("/lab_code/search")
    @ResponseBody
    public List<String> searchLabCode(@RequestParam(value = "term", required = false) String search) {
        return dao.search(DictinaryDao.Table.result_laboratory, DictinaryDao.Field.lab_code, search);
    }
    @RequestMapping("/lab_code/count")
    @ResponseBody
    public List<DictinaryDao.TermCount> countLabCode(@RequestParam(value = "term") List<String> terms) {
        return dao.count(DictinaryDao.Table.result_laboratory, DictinaryDao.Field.lab_code, terms);
    }

    @RequestMapping("/lab_code/test_name/search")
    @ResponseBody
    public List<String> searchLabCodeTestName(@RequestParam(value = "term", required = false) String search) {
        return dao.search(DictinaryDao.Table.util_labcode_lookup, DictinaryDao.Field.lcl_test_name, search);
    }

    @RequestMapping("/lab_code/sample_name/search")
    @ResponseBody
    public List<String> searchLabCodeSampleName(@RequestParam(value = "term", required = false) String search) {
        return dao.search(DictinaryDao.Table.util_labcode_lookup, DictinaryDao.Field.lcl_sample_name, search);
    }
}
