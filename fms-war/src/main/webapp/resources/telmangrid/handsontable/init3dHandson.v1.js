var telmangrid = {};

if (!telmangrid.clarity) {
    telmangrid.clarity = {
        init: function (ccid, data, colHeaders, rowHeaders, colRenderers, colWidths, rowHeaderWidth) {

            if (typeof data === "string") {
                data = eval("(" + data + ')');
            }
            if (typeof colHeaders === "string") {
                colHeaders = eval("(" + colHeaders + ')');
            }
            if (typeof rowHeaders === "string") {
                rowHeaders = eval("(" + rowHeaders + ')');
            }
            if (typeof colRenderers === "string") {
                colRenderers = eval("(" + colRenderers + ')');
            }
            if (typeof colWidths === "string") {
                colWidths = eval("(" + colWidths + ')') + 20;
            }
            if (typeof rowHeaderWidth === "string") {
                rowHeaderWidth = eval("(" + rowHeaderWidth + ')') + 80;
            }
            var colCount = colHeaders.length;
            var colWidthsArray = [];
            for (var i = 0; i < colCount; i++) {
                colWidthsArray.push(colWidths);
            }
            var offset = 20;
            var rowHeigth = 24;
            var divWidth = offset + rowHeaderWidth + colCount * colWidths + "px";
            var divHeigth = offset + rowHeigth + rowHeaders.length * rowHeigth + "px";

            /**
             * jsf id include a ":" wich is not recognized by jquery as $("#ccid")
             * A workaround is to use pure js function getElementById(ccid)
             * Retrived js object is converted then to jquery object just like an id
             */
            var jqueryDiv = $(document.getElementById(ccid));
            jqueryDiv.css({
//                "background-color": "blue",
                "width": divWidth,
                "height": divHeigth,
                "overflow": "hidden"
            });

            var container = document.getElementById(ccid);
            var hot = new Handsontable(container, {
                data: data,
                colHeaders: colHeaders,
                rowHeaders: rowHeaders,
                rowHeaderWidth: rowHeaderWidth,
                colWidths: colWidthsArray,
                manualColumnResize: true,
                afterGetRowHeader: function (col, TH) {
                    $(TH).css("text-align", "left");
                },
                afterChange: function (changes) {
                    var jsfData = JSON.stringify(this.getData());
                    var hiddenTextJsonDataToModel = $(document.getElementById("id-tab-view:nd-form:id-hidden-text-json-data-to-model"));
                    hiddenTextJsonDataToModel.val(jsfData);
                },
                cells: function (row, col, prop) {
                    // Conditional formatting
                    // https ://handsontable.com/docs/6.1.1/demo-conditional-formatting.html
                    // press F12 go to console and type Handsontable.renderers
                    var cellRender = colRenderers[row][col];
                    if (cellRender.component === "inputText") {
                        return {
                            renderer: function (instance, td, row, col, prop, value, cellProperties) {
                                // Handsontable.TextCell.renderer.apply(this, arguments); -version 0.10.5
                                Handsontable.renderers.TextRenderer.apply(this, arguments);
                                $(td).css({
                                    "background": cellRender.background,
                                    "text-align": "right"
                                });
                            },
                            readOnly: cellRender.readonly
                        };
                    } else if (cellRender.component === "checkBox") {
                        return {
                            renderer: function (instance, td, row, col, prop, value, cellProperties) {
                                Handsontable.renderers.CheckboxRenderer.apply(this, arguments);
                                $(td).css({
                                    background: cellRender.background,
                                    "text-align": "right"
                                });
                            },
                            readOnly: false//cellRender.readonly
                        };
                    } else {
                        return {
                            renderer: function (instance, td, row, col, prop, value, cellProperties) {
                                Handsontable.renderers.TextRenderer.apply(this, arguments);
                                $(td).css({
                                    "background": "white",
                                    "text-align": "right"
                                });
                            }
                        };
                    }
                }
            });
            $('.handsontable table tbody tr th').css({"text-align": "left"});
            $('.handsontable table tbody tr td').css({"text-align": "right"});
        }
    };
}