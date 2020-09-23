/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//<h:body onbeforeunload="ConfirmClose()" onunload="HandleOnClose()">



PrimeFaces.locales['tr'] = {
    closeText: 'kapat',
    prevText: 'geri',
    nextText: 'ileri',
    currentText: 'bugün',
    monthNames: ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran', 'Temmuz', 'Ağustos', 'Eylül', 'Ekim', 'Kasım', 'Aralık'],
    monthNamesShort: ['Oca', 'Şub', 'Mar', 'Nis', 'May', 'Haz', 'Tem', 'Ağu', 'Eyl', 'Eki', 'Kas', 'Ara'],
    dayNames: ['Pazar', 'Pazartesi', 'Salı', 'Çarşamba', 'Perşembe', 'Cuma', 'Cumartesi'],
    dayNamesShort: ['Pz', 'Pt', 'Sa', 'Ça', 'Pe', 'Cu', 'Ct'],
    dayNamesMin: ['Pz', 'Pt', 'Sa', 'Ça', 'Pe', 'Cu', 'Ct'],
    weekHeader: 'Hf',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Zaman Seçiniz',
    timeText: 'Zaman',
    hourText: 'Saat',
    minuteText: 'Dakika',
    secondText: 'Saniye',
    ampm: false,
    month: 'Ay',
    week: 'Hafta',
    day: 'Gün',
    allDayText: 'Tüm Gün'
};

var myclose = false;

function ConfirmClose()
{
    if (event.clientY < 0)
    {
        event.returnValue = 'You have closed the browser. Do you want to logout from your application?';
        setTimeout('myclose=false', 10);
        myclose = true;
    }
}

function HandleOnClose()
{
    if (myclose == true)
    {
        //the url of your logout page which invalidate session on logout 
        location.replace('/contextpath/j_spring_security_logout');
    }
}

function myPop() {
    this.square = null;
    this.overdiv = null;

    this.popOut = function(msgtxt) {
        //filter:alpha(opacity=25);-moz-opacity:.25;opacity:.25;
        this.overdiv = document.createElement("div");
        this.overdiv.className = "overdiv";

        this.square = document.createElement("div");
        this.square.className = "square";
        this.square.Code = this;
        var msg = document.createElement("div");
        msg.className = "msg";
        msg.innerHTML = msgtxt;
        this.square.appendChild(msg);
        var closebtn = document.createElement("button");
        closebtn.onclick = function() {
            this.parentNode.Code.popIn();
        }
        closebtn.innerHTML = "Tamam";
        this.square.appendChild(closebtn);

        document.body.appendChild(this.overdiv);
        document.body.appendChild(this.square);
    }
    this.popIn = function() {
        if (this.square != null) {
            document.body.removeChild(this.square);
            this.square = null;
        }
        if (this.overdiv != null) {
            document.body.removeChild(this.overdiv);
            this.overdiv = null;
        }
        location.reload(true);
    }
}


function loginOnEnterKeyPress(e) {

    var characterCode;
    if (e && e.which) { //if which property of event object is supported (NN4)
        e = e;
        characterCode = e.which; //character code is contained in NN4's which property
    }
    else {
        e = event;
        characterCode = e.keyCode; //character code is contained in IE's keyCode property
    }

    if (characterCode == 13) { //if generated character code is equal to ascii 13 (if enter key)
        login(1000);
        return false;
    }
    else {
        return true;
    }
}

function login(millis) {
    document.getElementById('loginForm:idLoginSubmitButton').click(); //submit the form
    //WE DONT BIND THE submitJaasForm to the 'loginForm:idLoginSubmitButton' onclick.
    //BECAUSE WE WANT TO RUN LoginMB login actionevent function before submit jaas form
    var date = new Date();
    var curDate = null;

    do {
        curDate = new Date();
    }
    while (curDate - date < millis);

    submitJaasForm();
}

function submitJaasForm() {
    var ice_username = document.getElementById('loginForm:username').value;
    var ice_password = document.getElementById('loginForm:password').value;
    var username = document.getElementById('idJaasUsername');
    var password = document.getElementById('idJaasPassword');
    username.setAttribute("value", ice_username);
    password.setAttribute("value", ice_password);

    document.getElementById("idJaasForm").submit();
}

function disableMe(componentId) {
    var component = document.getElementById(componentId);
    //component.disabled=true;
    component.readOnly = true;
//component.style.backgroundColor="fdeddf";

}

function submitContentForm() {
    document.getElementById('iceformContent:idSaveButton').click(); //submit the form
}

function submitContentFormAndSearch() {
    document.getElementById('iceformContent:idSearchButton').click(); //submit the form
}



function toggleMenuDiv() {
    var menuDiv = document.getElementById("menuDiv");
    var contentDiv = document.getElementById("contentDiv");
    var menuVerticalColorDiv = document.getElementById("idMenuVerticalColorDiv");

    if (menuDiv.style.display == "block") {
        menuDiv.style.display = "none";
        menuVerticalColorDiv.style.left = "0px";
        //menuVerticalColorDiv.innerHTML="göster";
        contentDiv.style.position = "absolute";
        contentDiv.style.left = "5px";
        contentDiv.style.top = "65px";
        contentDiv.style["margin-top"] = "0px";
        contentDiv.style["margin-left"] = "";
    } else {
        menuDiv.style.display = "block";
        menuVerticalColorDiv.style.left = "250px";
        //menuVerticalColorDiv.innerHTML="gizle";
        contentDiv.style.position = "fixed";
        contentDiv.style.left = "";
        contentDiv.style["top"] = "65px";
        contentDiv.style["left"] = "255px";
    }
}

function toggleFunktionsDiv() {
    var functionsDiv = document.getElementById("functionsDiv");
    var contentDiv = document.getElementById("contentDiv");
    var tooggleDiv = document.getElementById("tooggleDiv");
    var functionsColorDiv = document.getElementById("idFunctionsColorDiv");

    if (functionsDiv.style.display == "block") {
        functionsDiv.style.display = "none";
        functionsColorDiv.style.right = "0px";
        tooggleDiv.style.right = "5px";
        tooggleDiv.innerHTML = "fonksiyonları göster"
    } else {
        functionsDiv.style.display = "block";
        functionsColorDiv.style.right = "200px";
        tooggleDiv.style.right = "200px";
        tooggleDiv.innerHTML = "fonksiyonları gizle"
    }
}
     