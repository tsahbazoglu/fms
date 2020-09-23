
function doPost(i, chunk) {
    //$.post("esignContentCollector", {chunk: chunk});
    jQuery('#idMultiAjaxCollectorForm\\:imzCollectorIndex').val(i);
    jQuery('#idMultiAjaxCollectorForm\\:imzCollector').val(chunk);
    jQuery('#idMultiAjaxCollectorForm\\:buttonEimzaCollector').click();
}

function myAjaxRecursivePost(i, n_chunked) {
    if (i < n_chunked.length) {
        //console.log({index: i, length: length, chunk: n_chuked[i]});
        doPost(i, n_chunked[i]);
        i++;
        myAjaxRecursivePost(i, n_chunked);
    }
}

// applet version 1.2.0.CR1
function signDoc() {
    // set applet's public variable
    var imz = muhurdar.signDocURL(jQuery('#imzform\\:sourcedocid').val());
    var result = muhurdar.isStatusOk();
    //alert(result);
    if (result === 'Yes') {
        //document.getElementById('form:sonuc').innerHTML = imz;
        //alert("it is going to be set");
        jQuery('#imzform\\:buttonEimzaOKNew').click();
    }
    document.getElementById('hata').innerHTML = '' + muhurdar.getErrorMessage() + '\n' + muhurdar.getErrorTrace() + '';
}


function signDoc120() {
    var imz = muhurdar.signDoc(jQuery('#imzform\\:sourcedoc').val(), jQuery('#imzform\\:sourcedocid').val());
    if (imz != 'SUCCESS') {
        console.log(imz);
    } else {
        jQuery('#imzform\\:buttonEimzaOKNew').click();
    }
}


function signDocOld() {
// set applet's public variable
//alert("1"+document.getElementById('signresult'));
//alert("2"+document.getElementById('sourcedoc').innerHTML);
//deployJava.runApplet(attributes, parameters, '1.7');  

    //console.log("start signDoc()");
    //console.log({muhurdar: muhurdar});
    //console.log(jQuery('#imzform\\:sourcedoc').val());
    //console.log({muhurdar_isStatusOk: muhurdar.isStatusOk()});

    var imz = muhurdar.signDoc(jQuery('#imzform\\:sourcedoc').val());

    if (muhurdar.isStatusOk() === 'Yes') {


        if (false) {

            //var n_chuked = imz.match(/.{1,1024}/g); // . Find a single character, except newline or line terminator
            //var n_chuked = imz.match(/(.|\r\n|\n){1,1024}/g);
            //var n_chunked = imz.split(/\\n/g);
            //imz = "Hello World!";
            /*
             var imz_clone = "".concat(imz);
             var n_chunked = [];
             var chunksize = 4096;
             
             
             var n_chunked_length = 0;
             
             while (imz_clone.length > 0) {
             var chunk = imz_clone.substring(0, chunksize);
             
             if (chunk[chunk.lenght - 1] == "\\" && imz_clone[chunksize] == "n") {
             console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
             }
             if (chunk[chunk.lenght - 1] == "\\") {
             console.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
             }
             
             n_chunked.push(chunk);
             n_chunked_length += chunk.length;
             imz_clone = imz_clone.substring(chunksize, imz_clone.length);
             }
             
             */
            //myAjaxRecursivePost(0, n_chunked);
            //console.log({chunked_length: n_chunked.length});
        }


        //var my_clone = "".concat(imz).replace(/\n/g, "\r\n");

        //console.log({nochunk_clone_length: my_clone.length});
        //console.log({nochunk_length: imz.length});

        jQuery('#imzform\\:imz').val(imz);
        jQuery('#imzform\\:buttonEimzaOK').click();
    } else {
        console.log(muhurdar.getErrorMessage());
    }
}


/*
 
 http://stackoverflow.com/questions/462348/string-length-differs-from-javascript-to-java-code
 
 */