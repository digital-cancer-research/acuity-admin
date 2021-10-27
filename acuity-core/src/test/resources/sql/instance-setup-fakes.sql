--projects
insert into map_project_rule (MPR_ID, MPR_DRUG) 
values (1,'D0001', 'drug01');
insert into map_project_rule (MPR_ID, MPR_DRUG) 
values (2,'D0002','drug02');
--studies
insert into map_study_rule (MSR_ID,msr_prj_id,msr_study_code,msr_study_name,msr_phase,msr_randomised,msr_regulatory,msr_blinded)
values (1,1,'D00010001','study1','phase1',1,1,1);
insert into map_study_rule (MSR_ID,msr_prj_id,msr_study_code,msr_study_name,msr_phase,msr_randomised,msr_regulatory,msr_blinded)
values (2,1,'D00010002','study2','phase1',1,1,1);
insert into map_study_rule (MSR_ID,msr_prj_id,msr_study_code,msr_study_name,msr_phase,msr_randomised,msr_regulatory,msr_blinded)
values (3,2,'D00010003','study3','phase1',1,1,1);
insert into map_study_rule (MSR_ID,msr_prj_id,msr_study_code,msr_study_name,msr_phase,msr_randomised,msr_regulatory,msr_blinded)
values (4,2,'D00020001','study4','phase1',1,1,1);
insert into map_study_rule (MSR_ID,msr_prj_id,msr_study_code,msr_study_name,msr_phase,msr_randomised,msr_regulatory,msr_blinded)
values (5,2,'D00020002','study5','phase1',1,1,1);
--project groups
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(1,1,'group1','AE');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(2,1,'group2','AE');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(3,1,'group3','AE');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(4,1,'group4','LAB');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(5,1,'group5','LAB');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(6,2,'group6','AE');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(7,2,'group7','AE');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(8,2,'group8','LAB');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(9,2,'group9','LAB');
insert into map_project_group_rule (MGR_ID,mgr_project_id,mgr_name,mgr_type) values(10,2,'group10','LAB');
--study groups
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_NAME) values(1,1,'group1');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(2,1,'group2');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(3,1,'group3');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(4,2,'group4');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(5,2,'group5');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(6,3,'group6');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(7,3,'group7');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(8,4,'group8');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(9,4,'group9');
insert into map_study_group_rule (MSGR_ID,MSGR_study_id,MSGR_name) values(10,5,'group10');
commit;
