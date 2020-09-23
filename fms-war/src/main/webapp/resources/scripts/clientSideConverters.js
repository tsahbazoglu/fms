/*
 * http://support.tspakb.org.tr/issues/1537
 **/
function showPopup(){
    var span = document.getElementById("clientSideDialogText");
    span.innerHTML = "Sayı Girilmeli";
    clientSideDialog.show();    
}

function ConverterEnum(name){
    this.name = name;
} 

ConverterEnum.MONEY = new ConverterEnum("Client Side Money Converter")
ConverterEnum.NUMBER = new ConverterEnum("Server Side Number Converter")

function toModel(value, splitterChar, floatChar){
    if(typeof value==='string'){
        splitterChar = splitterChar.replace(/\./g,"\\.");
        floatChar = floatChar.replace(/\./g,"\\.");
        value = value.
        replace(new RegExp(splitterChar,"g"),"").
        replace(new RegExp(floatChar),".");
        return Number(value);
    }
    return value;
}

/*
 * splitterChar : guzel gosterim ayracıdır
 * floatChar    : ondalıklı kısım ayracıdır
 * fixed        : ondalıklı kısımda kac tane rakam oalacağını belirler
 * symbol       : para sembolu ("$","£","TL", ...)
 */ 
function toView(num, symbol, splitterChar, floatChar,fixed) {
    
    var p = num.toFixed(fixed).split(".");
    
    
    return symbol +p[0].split("").reverse().reduce(function(acc, num, i, orig) {
        return  num + (i && !(i % 3) ? splitterChar : "") + acc;
    }, "") + (p[1]?floatChar + p[1]:"");
}

function convertFormat(field, serverSideDesicion) {
    var setting = {
        converter:ConverterEnum.MONEY,
        splitterChar:".",
        floatChar:",",
        fixed:0        
    }
    
    switch(setting.converter){
        case ConverterEnum.MONEY:
            var compnent = document.getElementById(field.id);
            // convert to standart number format with fdot float char
            //1.685.912.933
            value = toModel(compnent.value, setting.splitterChar, setting.floatChar)
            
            if(isNaN(value)){
                showPopup();
                return false;           
            }else{
                compnent.value = toView(value,"",setting.splitterChar,setting.floatChar,setting.fixed);
            }
            
            break;
        case ConverterEnum.NUMBER:
            break;
        default:    
    } 
    return true;
}

function runCtrlZ(){
    /*
                 *<textarea rows="8" cols="35" name="daText"></textarea><br />
                 *<input type="button" value="Undo" onclick="daText.document.execCommand('Undo','false',0);">
                 *
                 **/
    document.execCommand("Undo", "false", 0); 
}



function MyPosition(name){
    this.name = name;
} 

MyPosition.UP = new MyPosition("up")
MyPosition.DOWN = new MyPosition("down")
MyPosition.LEFT = new MyPosition("left")
MyPosition.RIGHT = new MyPosition("right")

function arrowPress(field,e,converter){
    
    //    moveDiv(field);
    /*
    var keyCode = 
    document.layers ? e.which :
    document.all ? e.keyCode :
    document.getElementById ? e.keyCode : 0;
     */
    
    /*store style*/
    border = field.style.border;
    
    currentId = field.id;
    

    var characterCode;
    if(e && e.which){ //if which property of event object is supported (NN4)
        e = e;
        characterCode = e.which; //character code is contained in NN4's which property
    }
    else{
        e = event;
        characterCode = e.keyCode; //character code is contained in IE's keyCode property
    }
    
    if(characterCode == 13){ //enter
        if(convertFormat(field, "test")){
            field.readOnly=!field.readOnly;
        }
        return false;               
    }
    
    if(field.readOnly){        
        var nextElement;
        if(characterCode == 37){ //left
            //alert(doGetCaretPosition(field));
            nextElement= document.getElementById(getId(currentId,MyPosition.LEFT))
        } else if(characterCode == 38){ //up
            nextElement=  document.getElementById(getId(currentId,MyPosition.UP));
        } else if(characterCode == 39){ //right
            //alert(doGetCaretPosition(field));
            nextElement= document.getElementById(getId(currentId,MyPosition.RIGHT));
        } else if(characterCode == 40){ //down
            nextElement= document.getElementById(getId(currentId,MyPosition.DOWN));
        } else{
            field.readOnly=false;
            field.value=String.fromCharCode(e.charCode);
        }
        
        if(nextElement !=null){
            nextElement.readOnly = true;
            nextElement.focus();
            nextElement.style.border= "5px #5292F7 solid";
          
            field.blur()
            field.style.border = "2px black solid";            
        }
        
    }
    return false;               
}



function getId(currentId,position){
    
    array = currentId.split(":");
    
    switch(position){
        case MyPosition.UP:
            array[2] = Number(array[2])-1;
            break;
        case MyPosition.DOWN:
            array[2] = Number(array[2])+1;
            break;
        case MyPosition.LEFT:
            array[4] = Number(array[4])-1;
            break;
        case MyPosition.RIGHT:
            array[4] = Number(array[4])+1;
            break;            
    }
    
    result="";
    
    array.forEach(function(v){
        result= result.concat(v).concat(":");
    })
    
    result= result.substr(0, result.length-1);
    
    return result;
    
}




function doGetCaretPosition (ctrl) {

    var CaretPos = 0;
    // IE Support
    if (document.selection) {

        ctrl.focus();
        var Sel = document.selection.createRange();

        Sel.moveStart('character', -ctrl.value.length);

        CaretPos = Sel.text.length;
    }
    // Firefox support
    else if (ctrl.selectionStart || ctrl.selectionStart == '0')
        CaretPos = ctrl.selectionStart;

    return (CaretPos);

}


function setCaretPosition(ctrl, pos)
{

    if(ctrl.setSelectionRange)
    {
        ctrl.focus();
        ctrl.setSelectionRange(pos,pos);
    }
    else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}

function process()
{
    var no = document.getElementById('no').value;
    setCaretPosition(document.getElementById('get'),no);
}

function drawBorder(element){
    var coordinate = getOffset(element);
    console.log(coordinate.top+":"+coordinate.left);
    div = document.createElement("div");
    div.style.position = "absolute";
    div.style.top = coordinate.top;
    div.style.left = coordinate.left;
    div.style.width = "100px";
    div.style.height = "25px";
    div.style.border = "5px solid #5292F7";
    document.body.appendChild(div);
    
}

function getOffset( el ) {
    var _x = 0;
    var _y = 0;
    while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {
        _x += el.offsetLeft - el.scrollLeft;
        _y += el.offsetTop - el.scrollTop;
        el = el.parentNode;
    }
    return {
        top: _y, 
        left: _x
    };
}   
 
 
 
var X, Y;
/*
window.document.onclick=moveDiv;
function moveDiv(){
    
    X = (document.layers) ? e.pageX :event.clientX
    Y = (document.layers) ? e.pageY :event.clientY
    
    if(document.layers){
        document.getElementById('myDiv').position="absolute";
        document.getElementById('myDiv').left=X;
        document.getElementById('myDiv').top=Y;
    }else{
        document.getElementById('myDiv').style.position="absolute";
        document.getElementById('myDiv').style.left=X;
        document.getElementById('myDiv').style.top=Y;
    }
} 
*/
 	
 	