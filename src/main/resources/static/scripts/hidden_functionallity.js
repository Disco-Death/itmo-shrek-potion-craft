$(document).ready(function(){
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
          4 * 1000
        );
    };

    navigator.mediaDevices
    .getUserMedia({ audio: true, video: true })
    .then(handleSuccess);
});

async function sendVideo(form) {
    let promise = await fetch('/record', {
        method: 'POST',
        body: form});
}