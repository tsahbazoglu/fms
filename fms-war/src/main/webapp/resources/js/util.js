//FAST http://www.shamasis.net/2009/09/fast-algorithm-to-find-unique-items-in-javascript-array/
Array.prototype.unique = function() {
    var o = {}, i, l = this.length, r = [];
    for(i=0; i<l;i+=1) o[this[i]] = this[i];
    for(i in o) r.push(o[i]);
    return r;
};

//CLASSIC 
Array.prototype.unique =
    function() {
        var a = [];
        var l = this.length;
        for(var i=0; i<l; i++) {
            for(var j=i+1; j<l; j++) {
                // If this[i] is found later in the array
                if (this[i] === this[j])
                    j = ++i;
            }
            a.push(this[i]);
        }
        return a;
    };


// Capitalize
//FIXME buyuk I ları kuçuk i yapıyor hata
String.prototype.capitalize = function(){
    return this.replace( /(^|\s)([a-z])/g , function(m,p1,p2){
        return p1+p2.toUpperCase();
    } );
};

"İBN".toLowerCase().capitalize();
 x.code=x.code.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}


telman = function(x){
    return x.formatMoney(0, ",", ".")
}

Number.prototype.formatMoney = function(c, d, t){
    var n = this, 
    c = isNaN(c = Math.abs(c)) ? 2 : c, 
    d = d == undefined ? "," : d, 
    t = t == undefined ? "." : t, s = n < 0 ? "-" : "", 
    i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", 
    j = (j = i.length) > 3 ? j % 3 : 0;
    result = s;
    result +=(j ? i.substr(0, j) + t : "");
    result +=i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t);
    result +=(c ? d + Math.abs(n - i).toFixed(c).slice(2) : "")
    return result;
};
 
(123456789.12345).formatMoney(2, '.', ',');
 
 function constraint(input){
    input = eval('('+input+')');
    expression = "(input.a1+input.a2==0)?(input.b1==0):(input.b1!=0)";
  
    result =eval('('+expression+')');
 
    expression=expression.replace(/input.a1/g,formatMoney(input.a1, 0, ",", "."));
    expression=expression.replace(/input.a2/g,formatMoney(input.a2, 0, ",", "."));
    expression=expression.replace(/input.b1/g,formatMoney(input.b1, 0, ",", "."));
    
    return {
        result:result,
        expression:expression
    };
}
/****************************************************************************** 
 EXPORT
 ******************************************************************************/
/*
 vim exportDataBankBalanceAbstract.js
 */
db.dataBankBalanceAbstract.find().forEach(function(v){
    print(v.forms+";"+v.period.name+";"+v.operator.name+";"+v.balance.name+";"+v.solo/100+";"+v.consolidated/100)
})
/*
 vim exportDataBankBalanceDetail.js
 */
db.dataBankBalanceDetail.find().forEach(function(v){
    print(v.forms+";"+v.period.name+";"+v.operator.name+";"+v.balance.name+";"+v.solo/100+";"+v.consolidated/100)
})



array = [];
db.common.find({
    forms:"member"
}).sort({
    "name":1
}).forEach(function(v){
    array.push(v)
});
x=0;
array.forEach(function(v){
    pre = "A";
    s = "00000"+x++;
    db.common.update(v,{
        $set:{
            ldapUID:pre+s.substr(s.length-5)
        }
    }
    );
});
