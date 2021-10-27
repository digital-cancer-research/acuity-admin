<%--
  ~ Copyright 2021 The University of Manchester
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<script type="text/javascript" src="js/study-setup/step-baseline-drugs.js?1454331106"></script>

<div style="text-align: center">

    <div style="text-align: left; display: inline-block">
        <div class="radio">
            <label>
                <input type="radio" name="baselineDrugsMode" value="any" checked>
                Calculate the baseline value based on the date of first dose of any investigational drug.
            </label>
        </div>
        <div class="radio">
            <label>
                <input type="radio" name="baselineDrugsMode" value="custom">
                Specify how different drugs affect the calculation of baseline.
            </label>
        </div>

        <br/>
        <br/>

        <div id="customBaselineDrugsPane" style="display: none">
            Check to either include or exclude the given drug in calculation of baseline values.
            <br/>
            <br/>

            <div class="contentTable">
                <table id="customBaselineDrugsTable"></table>
            </div>
            <br/>

            <div>
                <input type="button" id="baselinesSaveButton" class="commonButton" style="float:left"
                       value="Save changes" disabled/>
                <input type="button" id="baselinesAddButton" class="commonButton" style="float:left" value="Add drug"/>

                <div style="clear: both;"></div>
            </div>

        </div>
        <br/>


    </div>
</div>
