CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID NUMBER(19,0) PRIMARY KEY ,
    VERSION NUMBER(19,0),
    JOB_INSTANCE_ID NUMBER(19,0),
    CREATE_TIME TIMESTAMP,
    START_TIME TIMESTAMP DEFAULT NULL,
    END_TIME TIMESTAMP DEFAULT NULL,
    STATUS VARCHAR2(10) ,
    EXIT_CODE VARCHAR2(100) ,
    EXIT_MESSAGE VARCHAR2(2500) ,
    LAST_UPDATED TIMESTAMP,
    FOREIGN KEY (JOB_INSTANCE_ID) REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;