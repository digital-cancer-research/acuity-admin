var CBioPortalGenomicProfileStep = function (studyWizard) {
// cBioPortal profiles
    this.bitmask = {
        MUTATIONS: 1, // 2^0
        CNA_LINEAR: 2, // 2^1
        CNA_DISCRETE: 4, // 2^2
        CNA_GISTIC: 8, // 2^3
        MRNA_U133: 16, // 2^4
        MRNA_U133_ZSCORES: 32, // 2^5
        MRNA_ZSCORES: 64, // 2^6
        MRNA_SEQ: 128, // 2^7
        MRNA_SEQ_ZSCORES: 256, // 2^8
        METHYLATION_HM27: 512, // 2^9
        METHYLATION_HM450: 1024, // 2^10
        PROTEIN: 2048, // 2^11
        PROTEIN_ZSCORES: 4096, // 2^12
        PROTEIN_CPTAC_ZSCORES: 8192 // 2^13
    };

    this.maxMaskValue = Math.max(this.bitmask.MUTATIONS, this.bitmask.CNA_LINEAR, this.bitmask.CNA_DISCRETE, this.bitmask.CNA_GISTIC,
        this.bitmask.MRNA_U133, this.bitmask.MRNA_U133_ZSCORES, this.bitmask.MRNA_ZSCORES, this.bitmask.MRNA_SEQ,
        this.bitmask.MRNA_SEQ_ZSCORES, this.bitmask.METHYLATION_HM27, this.bitmask.METHYLATION_HM450, this.bitmask.PROTEIN,
        this.bitmask.PROTEIN_ZSCORES, this.bitmask.PROTEIN_CPTAC_ZSCORES);

    var scope = this;
    scope.studyWizard = studyWizard;

    this.calculateProfilesMask = function () {
        var mask = 0;
        mask = $('#mutationsProf').prop('checked') ? mask | this.bitmask.MUTATIONS : mask;
        mask = $('#cnaLinProf').prop('checked') ? mask | this.bitmask.CNA_LINEAR : mask;
        mask = $('#cnaDiscProf').prop('checked') ? mask | this.bitmask.CNA_DISCRETE : mask;
        mask = $('#cnaGisticProf').prop('checked') ? mask | this.bitmask.CNA_GISTIC : mask;
        mask = $('#mRNAU133Prof').prop('checked') ? mask | this.bitmask.MRNA_U133 : mask;
        mask = $('#mRNAU133zScoresProf').prop('checked') ? mask | this.bitmask.MRNA_U133_ZSCORES : mask;
        mask = $('#mRNAzScoresProf').prop('checked') ? mask | this.bitmask.MRNA_ZSCORES : mask;
        mask = $('#mRNASeqProf').prop('checked') ? mask | this.bitmask.MRNA_SEQ : mask;
        mask = $('#mRNASeqzScoresProf').prop('checked') ? mask | this.bitmask.MRNA_SEQ_ZSCORES : mask;
        mask = $('#methylationH27Prof').prop('checked') ? mask | this.bitmask.METHYLATION_HM27 : mask;
        mask = $('#methylationH450Prof').prop('checked') ? mask | this.bitmask.METHYLATION_HM450 : mask;
        mask = $('#rppaProf').prop('checked') ? mask | this.bitmask.PROTEIN : mask;
        mask = $('#rppazScoresProf').prop('checked') ? mask | this.bitmask.PROTEIN_ZSCORES : mask;
        mask = $('#rppaCptaczScoresProf').prop('checked') ? mask | this.bitmask.PROTEIN_CPTAC_ZSCORES : mask;

        return mask;
    };

    this.setProfileData = function (profilesMask) {
        var enableCbioPortalConfiguration = (profilesMask && profilesMask > 0);
        $('#cbioPortalEnableStatus').prop('checked', enableCbioPortalConfiguration);
        $('#cbioPortalStudyCode').val((enableCbioPortalConfiguration
            ? scope.study.cbioPortalStudyCode : scope.study.studyCode) || scope.study.studyCode);
        $('#cbioPortalStudyCode').prop('disabled', !enableCbioPortalConfiguration);

        $('#mutationsProf').prop('checked', !!(profilesMask & scope.bitmask.MUTATIONS));
        $('#mutationsProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#cnaLinProf').prop('checked', !!(profilesMask & scope.bitmask.CNA_LINEAR));
        $('#cnaLinProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#cnaDiscProf').prop('checked', !!(profilesMask & scope.bitmask.CNA_DISCRETE));
        $('#cnaDiscProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#cnaGisticProf').prop('checked', !!(profilesMask & scope.bitmask.CNA_GISTIC));
        $('#cnaGisticProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#mRNAU133Prof').prop('checked', !!(profilesMask & scope.bitmask.MRNA_U133));
        $('#mRNAU133Prof').prop('disabled', !enableCbioPortalConfiguration);
        $('#mRNAU133zScoresProf').prop('checked', !!(profilesMask & scope.bitmask.MRNA_U133_ZSCORES));
        $('#mRNAU133zScoresProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#mRNAzScoresProf').prop('checked', !!(profilesMask & scope.bitmask.MRNA_ZSCORES));
        $('#mRNAzScoresProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#mRNASeqProf').prop('checked', !!(profilesMask & scope.bitmask.MRNA_SEQ));
        $('#mRNASeqProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#mRNASeqzScoresProf').prop('checked', !!(profilesMask & scope.bitmask.MRNA_SEQ_ZSCORES));
        $('#mRNASeqzScoresProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#methylationH27Prof').prop('checked', !!(profilesMask & scope.bitmask.METHYLATION_HM27));
        $('#methylationH27Prof').prop('disabled', !enableCbioPortalConfiguration);
        $('#methylationH450Prof').prop('checked', !!(profilesMask & scope.bitmask.METHYLATION_HM450));
        $('#methylationH450Prof').prop('disabled', !enableCbioPortalConfiguration);
        $('#rppaProf').prop('checked', !!(profilesMask & scope.bitmask.PROTEIN));
        $('#rppaProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#rppazScoresProf').prop('checked', !!(profilesMask & scope.bitmask.PROTEIN_ZSCORES));
        $('#rppazScoresProf').prop('disabled', !enableCbioPortalConfiguration);
        $('#rppaCptaczScoresProf').prop('checked', !!(profilesMask & scope.bitmask.PROTEIN_CPTAC_ZSCORES));
        $('#rppaCptaczScoresProf').prop('disabled', !enableCbioPortalConfiguration);
    };

    this.updateStudy = function () {
        var scope = this;
        var sendData = {
            profilesMask: scope.calculateProfilesMask(),
            cbioPortalStudyCode: $('#cbioPortalEnableStatus').is(':checked') ? $('#cbioPortalStudyCode').val() : null
        };
        ajaxModule.sendAjaxRequest('study-setup-cBioPortal-config', JSON.stringify(sendData), {showDialog: false},
            function() {
                scope.study.profilesMask = sendData.profilesMask;
                scope.study.cbioPortalStudyCode = sendData.cbioPortalStudyCode;
            });
    };
};


/* public methods ------*/
CBioPortalGenomicProfileStep.prototype = {

    startStep: function () {
        var scope = this;
        scope.setCurrentStudy(scope.studyWizard.workflow);
        $('#cbioPortalEnableStatus').on('change', function () {
            scope.setProfileData($('#cbioPortalEnableStatus').is(':checked') ? scope.maxMaskValue * 2 : null);

        });
    },

    leaveStep: function () {
        $('cbioPortalEnableStatus').unbind('change');
        this.updateStudy();
        return true;
    },

    setCurrentStudy: function (studyWorkflow) {
        this.study = studyWorkflow.selectedStudy;
        var scope = this;
        this.setProfileData(scope.study.profilesMask);
    }
};