var searchFilterModule = {
};

searchFilterModule.updateSearchInputText = function(elementID, emptyText){
  var searchInput =  $('#' + elementID);
  var searchVal = searchInput.val();
    if ( !searchVal || $.trim(searchVal).length == 0) {
         searchInput.val(emptyText);
    } else {
         searchInput.val(searchVal);
    }
    searchInput.css('color', 'gray');
};

searchFilterModule.updateSearchInput = function (elementID, emptyText, searchBtnId) {
  var searchInput =  $('#' + elementID);
  searchFilterModule.updateSearchInputText(elementID, emptyText);
  searchInput.focus(function(){
     searchInput.css('color', 'black');
     searchInput.val('');
  });
  searchInput.blur(function(){
     searchInput.css('color', 'gray');
      var searchText = searchInput.val();
      if ( $.trim(searchText) ==  $.trim(emptyText) ||  $.trim(searchText).length == 0) {
          searchInput.val(emptyText);
      }

  });

  searchInput.keydown(function(event){
    if(event.keyCode == 13){
       event.preventDefault();
       $('#' + searchBtnId).trigger('click');
    }
  });
};



