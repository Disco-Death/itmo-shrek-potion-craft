$(document).ready(function(){
 $( ".nav-storage .nav-link" ).click(function() {
    $( ".nav-storage .nav-link" ).removeClass('active');
    $('.tab-storage').hide();
    $(this).addClass('active');
    $('#'+$(this).attr('aria-current')).show();
  });
});
