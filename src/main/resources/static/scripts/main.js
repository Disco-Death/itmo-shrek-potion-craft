$(document).ready(function(){
    $( ".btn-disabled" ).click(function(event) {
        event.preventDefault();
        if (typeof errorMessage !== "undefined") {
           alert(errorMessage);
        }
    });
    $( ".nav-storage .nav-link" ).click(function() {
    $( ".nav-storage .nav-link" ).removeClass('active');
        $('.tab-storage').hide();
        $(this).addClass('active');
        $('#'+$(this).attr('aria-current')).show();
    });
    $('#selectEntity').on('change', function() {
        $('.select-entity').hide();
        $('#select'+this.value).show();
    });
    $('.trackStatus').on('change', function() {
        data = {
            name: 'status',
            id: $(this).attr("data-id"),
            value: $(this).is(":checked")
        };
        sendDataToTrack(data);
    });

    $('.trackLength').on('change', function() {
            data = {
                name: 'duration',
                id: $(this).attr("data-id"),
                value: $(this).val()
            };
            sendDataToTrack(data);
        });
});

function sendDataToTrack (data) {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/track/update-settings");
    xhr.setRequestHeader('x-csrf-token', $('#csrf_token').val());
    xhr.send(JSON.stringify(data));
}
