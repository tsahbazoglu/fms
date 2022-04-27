/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function falseAndCloseAll() {
    rcSave();
    hideDialods();
}
function justClose() {
    falsechange();
    hideDialods();
}
function hideDialods() {
    PF("wv_are_you_escape").hide();
    PF('crudOperationDialog2').hide();
}
function falsechange() {
    $("#id-status-data-change").val("false");
}
function truechange() {
    $("#id-status-data-change").val("true");
}
function openAnotherPopup() {
    if ($("#id-status-data-change").val() == "true") {
        PF('wv_are_you_escape').show();
    } else {
        PF('crudOperationDialog2').hide();
    }
    return false;
}

 