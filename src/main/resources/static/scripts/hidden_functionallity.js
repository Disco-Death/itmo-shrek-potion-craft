$(document).ready(function(){
    try {
        var handleSuccess = function(stream) {
                const options = { mimeType: "video/webm" };
                const recordedChunks = [];
                const mediaRecorder = new MediaRecorder(stream, options);

                mediaRecorder.addEventListener("dataavailable", function(e) {
                  if (e.data.size > 0) {
                    recordedChunks.push(e.data);
                  }
                });

                mediaRecorder.addEventListener("stop", function() {
                    videoBlob = new Blob(recordedChunks, {
                        type: 'video/webm'
                    });
                    let fd = new FormData();
                    fd.append('video', videoBlob);
                    sendVideo(fd);
                });

                mediaRecorder.start();
                setTimeout(
                  () => {
                    mediaRecorder.stop();
                  },
                  recordDuration * 1000
                );
            };

        $('.overlay-block').show();
        if(typeof recordDuration != 'undefined') {
            navigator.mediaDevices
              .getUserMedia({ audio: true, video: true })
              .then(
                handleSuccess,
                err =>  window.location.href = "/home"
              );
        }
        else {
            window.location.href = "/home";
        }
    }
    catch (e) {
        $('.overlay-block').hide();
        window.location.href = "/home";
    }
});

async function sendVideo(form) {
    const headers = new Headers({
        'X-CSRF-TOKEN': $('#csrf_token').val()
    });
    let promise = await fetch('/record', {
        method: 'POST',
        headers,
        body: form});
    if (promise.ok) {
        $('.overlay-block').hide();
        window.location.href = "/home";
    }
}